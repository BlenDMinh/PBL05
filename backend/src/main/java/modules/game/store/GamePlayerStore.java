package modules.game.store;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import modules.game.custom.ChessGame;

public class GamePlayerStore {
    static volatile GamePlayerStore instance;

    public static GamePlayerStore getInstance() {
        return instance;
    }

    private GamePlayerStore() {
    }

    static {
        instance = new GamePlayerStore();
    }
    private Map<String, ChessGame> gameMap = Collections.synchronizedMap(new WeakHashMap<>());

    public ChessGame getGameById(String id) {
        return gameMap.get(id);
    }

    public boolean isGameExist(String id) {
        return gameMap.containsKey(id);
    }

    public void addGame(ChessGame chessGame) {
        gameMap.put(chessGame.getId(), chessGame);
    }
}
