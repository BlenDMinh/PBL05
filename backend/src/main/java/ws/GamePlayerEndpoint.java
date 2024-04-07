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

import modules.auth.dto.UserPasswordDto;
import modules.game.custom.ChessGame;
import modules.game.dto.GameDto;
import modules.game.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint("/game-player/{id}")
public class GamePlayerEndpoint {
    static Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    Session player1Session, player2Session;
    private ChessGame chessGame;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") Integer id) throws SQLException, Exception {
        GameDto gameDto = gameService.getById(id);
        boolean isValidGame = gameService.isValidGame(gameDto);
        if (!isValidGame) {
            session.getAsyncRemote().sendText("Game not valid");
            session.close();
        } else if (chessGame == null) {
            chessGame = new ChessGame(id);
        }
    }

    @OnMessage
    public void onMessage(String message, Session playerSession) throws IOException {
        String playerId = (String) playerSession.getUserProperties().get("playerId");
        if (playerId == null) {
            stores.session.Session session = SimpleSessionManager.getInstance().getSession(message);
            UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
            playerSession.getUserProperties().put("playerId", userPasswordDto.getId());
        } else {

        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (chessGame != null)
            logger.error("GameId: " + chessGame.getId() + " " + throwable.getMessage());
    }
}
