package modules.game_chesslib.custom.player;

import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.chessgame.ChessGame;

public class HumanPlayer extends GamePlayer {

    protected int id;
    protected Long remainMillis = null;
    protected Integer stepCount = null;

    protected GameRule gameRule;

    public HumanPlayer(int id, ChessGame game, boolean white, GameRule gameRule) {
        this.id = id;
        this.game = game;
        this.white = white;
        this.gameRule = gameRule;
        if (gameRule.getTotalMinutePerPlayer() != -1) {
            this.remainMillis = Long.valueOf(Math.round(gameRule.getTotalMinutePerPlayer() * 60 * 1000));
        }
        if (gameRule.getTurnAroundStep() != -1) {
            this.stepCount = 0;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GameRule getGameRule() {
        return gameRule;
    }

    public void setGameRule(GameRule gameRule) {
        this.gameRule = gameRule;
    }

    public HumanPlayer(int id, ChessGame game, boolean white) {
        this.id = id;
        this.game = game;
        this.white = white;
    }

    public Long getRemainMillis() {
        return remainMillis;
    }

    public void decreaseRemainMillisBy(long millis) {
        if (this.remainMillis != null)
            this.remainMillis -= millis;
    }

    public void setRemainMillis(long remainMillis) {
        this.remainMillis = remainMillis;
    }

    public void increaseStepCount() {
        if (this.stepCount == null)
            return;
        this.stepCount++;
        if (this.stepCount >= gameRule.getTurnAroundStep()) {
            this.remainMillis += Long.valueOf(Math.round(gameRule.getTurnAroundPlusTime() * 1000));
            this.stepCount = 0;
        }
    }
}
