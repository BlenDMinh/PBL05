package ws;

import java.io.IOException;
import java.sql.SQLException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

import modules.auth.dto.UserPasswordDto;
import modules.game.custom.ChessGame;
import modules.game.custom.GameRule;
import modules.game.dto.GameDto;
import modules.game.dto.RuleSetDto;
import modules.game.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint("/game-player/{id}")
public class GamePlayerEndpoint {
    private final Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    static Session player1Session, player2Session;
    private ChessGame chessGame;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") Integer id) throws SQLException, Exception {
        GameDto gameDto = gameService.getById(id);
        boolean isValidGame = gameService.isValidGame(gameDto);
        if (!isValidGame) {
            session.getAsyncRemote().sendText("Game not valid");
            session.close();
        } else if (chessGame == null) {
            chessGame = new ChessGame(gameDto.getId(), gameDto.getPlayer1Id(), gameDto.getPlayer2Id());
            RuleSetDto ruleSetDto = gameDto.getRuleSetDto();
            JsonObject rulesetDetail = ruleSetDto.getDetail();
            logger.info(rulesetDetail.toString());
            GameRule gameRule = new GameRule(ruleSetDto.getId(), ruleSetDto.getName(),
                    rulesetDetail.get("minute_per_turn").getAsInt(),
                    rulesetDetail.get("total_minute_per_player").getAsInt(),
                    rulesetDetail.get("turn_around_steps").getAsInt(),
                    rulesetDetail.get("turn_around_time_plus").getAsInt());
            chessGame.setGameRule(gameRule);
        }
    }

    @OnMessage
    public void onMessage(String message, Session playerSession) throws IOException {
        Integer playerId = (Integer) playerSession.getUserProperties().get("player_id");
        if (playerId == null) {
            stores.session.Session session = SimpleSessionManager.getInstance().getSession(message);
            if (session == null) {
                playerSession.getAsyncRemote().sendText("Session not valid");
                playerSession.close();
            }
            UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
            int userId = userPasswordDto.getId();
            if (chessGame.getPlayer1().getId() == userId) {
                playerSession.getUserProperties().put("player_id", userId);
                player1Session = playerSession;
            } else if (chessGame.getPlayer2().getId() == userId) {
                playerSession.getUserProperties().put("player_id", userId);
                player2Session = playerSession;
            } else {
                playerSession.close();
            }
        } else if (player1Session == null || player2Session == null) {
            playerSession.getAsyncRemote().sendText("Waiting for opponent");
        } else {
            handleGame(message, playerSession);
        }
    }

    void handleGame(String message, Session playerSession) {
        int playerMoved = chessGame.isPlayer1Turn() ? (int) player1Session.getUserProperties().get("player_id")
                : (int) player2Session.getUserProperties().get("player_id");
        String t = "Player" + playerMoved + " has moved";
        chessGame.nextTurn();
        player1Session.getAsyncRemote().sendText(t);
        player2Session.getAsyncRemote().sendText(t);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (chessGame != null)
            logger.error("GameId: " + chessGame.getId() + " " + throwable.getMessage());
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        if (player1Session != null && player1Session.isOpen()) {
            player1Session.close();
        }
        if (player2Session != null && player2Session.isOpen()) {
            player2Session.close();
        }
    }

}
