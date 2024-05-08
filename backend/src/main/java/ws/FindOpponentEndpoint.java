package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonObject;

import common.dto.UserPasswordDto;
import common.socket.SocketMessage;
import common.socket.SocketMessageDto;
import common.socket.MessageDecoder;
import common.socket.MessageEncoder;
import modules.game_chesslib.service.GameService;
import modules.game_chesslib.GameStore;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.chessgame.GameHuman;
import modules.game_chesslib.dto.GameDto;
import modules.game_chesslib.dto.RuleSetDto;
import shared.session.SessionKey;
import shared.session.SimpleSessionManager;

@ServerEndpoint(value = "/find-opponent", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class FindOpponentEndpoint {
    static ArrayList<Set<Session>> group = new ArrayList<>();

    static {
        for (Rank _ : Rank.values()) {
            group.add(ConcurrentHashMap.newKeySet());
        }
    }

    private final GameService gameService = new GameService();

    static ReentrantLock lock = new ReentrantLock();

    @OnOpen
    public void onOpen(Session userSession) throws IOException {
        lock.lock();
        try {
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
            int elo = userPasswordDto.getElo();
            Rank rank = getRank(elo);
            userSession.getUserProperties().put("player_id", userPasswordDto.getId());
            userSession.getUserProperties().put("rank", rank);
            Set<Session> set = group.get(rank.getValue());
            if (set.contains(userSession)) {
                userSession.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.FINDING));
                return;
            }
            if (set.isEmpty()) {
                set.add(userSession);
                userSession.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.FINDING));
            } else {
                Iterator<Session> iterator = set.iterator();
                Session opponentSession = null;
                try {
                    do {
                        opponentSession = iterator.next();
                    } while ((int) opponentSession.getUserProperties().get("player_id") == userPasswordDto.getId());
                } catch (NoSuchElementException ex) {
                    userSession.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.FINDING));
                    return;
                }
                set.remove(opponentSession);
                matchingGame(userSession, opponentSession);
            }
        } finally {
            lock.unlock();
        }
    }

    @OnClose
    public void handleClose(Session session, CloseReason closeReason) {
        removeSession(session);
    }

    @OnError
    public void handleError(Throwable t) {

    }

    static Rank getRank(int elo) {
        if (elo < 2000) {
            return Rank.MEDIUM;
        } else if (elo < 2200) {
            return Rank.HIGH;
        } else if (elo < 2400) {
            return Rank.MASTER;
        } else if (elo < 2600) {
            return Rank.INTERNATIONAL_MASTER;
        } else {
            return Rank.GRAND_MASTER;
        }
    }

    static void removeSession(Session targetSession) {
        Rank rank = (Rank) targetSession.getUserProperties().get("rank");
        if (rank == null) {
            return;
        }
        java.util.Iterator<Session> iterator = group.get(rank.getValue()).iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            if (session.equals(targetSession)) {
                iterator.remove();
            }
        }
    }

    private void matchingGame(Session player1, Session player2) throws IOException {
        Random random = new Random();
        int randomNumber = random.nextInt(2);

        try {
            String gameId;
            if (randomNumber == 0) {
                gameId = gameService.createGame((int) player2.getUserProperties().get("player_id"),
                        (int) player1.getUserProperties().get("player_id"));
            } else {
                gameId = gameService.createGame((int) player1.getUserProperties().get("player_id"),
                        (int) player2.getUserProperties().get("player_id"));
            }
            GameDto gameDto = gameService.getById(gameId);
            RuleSetDto ruleSetDto = gameDto.getRuleSetDto();
            JsonObject rulesetDetail = ruleSetDto.getDetail();
            GameRule gameRule = new GameRule(ruleSetDto.getId(), ruleSetDto.getName(),
                    rulesetDetail.get("minute_per_turn").getAsDouble(),
                    rulesetDetail.get("total_minute_per_player").getAsDouble(),
                    rulesetDetail.get("turn_around_steps").getAsInt(),
                    rulesetDetail.get("turn_around_time_plus").getAsDouble());
            GameHuman newChessGame = new GameHuman(gameDto.getId(), gameDto.getWhiteId(),
                    gameDto.getBlackId(), gameRule);
            GameStore.getInstance().addGameHuman(newChessGame);
            player1.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.GAME_CREATED, gameId));
            player2.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.GAME_CREATED, gameId));
        } catch (Exception e) {
            player1.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.ERROR));
            player2.getAsyncRemote().sendObject(new SocketMessageDto(SocketMessage.ERROR));
        }
        player1.close();
        player2.close();
    }
}

enum Rank {
    MEDIUM(0), HIGH(1), MASTER(2), INTERNATIONAL_MASTER(3), GRAND_MASTER(4);

    private final int value;

    Rank(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
