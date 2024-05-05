package modules.game_chesslib;

import java.util.HashMap;
import java.util.Map;

import modules.game_chesslib.custom.chessgame.GameBot;
import modules.game_chesslib.custom.chessgame.GameHuman;

public class GameStore {
    static volatile GameStore instance;

    public static GameStore getInstance() {
        return instance;
    }

    private GameStore() {
    }

    static {
        instance = new GameStore();
    }
    private Map<String, GameHuman> gameHumanMap = new HashMap<>();
    private Map<String, GameBot> gameBotMap = new HashMap<>();

    public GameHuman getGameHumanById(String id) {
        return gameHumanMap.get(id);
    }

    public boolean isGameHumanExist(String id) {
        return gameHumanMap.containsKey(id);
    }

    public void addGameHuman(GameHuman gameHuman) {
        gameHumanMap.put(gameHuman.getId(), gameHuman);
    }

    public GameBot getGameBotById(String id) {
        return gameBotMap.get(id);
    }

    public boolean isGameBotExist(String id) {
        return gameBotMap.containsKey(id);
    }

    public void addBotHuman(GameBot gameBot) {
        gameBotMap.put(gameBot.getId(), gameBot);
    }
}
