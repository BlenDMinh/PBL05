package ws.v2;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;
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
import common.GameStatus;
import common.dto.UserPasswordDto;
import modules.game_chesslib.common.GameMessage;
import modules.game_chesslib.common.GameMessageDto;
import modules.game_chesslib.common.MessageDecoder;
import modules.game_chesslib.common.MessageEncoder;
import modules.game_chesslib.common.nested.MoveResponse;
import modules.game_chesslib.common.nested.PlayerJoinedResponse;
import modules.game_chesslib.common.nested.ResignResponse;
import modules.game_chesslib.common.nested.TimeUpResponse;
import modules.game_chesslib.custom.chessgame.GameHuman;
import modules.game_chesslib.custom.player.HumanPlayer;
import modules.game_chesslib.dto.GameHumanDto;
import modules.game_chesslib.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;
import modules.game_chesslib.GameStore;

@ServerEndpoint(value = "/v2/game-player/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GamePlayerEndPoint {
    private final Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();
    private GameHuman chessGame;
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
            if (GameStore.getInstance().isGameHumanExist(id)) {
                chessGame = GameStore.getInstance().getGameHumanById(id);
                boolean isValidGame = chessGame.getStatus().equals(GameStatus.WAITING)
                        || chessGame.getStatus().equals(GameStatus.PLAYING);
                if (!isValidGame || (chessGame.getWhiteSession() != null && chessGame.getBlackSession() != null)) {
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
                    playerSession.close();
                    return;
                }
            } else {
                playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
            }

            int userId = userPasswordDto.getId();
            ModelMapper modelMapper = new ModelMapper();
            if (chessGame.getHumanWhitePlayer().getId() == userId) {
                playerSession.getUserProperties().put("sid", sessionId);
                chessGame.setWhiteSession(playerSession);
                GameHumanDto gameHumanDto = modelMapper.map(userPasswordDto,
                        GameHumanDto.class);
                gameHumanDto.setWhite(true);
                GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                        new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                gameHumanDto, chessGame.getMoveHistories(),
                                chessGame.getHumanWhitePlayer().getRemainMillis(),
                                chessGame.getHumanBlackPlayer().getRemainMillis()));
                sendToAllPlayer(resp);
                if (chessGame.getBlackSession() != null) {
                    String blackSessionId = (String) chessGame.getBlackSession().getUserProperties().get("sid");
                    stores.session.Session session2 = SimpleSessionManager.getInstance()
                            .getSession(blackSessionId);
                    UserPasswordDto userPasswordDto2 = session2.getAttribute(
                            SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    GameHumanDto gameHumanDto2 = modelMapper.map(userPasswordDto2,
                            GameHumanDto.class);
                    gameHumanDto2.setWhite(false);
                    GameMessageDto resp2 = new GameMessageDto(GameMessage.PLAYER_JOINED,
                            new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                    gameHumanDto2, chessGame.getMoveHistories(),
                                    chessGame.getHumanWhitePlayer().getRemainMillis(),
                                    chessGame.getHumanBlackPlayer().getRemainMillis()));
                    playerSession.getBasicRemote().sendObject(resp2);
                }
            } else if (chessGame.getHumanBlackPlayer().getId() == userId) {
                playerSession.getUserProperties().put("sid", sessionId);
                chessGame.setBlackSession(playerSession);
                GameHumanDto gameHumanDto = modelMapper.map(userPasswordDto,
                        GameHumanDto.class);
                gameHumanDto.setWhite(false);
                GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                        new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                gameHumanDto, chessGame.getMoveHistories(),
                                chessGame.getHumanWhitePlayer().getRemainMillis(),
                                chessGame.getHumanBlackPlayer().getRemainMillis()));
                sendToAllPlayer(resp);
                if (chessGame.getWhiteSession() != null) {
                    String whiteSessionId = (String) chessGame.getWhiteSession().getUserProperties().get("sid");
                    stores.session.Session session1 = SimpleSessionManager.getInstance()
                            .getSession(whiteSessionId);
                    UserPasswordDto userPasswordDto1 = session1.getAttribute(
                            SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    GameHumanDto gameHumanDto1 = modelMapper.map(userPasswordDto1,
                            GameHumanDto.class);
                    gameHumanDto1.setWhite(true);
                    GameMessageDto resp2 = new GameMessageDto(GameMessage.PLAYER_JOINED,
                            new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                    gameHumanDto1, chessGame.getMoveHistories(),
                                    chessGame.getHumanWhitePlayer().getRemainMillis(),
                                    chessGame.getHumanBlackPlayer().getRemainMillis()));
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
                            boolean white = chessGame.getBoard().getSideToMove().equals(Side.WHITE);
                            HumanPlayer currentPlayer = white
                                    ? chessGame.getHumanWhitePlayer()
                                    : chessGame.getHumanBlackPlayer();
                            pauseTimerAndUpdateRemainTimeFor(currentPlayer);
                            chessGame.addMoveHistory(chessMove);
                            chessGame.getBoard().doMove(chessMove);
                            sendToAllPlayer(
                                    new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                            chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                            chessGame.getMoveHistories(),
                                            chessGame.getHumanWhitePlayer().getRemainMillis(),
                                            chessGame.getHumanBlackPlayer().getRemainMillis())));
                            chessGame.setLastMoveTime(System.currentTimeMillis());
                            currentPlayer.increaseStepCount();
                            startTimer();
                            break;
                        }
                    }
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    return;
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
                            boolean white = chessGame.getBoard().getSideToMove().equals(Side.WHITE);
                            HumanPlayer currentPlayer = white
                                    ? chessGame.getHumanWhitePlayer()
                                    : chessGame.getHumanBlackPlayer();
                            pauseTimerAndUpdateRemainTimeFor(currentPlayer);
                            chessGame.addMoveHistory(chessMove);
                            chessGame.getBoard().doMove(chessMove);
                            sendToAllPlayer(
                                    new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                            chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                            chessGame.getMoveHistories(),
                                            chessGame.getHumanWhitePlayer().getRemainMillis(),
                                            chessGame.getHumanBlackPlayer().getRemainMillis())));
                            chessGame.setLastMoveTime(System.currentTimeMillis());
                            currentPlayer.increaseStepCount();
                            startTimer();
                            break;
                        }
                    }
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    return;

                case GameMessage.RESIGN:
                    String sessionId = (String) playerSession.getUserProperties().get("sid");
                    stores.session.Session session = SimpleSessionManager.getInstance()
                            .getSession(sessionId);
                    UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    HumanPlayer gamePlayer = userPasswordDto.getId() == chessGame.getHumanWhitePlayer().getId()
                            ? chessGame.getHumanWhitePlayer()
                            : chessGame.getHumanBlackPlayer();
                    boolean resignSide = gamePlayer.isWhite();
                    sendToAllPlayer(
                            new GameMessageDto(GameMessage.RESIGN, new ResignResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE), resignSide,
                                    chessGame.getMoveHistories(), chessGame.getHumanWhitePlayer().getRemainMillis(),
                                    chessGame.getHumanBlackPlayer().getRemainMillis())));
                    break;

                default:
                    playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.UNKNOWN));
                    return;
            }
            postCheck();
        } finally {
            lock.unlock();
        }
    }

    void pauseTimerAndUpdateRemainTimeFor(HumanPlayer currentPlayer) {
        chessGame.cancelTimer();
        if (chessGame.getLastMoveTime() != 0) {
            long moveWaitedTime = System.currentTimeMillis() - chessGame.getLastMoveTime();
            currentPlayer.decreaseRemainMillisBy(moveWaitedTime);
        }
    }

    void startTimer() {
        boolean isWhite = chessGame.getBoard().getSideToMove().equals(Side.WHITE);
        Long remainMillisWhenTurnEnd = (isWhite ? chessGame.getHumanWhitePlayer() : chessGame.getHumanBlackPlayer())
                .getRemainMillis();
        Long remainMillisWhenTotalEnd = remainMillisWhenTurnEnd.longValue();
        if (remainMillisWhenTurnEnd != null) {
            remainMillisWhenTurnEnd -= Long.valueOf(Math.round(chessGame.getGameRule().getMinutePerTurn() * 60 * 1000));
            remainMillisWhenTurnEnd = Math.max(0, remainMillisWhenTurnEnd);
        }
        if (remainMillisWhenTotalEnd != null) {
            remainMillisWhenTotalEnd = 0L;
        }
        chessGame.startTimer(timeUpTask(chessGame.getBoard().getFen(), isWhite, remainMillisWhenTurnEnd),
                timeUpTask(chessGame.getBoard().getFen(), isWhite, remainMillisWhenTotalEnd),
                isWhite);
    }

    void postCheck() throws IOException, EncodeException {
        if (chessGame.getBoard().isDraw()) {
            sendToAllPlayer(new GameMessageDto(GameMessage.DRAW));
        } else if (chessGame.getBoard().isMated()) {
            sendToAllPlayer(new GameMessageDto(GameMessage.MATE, new MoveResponse(chessGame.getBoard().getFen(),
                    chessGame.getBoard().getSideToMove().equals(Side.WHITE), chessGame.getMoveHistories(),
                    chessGame.getHumanWhitePlayer().getRemainMillis(),
                    chessGame.getHumanBlackPlayer().getRemainMillis())));
        }
    }

    boolean isRightTurn(Session playerSession) throws IOException, EncodeException {
        String sessionId = (String) playerSession.getUserProperties().get("sid");
        stores.session.Session session = SimpleSessionManager.getInstance()
                .getSession(sessionId);
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        Side side = chessGame.getBoard().getSideToMove();
        // check right turn
        if (side.equals(Side.WHITE)) {
            if (userPasswordDto.getId() != chessGame.getHumanWhitePlayer().getId()) {
                playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                return false;
            }
        } else {
            if (userPasswordDto.getId() != chessGame.getHumanBlackPlayer().getId()) {
                playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                return false;
            }
        }
        return true;
    }

    void sendToAllPlayer(GameMessageDto message) throws IOException, EncodeException {
        if (chessGame.getWhiteSession() != null) {
            chessGame.getWhiteSession().getBasicRemote().sendObject(message);
        }
        if (chessGame.getBlackSession() != null) {
            chessGame.getBlackSession().getBasicRemote().sendObject(message);
        }
    }

    TimerTask timeUpTask(String fen, boolean isWhite, Long remainMillisWhenEnd) {
        return new TimerTask() {

            @Override
            public void run() {
                if (isWhite) {
                    chessGame.getHumanWhitePlayer().setRemainMillis(remainMillisWhenEnd);
                } else {
                    chessGame.getHumanBlackPlayer().setRemainMillis(remainMillisWhenEnd);
                }
                try {
                    sendToAllPlayer(new GameMessageDto(GameMessage.TIME_UP, new TimeUpResponse(fen, isWhite,
                            chessGame.getHumanWhitePlayer().getRemainMillis(),
                            chessGame.getHumanBlackPlayer().getRemainMillis())));
                } catch (IOException | EncodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        chessGame.getBoard().legalMoves();
        if (chessGame != null)
            logger.error("GameId: " + chessGame.getId() + " " + throwable.getMessage());
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        if (session.equals(chessGame.getWhiteSession())) {
            chessGame.setWhiteSession(null);
        } else if (session.equals(chessGame.getBlackSession())) {
            chessGame.setBlackSession(null);
        }
    }
}
