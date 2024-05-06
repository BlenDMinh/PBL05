package ws;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import common.dto.UserPasswordDto;
import modules.game_chesslib.custom.GameDifficulty;
import modules.game_chesslib.custom.chessgame.GameBot;
import modules.game_chesslib.GameStore;
import modules.game_chesslib.common.GameMessage;
import modules.game_chesslib.common.GameMessageDto;
import modules.game_chesslib.common.MessageDecoder;
import modules.game_chesslib.common.MessageEncoder;
import modules.game_chesslib.common.nested.BotConfigRequest;
import stores.session.SessionKey;
import stores.session.SimpleSessionManager;

@ServerEndpoint(value = "/find-bot", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class FindBotEndpoint {
    private final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session userSession) throws IOException {
        String queryString = userSession.getQueryString();
        if (!queryString.startsWith("sid=")) {
            userSession.close();
        }
        String sessionId = queryString.substring("sid=".length());
        stores.session.Session session = SimpleSessionManager.getInstance()
                .getSession(sessionId);
        if (session == null) {
            userSession.getAsyncRemote().sendText(GameMessage.SESSION_NOT_VALID);
            userSession.close();
            return;
        }
        UserPasswordDto userPasswordDto = session.getAttribute(SessionKey.USER_PASSWORD_DTO, UserPasswordDto.class);
        userSession.getUserProperties().put("user_id", userPasswordDto.getId());
    }

    @OnMessage
    public void onMessage(GameMessageDto gameMessageDto, Session playerSession) throws IOException {
        BotConfigRequest botConfigRequest = gson.fromJson(gameMessageDto.getData().toString(), BotConfigRequest.class);
        String side = botConfigRequest.getSide();
        if (side.equals("random")) {
            String[] sides = { "white", "black" };
            Random rand = new Random();
            int index = rand.nextInt(sides.length);
            side = sides[index];
        }
        String gameId = UUID.randomUUID().toString();
        int humanId = (int) playerSession.getUserProperties().get("user_id");
        GameDifficulty difficulty = GameDifficulty.valueOf(botConfigRequest.getDifficulty());
        GameStore.getInstance().addBotHuman(new GameBot(gameId, humanId, difficulty, side.equals("white")));
        playerSession.getAsyncRemote().sendObject(new GameMessageDto(GameMessage.GAME_CREATED, gameId));
        playerSession.close();

    }
}
