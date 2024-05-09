package ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import common.dto.UserPasswordDto;
import common.socket.MessageDecoder;
import common.socket.MessageEncoder;
import common.socket.SocketMessage;
import common.socket.SocketMessageDto;
import modules.game_chesslib.GameStore;
import modules.game_chesslib.InvitationStore;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.GameSide;
import modules.game_chesslib.custom.Invitation;
import modules.game_chesslib.custom.chessgame.GameHuman;
import modules.game_chesslib.dto.GameDto;
import modules.game_chesslib.service.GameService;
import modules.game_chesslib.socket.InviteToGameRequest;
import modules.game_chesslib.socket.InviteToGameResponse;
import shared.session.SessionKey;
import shared.session.SimpleSessionManager;

@ServerEndpoint(value = "/general", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GeneralEndpoint {
    static Map<Integer, Set<Session>> map = new HashMap<>();
    final Gson gson = new GsonBuilder().registerTypeAdapter(GameSide.class, new GameSideDeserializer())
            .create();

    GameService gameService = new GameService();

    @OnOpen
    public void onOpen(Session userSession) throws IOException {
        String queryString = userSession.getQueryString();
        if (!queryString.startsWith("sid=")) {
            userSession.close();
        }
        String sessionId = queryString.substring("sid=".length());
        shared.session.Session session = SimpleSessionManager.getInstance()
                .getSession(sessionId);
        if (session == null) {
            userSession.getBasicRemote().sendText(SocketMessage.SESSION_NOT_VALID);
            userSession.close();
            return;
        }
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        if (!map.containsKey(userPasswordDto.getId())) {
            map.put(userPasswordDto.getId(), ConcurrentHashMap.newKeySet());
        }
        userSession.getUserProperties().put("user_id", userPasswordDto.getId());
        map.get(userPasswordDto.getId()).add(userSession);
    }

    @OnMessage
    public void onMessage(SocketMessageDto socketMessageDto, Session userSession) throws IOException, EncodeException {
        String message = socketMessageDto.getMessage();
        int userId = (int) userSession.getUserProperties().get("user_id");
        switch (message) {
            case SocketMessage.INVITE_TO_GAME_REQUEST:
                InviteToGameRequest inviteToGameRequest = gson.fromJson(gson.toJson(socketMessageDto.getData()),
                        InviteToGameRequest.class);
                if (map.containsKey(inviteToGameRequest.getOpponentId())) {
                    Invitation invitation = new Invitation(userSession,
                            inviteToGameRequest.getGameRule(), inviteToGameRequest.getMeSide(),
                            inviteToGameRequest.isRated());
                    InvitationStore.getInstance().addInvitation(invitation);
                    InviteToGameRequest inviteToGameRequest2 = new InviteToGameRequest(userId,
                            inviteToGameRequest.getGameRule(), inviteToGameRequest.revertSide(),
                            inviteToGameRequest.isRated());
                    inviteToGameRequest2.setInvitationId(invitation.getId());
                    for (Session session : map.get(inviteToGameRequest.getOpponentId())) {
                        session.getBasicRemote()
                                .sendObject(new SocketMessageDto(SocketMessage.INVITE_TO_GAME_REQUEST,
                                        inviteToGameRequest2));
                    }
                }
                break;
            case SocketMessage.INVITE_TO_GAME_RESPONSE:
                InviteToGameResponse inviteToGameResponse = gson.fromJson(socketMessageDto.getData().toString(),
                        InviteToGameResponse.class);
                if (!inviteToGameResponse.isAccept()) {
                    try {
                        Session from = InvitationStore.getInstance()
                                .getInvitationById(inviteToGameResponse.getInvitationId()).getFrom();
                        from.getBasicRemote().sendObject(new SocketMessageDto(SocketMessage.INVITATION_REJECTED));
                    } catch (Throwable throwable) {
                    }
                } else {
                    Invitation invitation = InvitationStore.getInstance()
                            .getInvitationById(inviteToGameResponse.getInvitationId());

                    InvitationStore.getInstance().removeInvitation(invitation.getId());
                    boolean isFromWhite = false;
                    switch (invitation.getFromSide()) {
                        case WHITE:
                            isFromWhite = true;
                            break;
                        case BLACK:
                            isFromWhite = false;
                        default:
                            Random random = new Random();
                            int randomNumber = random.nextInt(2);
                            if (randomNumber == 0) {
                                isFromWhite = true;
                            }
                            break;
                    }
                    try {
                        String gameId;
                        Session player1 = invitation.getFrom(), player2 = userSession;
                        int rulesetId = invitation.getGameRule().getId();
                        if (!isFromWhite) {
                            gameId = gameService.createGame((int) player2.getUserProperties().get("user_id"),
                                    (int) player1.getUserProperties().get("user_id"), rulesetId);
                        } else {
                            gameId = gameService.createGame((int) player1.getUserProperties().get("user_id"),
                                    (int) player2.getUserProperties().get("user_id"), rulesetId);
                        }
                        GameDto gameDto = gameService.getById(gameId);
                        GameRule gameRule = invitation.getGameRule();
                        GameHuman newChessGame = new GameHuman(gameDto.getId(), gameDto.getWhiteId(),
                                gameDto.getBlackId(), gameRule);
                        GameStore.getInstance().addGameHuman(newChessGame);
                        player1.getBasicRemote().sendObject(new SocketMessageDto(SocketMessage.GAME_CREATED, gameId));
                        player2.getBasicRemote().sendObject(new SocketMessageDto(SocketMessage.GAME_CREATED, gameId));
                    } catch (Exception e) {
                    }
                }
                break;
            default:
                break;
        }
    }

    @OnClose
    public void handleClose(Session session, CloseReason closeReason) {
        map.get(session.getUserProperties().get("user_id")).remove(session);
    }

}

class GameSideDeserializer implements JsonDeserializer<GameSide> {
    @Override
    public GameSide deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String stringValue = json.getAsString().toUpperCase(); // Convert the string value to uppercase
        try {
            return GameSide.valueOf(stringValue); // Attempt to match the uppercase string to enum constant
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid GameSide value: " + stringValue);
        }
    }
}