package modules.game_chesslib.custom.chessgame;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

import common.GameStatus;
import modules.game_chesslib.custom.MoveHistory;
import modules.game_chesslib.custom.player.GamePlayer;

public abstract class ChessGame {
    public ChessGame() {
    }

    protected String id;
    protected Board board;
    protected GamePlayer whitePlayer;
    protected GamePlayer blackPlayer;

    protected GameStatus status = GameStatus.WAITING;
    protected ReentrantLock lock = new ReentrantLock();

    protected List<MoveHistory> moveHistories = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public List<MoveHistory> getMoveHistories() {
        return moveHistories;
    }

    public void setMoveHistories(List<MoveHistory> moveHistories) {
        this.moveHistories = moveHistories;
    }

    public void addMoveHistory(Move chessMove) {
        this.moveHistories.add(new MoveHistory(board.getPiece(chessMove.getFrom()).name(), chessMove.getFrom().name(),
                chessMove.getTo().name(),
                chessMove.getPromotion().name()));
    }

    public Board getBoard() {
        lock.lock();
        try {
            return this.board;
        } finally {
            lock.unlock();
        }
    }

}
