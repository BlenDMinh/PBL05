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

import modules.auth.dto.UserPasswordDto;
import modules.game_chesslib.custom.GameDifficulty;
import modules.game_chesslib.common.nested.HumanJoinRequest;
import modules.game_chesslib.common.nested.MoveResponse;
import modules.game_chesslib.common.nested.PlayerJoinedResponse;
import modules.game_chesslib.custom.player.BotPlayer;
import modules.game_chesslib.dto.GamePlayerDto;
import modules.game_chesslib.GameStore;
import modules.game_chesslib.algo.Minimax;
import modules.game_chesslib.common.GameMessage;
import modules.game_chesslib.common.GameMessageDto;
import modules.game_chesslib.common.MessageDecoder;
import modules.game_chesslib.common.MessageEncoder;
import modules.game_chesslib.custom.ChessGame;
import modules.game_chesslib.custom.player.UserPlayer;
import modules.game_chesslib.service.GameService;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint(value = "/v2/game-bot/{id}", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class GameBotEndPoint {
    private final Logger logger = Logger.getLogger("GamePlayerEndpoint");
    private final GameService gameService = new GameService();
    private final Gson gson = new Gson();
    private final Minimax bot = new Minimax();
    static Session humanSession;
    private ChessGame chessGame;

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) throws SQLException, Exception {
        if (GameStore.getInstance().isGameExist(id)) {
            chessGame = GameStore.getInstance().getGameById(id);
        }
        // GameDto gameDto = gameService.getById(id);
        // boolean isValidGame = gameService.isValidGame(gameDto);
        // if (!isValidGame || humanSession != null) {
        // session.getAsyncRemote().sendObject(new
        // GameMessageDto(GameMessage.GAME_NOT_VALID));
        // session.close();
        // } else
        if (chessGame == null) {
            ChessGame newChessGame = new ChessGame(id);
            // RuleSetDto ruleSetDto = gameDto.getRuleSetDto();
            // JsonObject rulesetDetail = ruleSetDto.getDetail();
            // GameRule gameRule = new GameRule(ruleSetDto.getId(), ruleSetDto.getName(),
            // rulesetDetail.get("minute_per_turn").getAsInt(),
            // rulesetDetail.get("total_minute_per_player").getAsInt(),
            // rulesetDetail.get("turn_around_steps").getAsInt(),
            // rulesetDetail.get("turn_around_time_plus").getAsInt());
            // newChessGame.setGameRule(gameRule);
            GameStore.getInstance().addGame(newChessGame);
            chessGame = newChessGame;
        }

    }

    @OnMessage
    public void onMessage(GameMessageDto gameMessageDto, Session playerSession)
            throws IOException, InterruptedException {
        String message = gameMessageDto.getMessage();
        if (!message.equals(GameMessage.HUMAN_JOIN)) {
            Integer playerId = (Integer) playerSession.getUserProperties().get("player_id");
            if (playerId == null) {
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.NOT_AUTHENTICATED));
                return;
            }
        }
        switch (message) {
            case GameMessage.HUMAN_JOIN:
                Integer playerId = (Integer) playerSession.getUserProperties().get("player_id");
                if (playerId == null) {
                    HumanJoinRequest humanJoinRequest = gson.fromJson(gameMessageDto.getData().toString(),
                            HumanJoinRequest.class);
                    stores.session.Session session = SimpleSessionManager.getInstance()
                            .getSession(humanJoinRequest.getSessionId());
                    if (session == null) {
                        playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.SESSION_NOT_VALID));
                        playerSession.close();
                    }
                    UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO,
                            UserPasswordDto.class);
                    int userId = userPasswordDto.getId();
                    ModelMapper modelMapper = new ModelMapper();
                    playerSession.getUserProperties().put("player_id", userId);
                    humanSession = playerSession;
                    chessGame.setPlayer1(new UserPlayer(userId, chessGame, true));
                    GameDifficulty gameDifficulty = GameDifficulty.fromValue(humanJoinRequest.getDifficulty());
                    ((BotPlayer) chessGame.getPlayer2()).setDifficulty(gameDifficulty);
                    GamePlayerDto gamePlayerDto = modelMapper.map(userPasswordDto,
                            GamePlayerDto.class);
                    gamePlayerDto.setWhite(chessGame.getPlayer1().isWhite());
                    String fen = chessGame.getBoard().getFen();
                    boolean white = chessGame.getBoard().getSideToMove().equals(Side.WHITE);
                    GameMessageDto resp = new GameMessageDto(GameMessage.PLAYER_JOINED,
                            new PlayerJoinedResponse(fen, white, gamePlayerDto));
                    sendToAllPlayer(resp);
                }
                break;

            case GameMessage.MOVE:
                modules.game_chesslib.custom.Move move = gson.fromJson(gameMessageDto.getData().toString(),
                        modules.game_chesslib.custom.Move.class);
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
                    Move bestMove = bot.getBestMove(((BotPlayer) chessGame.getPlayer2()).getDifficulty().getValue(),
                            chessGame.getBoard());
                    chessGame.getBoard().doMove(bestMove);
                    sendToAllPlayer(
                            new GameMessageDto(GameMessage.MOVE, new MoveResponse(chessGame.getBoard().getFen(),
                                    chessGame.getBoard().getSideToMove().equals(Side.WHITE))));
                    return;
                } else {
                    playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.INVALID_MOVE));
                }
                break;
            default:
                playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.UNKNOWN));
                return;
        }
    }

    void sendToAllPlayer(GameMessageDto message) {
        if (humanSession != null) {
            humanSession.getAsyncRemote().sendObject(message);
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
        if (session.equals(humanSession)) {
            humanSession = null;
        }
    }
}
