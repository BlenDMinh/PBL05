package ws.v2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import common.GameStatus;
import common.dto.UserPasswordDto;
import modules.game_chesslib.common.GameMessage;
import modules.game_chesslib.common.GameMessageDto;
import modules.game_chesslib.common.MessageDecoder;
import modules.game_chesslib.common.MessageEncoder;
import modules.game_chesslib.common.nested.MoveResponse;
import modules.game_chesslib.common.nested.PlayerJoinedResponse;
import modules.game_chesslib.common.nested.ResignResponse;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.player.GamePlayer;
import modules.game_chesslib.dto.GameDto;
import modules.game_chesslib.dto.GameHumanDto;
import modules.game_chesslib.dto.RuleSetDto;
import modules.game_chesslib.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;
import modules.game_chesslib.GameStore;
import modules.game_chesslib.custom.ChessGame;

@ServerEndpoint(value = "/v2/game-player/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GamePlayerEndPoint {
    private final Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();
    private ChessGame chessGame;
    static ReentrantLock lock = new ReentrantLock();

    @OnOpen
    public void onOpen(Session playerSession, @PathParam("id") String id) throws SQLException, Exception {
        lock.lock();
        try {
            String queryString = playerSession.getQueryString();
            if (!queryString.startsWith("sid=")) {
                playerSession.close();
            }
            String sessionId = queryString.substring("sid=".length());
            stores.session.Session session = SimpleSessionManager.getInstance()
                    .getSession(sessionId);
            if (session == null) {
                playerSession.getBasicRemote().sendText(GameMessage.SESSION_NOT_VALID);
                playerSession.close();
                return;
            }
            UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
            if (GameStore.getInstance().isGameExist(id)) {
                chessGame = GameStore.getInstance().getGameById(id);
                boolean isValidGame = chessGame.getStatus().equals(GameStatus.WAITING)
                        || chessGame.getStatus().equals(GameStatus.PLAYING);
                if (!isValidGame || (chessGame.getPlayer1Session() != null && chessGame.getPlayer2Session() != null)) {
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
                    playerSession.close();
                    return;
                }
            } else {
                GameDto gameDto = gameService.getById(id);
                boolean isValidGame = gameService.isValidGame(gameDto);
                if (!isValidGame) {
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
                    playerSession.close();
                    return;
                }
                ChessGame newChessGame = new ChessGame(gameDto.getId(), gameDto.getPlayer1Id(),
                        gameDto.getPlayer2Id());
                RuleSetDto ruleSetDto = gameDto.getRuleSetDto();
                JsonObject rulesetDetail = ruleSetDto.getDetail();
                GameRule gameRule = new GameRule(ruleSetDto.getId(), ruleSetDto.getName(),
                        rulesetDetail.get("minute_per_turn").getAsInt(),
                        rulesetDetail.get("total_minute_per_player").getAsInt(),
                        rulesetDetail.get("turn_around_steps").getAsInt(),
                        rulesetDetail.get("turn_around_time_plus").getAsInt());
                newChessGame.setGameRule(gameRule);
                GameStore.getInstance().addGame(newChessGame);
                chessGame = newChessGame;
            }

            int userId = userPasswordDto.getId();
            ModelMapper modelMapper = new ModelMapper();
            if (chessGame.getPlayer1().getId() == userId) {
                playerSession.getUserProperties().put("sid", sessionId);
                chessGame.setPlayer1Session(playerSession);
                GameHumanDto gameHumanDto = modelMapper.map(userPasswordDto,
                        GameHumanDto.class);
                gameHumanDto.setWhite(chessGame.getPlayer1().isWhite());
                GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                        new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                gameHumanDto, chessGame.getMoveHistories()));
                sendToAllPlayer(resp);
                if (chessGame.getPlayer2Session() != null) {
                    String player2SessionId = (String) chessGame.getPlayer2Session().getUserProperties().get("sid");
                    stores.session.Session session2 = SimpleSessionManager.getInstance()
                            .getSession(player2SessionId);
                    UserPasswordDto userPasswordDto2 = session2.getAttribute(
                            SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    GameHumanDto gameHumanDto2 = modelMapper.map(userPasswordDto2,
                            GameHumanDto.class);
                    gameHumanDto2.setWhite(chessGame.getPlayer2().isWhite());
                    GameMessageDto resp2 = new GameMessageDto(GameMessage.PLAYER_JOINED,
                            new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                    gameHumanDto2, chessGame.getMoveHistories()));
                    playerSession.getBasicRemote().sendObject(resp2);
                }
            } else if (chessGame.getPlayer2().getId() == userId) {
                playerSession.getUserProperties().put("sid", sessionId);
                chessGame.setPlayer2Session(playerSession);
                GameHumanDto gameHumanDto = modelMapper.map(userPasswordDto,
                        GameHumanDto.class);
                gameHumanDto.setWhite(chessGame.getPlayer2().isWhite());
                GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                        new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                gameHumanDto, chessGame.getMoveHistories()));
                sendToAllPlayer(resp);
                if (chessGame.getPlayer1Session() != null) {
                    String player1SessionId = (String) chessGame.getPlayer1Session().getUserProperties().get("sid");
                    stores.session.Session session1 = SimpleSessionManager.getInstance()
                            .getSession(player1SessionId);
                    UserPasswordDto userPasswordDto1 = session1.getAttribute(
                            SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    GameHumanDto gameHumanDto1 = modelMapper.map(userPasswordDto1,
                            GameHumanDto.class);
                    gameHumanDto1.setWhite(chessGame.getPlayer1().isWhite());
                    GameMessageDto resp2 = new GameMessageDto(GameMessage.PLAYER_JOINED,
                            new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                    gameHumanDto1, chessGame.getMoveHistories()));
                    playerSession.getBasicRemote().sendObject(resp2);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @OnMessage
    public void onMessage(GameMessageDto gameMessageDto, Session playerSession) throws IOException, EncodeException {
        lock.lock();
        try {
            String message = gameMessageDto.getMessage();
            Square from, to;
            Move chessMove;
            boolean validMove = false;
            modules.game_chesslib.custom.Move move;
            switch (message) {
                case GameMessage.MOVE:
                    move = gson.fromJson(gameMessageDto.getData().toString(),
                            modules.game_chesslib.custom.Move.class);
                    if (isRightTurn(playerSession)) {
                        from = Square.valueOf(move.getFrom().toUpperCase());
                        to = Square.valueOf(move.getTo().toUpperCase());
                        chessMove = new Move(from, to);
                        validMove = false;
                        try {
                            validMove = chessGame.getBoard()
                                    .isMoveLegal(
                                            chessMove,
                                            true);
                        } catch (Exception e) {
                            validMove = false;
                        }
                        if (validMove) {
                            String mess = GameMessage.MOVE;
                            if (chessMove.getFrom().equals(Square.E1)) {
                            }
                            chessGame.addMoveHistory(chessMove);
                            chessGame.getBoard().doMove(chessMove);
                            sendToAllPlayer(
                                    new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                            chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                            chessGame.getMoveHistories())));
                            postCheck();
                            return;
                        }
                    }
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    break;
                case GameMessage.PROMOTION:
                    move = gson.fromJson(gameMessageDto.getData().toString(),
                            modules.game_chesslib.custom.Move.class);
                    if (isRightTurn(playerSession)) {
                        from = Square.valueOf(move.getFrom().toUpperCase());
                        to = Square.valueOf(move.getTo().toUpperCase());
                        Piece promotion = Piece.fromValue(move.getPromotion());
                        chessMove = new Move(from, to, promotion);
                        validMove = false;
                        try {
                            validMove = chessGame.getBoard()
                                    .isMoveLegal(
                                            chessMove,
                                            true);
                        } catch (Exception e) {
                            validMove = false;
                        }
                        if (validMove) {
                            chessGame.addMoveHistory(chessMove);
                            chessGame.getBoard().doMove(chessMove);
                            sendToAllPlayer(
                                    new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                            chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                            chessGame.getMoveHistories())));
                            postCheck();
                            return;
                        }
                    }
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    break;

                case GameMessage.RESIGN:
                    String sessionId = (String) playerSession.getUserProperties().get("sid");
                    stores.session.Session session = SimpleSessionManager.getInstance()
                            .getSession(sessionId);
                    UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    GamePlayer gamePlayer = userPasswordDto.getId() == chessGame.getPlayer1().getId()
                            ? chessGame.getPlayer1()
                            : chessGame.getPlayer2();
                    boolean resignSide = gamePlayer.isWhite();
                    sendToAllPlayer(
                            new GameMessageDto(GameMessage.RESIGN, new ResignResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE), resignSide,
                                    chessGame.getMoveHistories())));
                    break;

                default:
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.UNKNOWN));
                    return;
            }
        } finally {
            lock.unlock();
        }
    }

    void postCheck() throws IOException, EncodeException {
        if (chessGame.getBoard().isDraw()) {
            sendToAllPlayer(new GameMessageDto(GameMessage.DRAW));
        } else if (chessGame.getBoard().isMated()) {
            sendToAllPlayer(new GameMessageDto(GameMessage.MATE, new MoveResponse(chessGame.getBoard().getFen(),
                    chessGame.getBoard().getSideToMove().equals(Side.WHITE), chessGame.getMoveHistories())));
        }
    }

    boolean isRightTurn(Session playerSession) throws IOException, EncodeException {
        String sessionId = (String) playerSession.getUserProperties().get("sid");
        stores.session.Session session = SimpleSessionManager.getInstance()
                .getSession(sessionId);
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        GamePlayer gamePlayer = userPasswordDto.getId() == chessGame.getPlayer1().getId() ? chessGame.getPlayer1()
                : chessGame.getPlayer2();
        Side side = chessGame.getBoard().getSideToMove();
        // check right turn
        if (side.equals(Side.WHITE)) {
            if (gamePlayer != chessGame.getPlayer1()) {
                playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                return false;
            }
        } else {
            if (gamePlayer != chessGame.getPlayer2()) {
                playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                return false;
            }
        }
        return true;
    }

    void sendToAllPlayer(GameMessageDto message) throws EncodeException, IOException {
        if (chessGame.getPlayer1Session() != null) {
            chessGame.getPlayer1Session().getBasicRemote().sendObject(message);
        }
        if (chessGame.getPlayer2Session() != null) {
            chessGame.getPlayer2Session().getBasicRemote().sendObject(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        chessGame.getBoard().legalMoves();
        if (chessGame != null)
            logger.error("GameId: " + chessGame.getId() + " " + throwable.getMessage());
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        if (session.equals(chessGame.getPlayer1Session())) {
            chessGame.setPlayer1Session(null);
        } else if (session.equals(chessGame.getPlayer2Session())) {
            chessGame.setPlayer2Session(null);
        }
    }
}
