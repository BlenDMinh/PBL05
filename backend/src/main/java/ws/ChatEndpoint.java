package ws;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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
    static Map<Integer, Session> users = new ConcurrentHashMap<>();
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
        users.put(userPasswordDto.getId(), userSession);
        userSession.getUserProperties().put("user_id", userPasswordDto.getId());
        lockers.put(userPasswordDto.getId(), new ReentrantLock());
    }

    @OnMessage
    public void handleMessage(MessageRequestDto messageRequestDto, Session senderSession) throws IOException {
        int receiverId = messageRequestDto.getReceiverId();
        Session receiverSession = users.get(receiverId);
        ReentrantLock re = lockers.get(receiverId);
        boolean done = false;
        while (!done) {
            // Getting Outer Lock
            boolean ans = re.tryLock();

            // Returns True if lock is free
            if (ans) {
                re.lock();
                try {
                    MessageResponseDto messageResponseDto = chatService.addMessage(messageRequestDto.getContent(),
                            messageRequestDto.getSenderId(), messageRequestDto.getReceiverId());
                    sendTo2Users(senderSession, receiverSession, messageResponseDto);
                    done = true;
                } catch (Exception ex) {

                } finally {
                    re.unlock();
                }
            }
        }
    }

    void sendTo2Users(Session senderSession, Session receiverSession, MessageResponseDto messageResponseDto) {
        senderSession.getAsyncRemote().sendObject(messageResponseDto);
        receiverSession.getAsyncRemote().sendObject(messageResponseDto);
    }
}
