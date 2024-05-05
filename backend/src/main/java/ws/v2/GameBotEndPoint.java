package ws.v2;

import java.io.IOException;
import java.sql.SQLException;

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

import common.dto.UserPasswordDto;
import modules.game_chesslib.common.nested.MoveResponse;
import modules.game_chesslib.common.nested.PlayerJoinedResponse;
import modules.game_chesslib.common.nested.ResignResponse;
import modules.game_chesslib.custom.chessgame.GameBot;
import modules.game_chesslib.custom.player.BotPlayer;
import modules.game_chesslib.dto.GameBotDto;
import modules.game_chesslib.dto.GameHumanDto;
import modules.game_chesslib.GameStore;
import modules.game_chesslib.algo.Minimax;
import modules.game_chesslib.common.GameMessage;
import modules.game_chesslib.common.GameMessageDto;
import modules.game_chesslib.common.MessageDecoder;
import modules.game_chesslib.common.MessageEncoder;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint(value = "/v2/game-bot/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GameBotEndPoint {
    private final Logger logger = Logger.getLogger("GameBotEndpoint");
    private final Gson gson = new Gson();
    private final Minimax bot = new Minimax();
    static Session humanSession;
    private GameBot chessGame;

    @OnOpen
    public void onOpen(Session playerSession, @PathParam("id") String id) throws SQLException, Exception {
        if (!GameStore.getInstance().isGameBotExist(id)) {
            playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
            return;
        }
        chessGame = GameStore.getInstance().getGameBotById(id);
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
        if (userPasswordDto.getId() != chessGame.getHumanPlayer().getId()) {
            playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.NOT_AUTHENTICATED));
            return;
        }
        humanSession = playerSession;
        ModelMapper modelMapper = new ModelMapper();
        GameHumanDto gameHumanDto = modelMapper.map(userPasswordDto,
                GameHumanDto.class);
        gameHumanDto.setWhite(chessGame.getHumanPlayer().isWhite());
        BotPlayer botPlayer = chessGame.getBotPlayer();
        GameBotDto gameBotDto = new GameBotDto(botPlayer.isWhite(), botPlayer.getDifficulty());
        playerSession.getBasicRemote().sendObject(new GameMessageDto(GameMessage.PLAYER_JOINED,
                new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                        chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                        gameHumanDto, chessGame.getMoveHistories())));
        playerSession.getBasicRemote()
                .sendObject(new GameMessageDto(GameMessage.BOT_JOINED,
                        new PlayerJoinedResponse(chessGame.getBoard().getFen(),
                                chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                gameBotDto, chessGame.getMoveHistories())));
        if (botPlayer.isWhite() && chessGame.getBoard().getSideToMove().equals(Side.WHITE)) {
            doBot();
            sendToAllPlayer(
                    new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                            chessGame.getBoard().getSideToMove().equals(Side.WHITE), chessGame.getMoveHistories())));
        }
    }

    @OnMessage
    public void onMessage(GameMessageDto gameMessageDto, Session playerSession)
            throws IOException, InterruptedException, EncodeException {
        String message = gameMessageDto.getMessage();
        Square from, to;
        Move chessMove;
        boolean validMove = false;
        modules.game_chesslib.custom.Move move;
        switch (message) {
            case GameMessage.MOVE:
                if (!isRightTurn(playerSession)) {
                    return;
                }
                move = gson.fromJson(gameMessageDto.getData().toString(),
                        modules.game_chesslib.custom.Move.class);
                from = Square.valueOf(move.getFrom().toUpperCase());
                to = Square.valueOf(move.getTo().toUpperCase());
                chessMove = new Move(from, to);
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
                    doBot();
                    sendToAllPlayer(
                            new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                    chessGame.getMoveHistories())));
                    postCheck();
                    return;
                } else {
                    playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                }
                break;

            case GameMessage.PROMOTION:
                if (!isRightTurn(playerSession)) {
                    return;
                }
                move = gson.fromJson(gameMessageDto.getData().toString(),
                        modules.game_chesslib.custom.Move.class);
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
                    doBot();
                    sendToAllPlayer(
                            new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                    chessGame.getMoveHistories())));
                    postCheck();
                    return;
                } else {
                    playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                }
                break;

            case GameMessage.RESIGN:
                sendToAllPlayer(
                        new GameMessageDto(GameMessage.RESIGN, new ResignResponse(chessGame.getBoard().getFen(),
                                chessGame.getBoard().getSideToMove().equals(Side.WHITE),
                                chessGame.getHumanPlayer().isWhite(),
                                chessGame.getMoveHistories())));
                break;
            default:
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.UNKNOWN));
                return;
        }
    }

    void doBot() {
        BotPlayer botPlayer = chessGame.getBotPlayer();
        Move bestMove = bot.getBestMoveAlphaBeta(chessGame.getBoard(),
                botPlayer.getDifficulty().getValue(),
                botPlayer.isWhite() ? Side.WHITE : Side.BLACK);
        chessGame.addMoveHistory(bestMove);
        chessGame.getBoard().doMove(bestMove);
    }

    void sendToAllPlayer(GameMessageDto message) throws IOException, EncodeException {
        if (humanSession != null) {
            humanSession.getBasicRemote().sendObject(message);
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

    boolean isRightTurn(Session playerSession) {
        boolean white = chessGame.getHumanPlayer().isWhite();
        Side side = chessGame.getBoard().getSideToMove();
        // check right turn
        if (side.equals(Side.WHITE) == white) {
            return true;
        }
        playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
        return false;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        chessGame.getBoard().legalMoves();
        if (chessGame != null)
            logger.error("GameId: " + chessGame.getId() + " " + throwable.getMessage());
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        if (session.equals(humanSession)) {
            humanSession = null;
        }
    }
}
