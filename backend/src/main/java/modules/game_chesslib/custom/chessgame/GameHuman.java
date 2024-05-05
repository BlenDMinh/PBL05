package modules.game_chesslib.custom.chessgame;

import com.github.bhlangonijr.chesslib.Board;

import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.player.HumanPlayer;

import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.Session;

public class GameHuman extends ChessGame {

    protected GameRule gameRule;

    public GameRule getGameRule() {
        return gameRule;
    }

    public void setGameRule(GameRule gameRule) {
        this.gameRule = gameRule;
    }

    public GameHuman(String id, int whiteId, int blackId, GameRule gameRule) {
        this.id = id;
        this.gameRule = gameRule;
        this.whitePlayer = new HumanPlayer(whiteId, this, true, gameRule);
        this.blackPlayer = new HumanPlayer(blackId, this, false, gameRule);
        this.board = new Board();
    }

    public HumanPlayer getHumanWhitePlayer() {
        return (HumanPlayer) whitePlayer;
    }

    public void setHumanWhitePlayer(HumanPlayer whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public HumanPlayer getHumanBlackPlayer() {
        return (HumanPlayer) blackPlayer;
    }

    public void setHumanBlackPlayer(HumanPlayer blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    protected Session whiteSession, blackSession;

    public Session getWhiteSession() {
        return whiteSession;
    }

    public void setWhiteSession(Session whiteSession) {
        this.whiteSession = whiteSession;
    }

    public Session getBlackSession() {
        return blackSession;
    }

    public void setBlackSession(Session blackSession) {
        this.blackSession = blackSession;
    }

    protected long lastMoveTime = 0;

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    protected TimerTask timePerTurnEndTask, timerPerPlayerEndTask;

    public void startTimer(TimerTask timePerTurnEndTask, TimerTask timerPerPlayerEndTask, boolean isWhite) {
        this.timePerTurnEndTask = timePerTurnEndTask;
        this.timerPerPlayerEndTask = timerPerPlayerEndTask;
        Timer timer = new Timer();
        if (this.gameRule.getMinutePerTurn() != -1) {
            timer.schedule(timePerTurnEndTask, Long.valueOf(Math.round(this.gameRule.getMinutePerTurn() * 60 * 1000)));
        }
        if (this.gameRule.getTotalMinutePerPlayer() != -1) {
            timer.schedule(timerPerPlayerEndTask,
                    (isWhite ? this.getHumanWhitePlayer() : this.getHumanBlackPlayer()).getRemainMillis());
        }
    }

    public void cancelTimer() {
        if (this.timePerTurnEndTask != null)
            this.timePerTurnEndTask.cancel();
        if (this.timerPerPlayerEndTask != null)
            this.timerPerPlayerEndTask.cancel();
    }
}
