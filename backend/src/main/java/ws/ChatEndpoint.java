package ws;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import modules.auth.dto.UserPasswordDto;
import modules.chat.MessageDecoder;
import modules.chat.MessageEncoder;
import modules.chat.ServerMessage;
import modules.chat.dto.MessageRequestDto;
import modules.chat.dto.MessageResponseDto;
import modules.chat.service.ChatService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint(value = "/chat", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatEndpoint {
    static Map<Integer, HashSet<Session>> users = new ConcurrentHashMap<>();
    static Map<Integer, ReentrantLock> lockers = new ConcurrentHashMap<>();
    private ChatService chatService = new ChatService();

    @OnOpen
    public void onOpen(Session userSession) throws IOException {
        String queryString = userSession.getQueryString();
        if (!queryString.startsWith("sid=")) {
            userSession.close();
        }
        String sessionId = queryString.substring("sid=".length());
        stores.session.Session session = SimpleSessionManager.getInstance()
                .getSession(sessionId);
        if (session == null) {
            userSession.getBasicRemote().sendText(ServerMessage.SESSION_NOT_VALID);
            userSession.close();
            return;
        }
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        HashSet<Session> userSessions = new HashSet<>();
        if (users.containsKey(userPasswordDto.getId())) {
            userSessions = users.get(userPasswordDto.getId());
        }
        userSessions.add(userSession);
        users.put(userPasswordDto.getId(), userSessions);
        userSession.getUserProperties().put("user_id", userPasswordDto.getId());
        if (!lockers.containsKey(userPasswordDto.getId())) {
            lockers.put(userPasswordDto.getId(), new ReentrantLock());
        }
    }

    @OnMessage
    public void handleMessage(MessageRequestDto messageRequestDto, Session senderSession) throws IOException {
        int receiverId = messageRequestDto.getReceiverId();
        int senderId = (int) senderSession.getUserProperties().get("user_id");
        HashSet<Session> receiverSessions = users.containsKey(receiverId) ? users.get(receiverId) : null;
        HashSet<Session> senderSessions = users.get(senderId);
        ReentrantLock re = lockers.get(receiverId);
        re.lock();
        try {
            MessageResponseDto messageResponseDto = chatService.addMessage(messageRequestDto.getContent(),
                    messageRequestDto.getSenderId(), messageRequestDto.getReceiverId());
            sendToUsers(senderSessions, receiverSessions, messageResponseDto);
        } catch (Exception ex) {

        } finally {
            re.unlock();
        }

    }

    void sendToUsers(HashSet<Session> senderSessions, HashSet<Session> receiverSessions,
            MessageResponseDto messageResponseDto) {
        if (senderSessions != null) {
            for (Session senderSession : senderSessions) {
                senderSession.getAsyncRemote().sendObject(messageResponseDto);
            }
        }
        if (receiverSessions != null) {
            for (Session receiverSession : receiverSessions) {
                receiverSession.getAsyncRemote().sendObject(messageResponseDto);
            }
        }
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        Integer userId = (Integer) session.getUserProperties().get("user_id");
        if (userId != null) {
            HashSet<Session> userSessions = users.get(userId);
            for (Session userSession : userSessions) {
                userSessions.remove(userSession);
                if (userSessions.isEmpty()) {
                    lockers.remove(userId);
                }
            }
        }
    }
}
