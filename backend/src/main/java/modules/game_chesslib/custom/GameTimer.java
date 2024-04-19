package modules.game_chesslib.custom;

import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.Session;

import com.github.bhlangonijr.chesslib.Side;

import common.GameStatus;

public class GameTimer {
    ChessGame chessGame;
    TimerTask timerTask;
    Session player1Session, player2Session;

    public GameTimer(ChessGame chessGame, Session player1Session, Session player2Session) {
        this.chessGame = chessGame;
        this.player1Session = player1Session;
        this.player2Session = player2Session;
        this.timerTask = new TimerTask() {

            @Override
            public void run() {
                long millisecondsPerTurn = chessGame.getGameRule().minutePerTurn * 60 * 1000;
                long totalMillisecondsPerPlayer = chessGame.getGameRule().minutePerTurn * 60 * 1000;
                while (chessGame.getStatus().equals(GameStatus.PLAYING)) {
                    long currentTime = System.currentTimeMillis();
                    if (chessGame.getBoard().getSideToMove().equals(Side.WHITE)) {

                        if (millisecondsPerTurn != 0
                                && currentTime - chessGame.getPlayer1LastTime().getTime() > millisecondsPerTurn) {

                        }

                        if (totalMillisecondsPerPlayer != 0
                                && currentTime
                                        - chessGame.getPlayer1StartTime().getTime() > totalMillisecondsPerPlayer) {

                        }
                    } else {

                    }
                }
            }

        };
    }

    public void start() {
        Timer timer = new Timer(chessGame.getId());
        timer.schedule(timerTask, 1000L);
    }
}
