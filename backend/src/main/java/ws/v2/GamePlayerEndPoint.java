package ws.v2;

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
import org.modelmapper.ModelMapper;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import modules.game_chesslib.common.GameMessage;
import modules.game_chesslib.common.GameMessageDto;
import modules.game_chesslib.common.MessageDecoder;
import modules.game_chesslib.common.MessageEncoder;
import modules.game_chesslib.common.nested.MoveResponse;
import modules.game_chesslib.common.nested.PlayerJoinedResponse;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.player.GamePlayer;
import modules.game_chesslib.dto.GameDto;
import modules.game_chesslib.dto.GamePlayerDto;
import modules.game_chesslib.dto.RuleSetDto;
import modules.game_chesslib.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;
import modules.auth.dto.UserPasswordDto;
import modules.game_chesslib.GamePlayerStore;
import modules.game_chesslib.custom.ChessGame;

@ServerEndpoint(value = "/v2/game-player/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GamePlayerEndPoint {
    private final Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();
    static Session player1Session, player2Session;
    private ChessGame chessGame;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) throws SQLException, Exception {
        if (GamePlayerStore.getInstance().isGameExist(id)) {
            chessGame = GamePlayerStore.getInstance().getGameById(id);
        }
        GameDto gameDto = gameService.getById(id);
        boolean isValidGame = gameService.isValidGame(gameDto);
        if (!isValidGame || (player1Session != null && player2Session != null)) {
            session.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
            session.close();
        } else if (chessGame == null) {
            ChessGame newChessGame = new ChessGame(id, gameDto.getPlayer1Id(), gameDto.getPlayer2Id());
            RuleSetDto ruleSetDto = gameDto.getRuleSetDto();
            JsonObject rulesetDetail = ruleSetDto.getDetail();
            GameRule gameRule = new GameRule(ruleSetDto.getId(), ruleSetDto.getName(),
                    rulesetDetail.get("minute_per_turn").getAsInt(),
                    rulesetDetail.get("total_minute_per_player").getAsInt(),
                    rulesetDetail.get("turn_around_steps").getAsInt(),
                    rulesetDetail.get("turn_around_time_plus").getAsInt());
            newChessGame.setGameRule(gameRule);
            GamePlayerStore.getInstance().addGame(newChessGame);
            chessGame = newChessGame;
        }
    }

    @OnMessage
    public void onMessage(GameMessageDto gameMessageDto, Session playerSession) throws IOException {
        String message = gameMessageDto.getMessage();
        if (!message.equals(GameMessage.JSESSIONID)) {
            Integer playerId = (Integer) playerSession.getUserProperties().get("player_id");
            if (playerId == null) {
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.NOT_AUTHENTICATED));
                return;
            }
        }
        switch (message) {
            case GameMessage.JSESSIONID:
                Integer playerId = (Integer) playerSession.getUserProperties().get("player_id");
                if (playerId == null) {
                    stores.session.Session session = SimpleSessionManager.getInstance()
                            .getSession(gameMessageDto.getData().toString());
                    if (session == null) {
                        playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.SESSION_NOT_VALID));
                        playerSession.close();
                    }
                    UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    int userId = userPasswordDto.getId();
                    ModelMapper modelMapper = new ModelMapper();

                    if (chessGame.getPlayer1().getId() == userId) {
                        playerSession.getUserProperties().put("player_id", userId);
                        player1Session = playerSession;
                        GamePlayerDto gamePlayerDto = modelMapper.map(userPasswordDto,
                                GamePlayerDto.class);
                        gamePlayerDto.setWhite(chessGame.getPlayer1().isWhite());
                        GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                                new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                        chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                        gamePlayerDto));
                        sendToAllPlayer(resp);
                    } else if (chessGame.getPlayer2().getId() == userId) {
                        playerSession.getUserProperties().put("player_id", userId);
                        player2Session = playerSession;
                        GamePlayerDto gamePlayerDto = modelMapper.map(userPasswordDto,
                                GamePlayerDto.class);
                        gamePlayerDto.setWhite(chessGame.getPlayer2().isWhite());
                        GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                                new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                        chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                        gamePlayerDto));
                        sendToAllPlayer(resp);
                    } else {
                        playerSession.close();
                    }
                }
                break;

            case GameMessage.MOVE:
            modules.game_chesslib.custom.Move move = gson.fromJson(gameMessageDto.getData().toString(), modules.game_chesslib.custom.Move.class);
                if (isRightTurn(playerSession)) {
                    Square from = Square.valueOf(move.getFrom().toUpperCase());
                    Square to = Square.valueOf(move.getTo().toUpperCase());
                    Move chessMove = new Move(from, to);
                    boolean validMove = chessGame.getBoard()
                            .isMoveLegal(
                                    chessMove,
                                    true);
                    if (validMove) {
                        chessGame.getBoard().doMove(chessMove);
                        sendToAllPlayer(
                                new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                        chessGame.getBoard().getSideToMove().equals(Side.WHITE))));
                        return;
                    }
                }
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                break;
            default:
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.UNKNOWN));
                return;
        }
    }

    boolean isRightTurn(Session playerSession) {
        int playerId = (Integer) playerSession.getUserProperties().get("player_id");
        GamePlayer gamePlayer = playerId == chessGame.getPlayer1().getId() ? chessGame.getPlayer1()
                : chessGame.getPlayer2();
        Side side = chessGame.getBoard().getSideToMove();
        // check right turn
        if (side.equals(Side.WHITE)) {
            if (gamePlayer != chessGame.getPlayer1()) {
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                return false;
            }
        } else {
            if (gamePlayer != chessGame.getPlayer2()) {
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                return false;
            }
        }
        return true;
    }

    void sendToAllPlayer(GameMessageDto message) {
        if (player1Session != null) {
            player1Session.getAsyncRemote().sendObject(message);
        }
        if (player2Session != null) {
            player2Session.getAsyncRemote().sendObject(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        if (chessGame != null)
            logger.error("GameId: " + chessGame.getId() + " " + throwable.getMessage());
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        if (session.equals(player1Session)) {
            player1Session = null;
        } else if (session.equals(player2Session)) {
            player2Session = null;
        }
    }
}
