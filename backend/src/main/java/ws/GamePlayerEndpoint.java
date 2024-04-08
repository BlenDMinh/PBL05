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
import org.modelmapper.ModelMapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import modules.auth.dto.UserPasswordDto;
import modules.game.common.GameMessage;
import modules.game.common.GameMessageDto;
import modules.game.common.MessageDecoder;
import modules.game.common.MessageEncoder;
import modules.game.common.nested.CastleResponse;
import modules.game.common.nested.MoveRequest;
import modules.game.common.nested.MoveResponse;
import modules.game.common.nested.PlayerJoinedResponse;
import modules.game.common.nested.PromotionRequest;
import modules.game.common.nested.PromotionResponse;
import modules.game.custom.ChessGame;
import modules.game.custom.GameRule;
import modules.game.custom.Position;
import modules.game.custom.piece.ChessPiece;
import modules.game.custom.piece.King;
import modules.game.custom.piece.Pawn;
import modules.game.custom.player.GamePlayer;
import modules.game.dto.GameDto;
import modules.game.dto.GamePlayerDto;
import modules.game.dto.RuleSetDto;
import modules.game.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint(value = "/game-player/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GamePlayerEndpoint {
    private final Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();
    static Session player1Session, player2Session;
    private ChessGame chessGame;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") Integer id) throws SQLException, Exception {
        GameDto gameDto = gameService.getById(id);
        boolean isValidGame = gameService.isValidGame(gameDto);
        if (!isValidGame || (player1Session != null && player2Session != null)) {
            session.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.GAME_NOT_VALID));
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
    public void onMessage(GameMessageDto gameMessageDto, Session playerSession) throws IOException {
        String message = gameMessageDto.getMessage();
        if (!message.equals(GameMessage.JSESSIONID)) {
            Integer playerId = (Integer) playerSession.getUserProperties().get("player_id");
            if (playerId == null) {
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.NOT_AUTHENTICATED));
                return;
            }
        }
        boolean isPlayer1Turn = chessGame.isPlayer1Turn();
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
                    GamePlayerDto gamePlayerDto = modelMapper.map(userPasswordDto,
                            GamePlayerDto.class);

                    if (chessGame.getPlayer1().getId() == userId) {
                        playerSession.getUserProperties().put("player_id", userId);
                        player1Session = playerSession;
                        GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                                new PlayerJoinedResponse(chessGame.getRawBoard(), true, gamePlayerDto));
                        sendToAllPlayer(resp);
                    } else if (chessGame.getPlayer2().getId() == userId) {
                        playerSession.getUserProperties().put("player_id", userId);
                        player2Session = playerSession;
                        GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                                new PlayerJoinedResponse(chessGame.getRawBoard(), false, gamePlayerDto));
                        sendToAllPlayer(resp);
                        chessGame.nextTurn();
                    } else {
                        playerSession.close();
                    }
                }
                break;

            case GameMessage.MOVE:
                MoveRequest moveRequest = gson.fromJson(gameMessageDto.getData().toString(), MoveRequest.class);
                Position from = moveRequest.getFrom();
                Position to = moveRequest.getTo();
                if (isRightTurnAndOwner(from, to, playerSession)) {
                    ChessPiece chessPiece = chessGame.getBoard()[from.getRow()][from.getCol()];
                    if (chessPiece.isValidMove(to)) {
                        chessPiece.doMove(to);
                        sendToAllPlayer(
                                new GameMessageDto(GameMessage.MOVE,
                                        new MoveResponse(chessGame.getRawBoard(), !isPlayer1Turn, from, to)));
                        chessGame.nextTurn();
                    } else {
                        playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    }
                }
                break;

            case GameMessage.CASTLE:
                if (isRightTurn(playerSession)) {
                    boolean white = getTurn(playerSession);
                    King king = white ? chessGame.getWhiteKing() : chessGame.getBlackKing();
                    if (king.isCastlingAllowed()) {
                        king.doCastle();
                        sendToAllPlayer(
                                new GameMessageDto(GameMessage.CASTLE,
                                        new CastleResponse(chessGame.getRawBoard(), !isPlayer1Turn)));
                        chessGame.nextTurn();
                    } else {
                        playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    }
                }
                break;

            case GameMessage.PROMOTION:
                PromotionRequest promotionRequest = gson.fromJson(gameMessageDto.getData().toString(),
                        PromotionRequest.class);
                Position pawnFrom = promotionRequest.getFrom();
                Position pawnTo = promotionRequest.getTo();
                String piece = promotionRequest.getPiece();
                if (isRightTurnAndOwner(pawnFrom, pawnTo, playerSession)) {
                    playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    return;
                }
                // check if board at from position is a pawn
                if (!(chessGame.getBoard()[pawnFrom.getRow()][pawnFrom.getCol()] instanceof Pawn)) {
                    playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    return;
                }
                Pawn pawn = (Pawn) chessGame.getBoard()[pawnFrom.getRow()][pawnFrom.getCol()];
                if (!pawn.isValidMove(pawnTo) || !pawn.isReachedEndOfRow()) {
                    playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                    return;
                }
                pawn.doMove(pawnTo);
                pawn.promotePawn(piece);
                sendToAllPlayer(new GameMessageDto(GameMessage.PROMOTION,
                        new PromotionResponse(chessGame.getRawBoard(), !isPlayer1Turn, pawnFrom, pawnTo, piece)));
                chessGame.nextTurn();
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

        // check right turn
        if (chessGame.isPlayer1Turn()) {
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

    boolean isRightTurnAndOwner(Position from, Position to, Session playerSession) {
        int playerId = (Integer) playerSession.getUserProperties().get("player_id");
        GamePlayer gamePlayer = playerId == chessGame.getPlayer1().getId() ? chessGame.getPlayer1()
                : chessGame.getPlayer2();

        // check right turn
        if (chessGame.isPlayer1Turn()) {
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

        // check exist ChessPiece at positon 'from'
        // if exist, check right owner of ChessPiece
        ChessPiece chessPiece = chessGame.getBoard()[from.getRow()][from.getCol()];
        if (chessPiece == null || chessPiece.isWhite() != gamePlayer.isWhite()) {
            playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
            return false;
        }

        return true;
    }

    boolean getTurn(Session playerSession) {
        int playerId = (Integer) playerSession.getUserProperties().get("player_id");
        GamePlayer gamePlayer = playerId == chessGame.getPlayer1().getId() ? chessGame.getPlayer1()
                : chessGame.getPlayer2();
        return gamePlayer.isWhite();
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
        if (player1Session != null && player1Session.isOpen()) {
            player1Session.close();
        }
        if (player2Session != null && player2Session.isOpen()) {
            player2Session.close();
        }
    }
}
