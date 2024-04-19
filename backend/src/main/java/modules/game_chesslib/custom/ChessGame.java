package modules.game_chesslib.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;

import common.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.custom.player.BotPlayer;
import modules.game_chesslib.custom.player.GamePlayer;
import modules.game_chesslib.custom.player.UserPlayer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChessGame {
    String id;
    Board board;
    GameRule gameRule;
    GamePlayer player1;
    GamePlayer player2;
    GameStatus status = GameStatus.WAITING;

    List<MoveHistory> moveHistories = new ArrayList<>();
    Date player1StartTime, player2StartTime, player1LastTime, player2LastTime;

    public ChessGame(String id, int player1Id, int player2Id) {
        this.id = id;
        this.player1 = new UserPlayer(player1Id, this, true);
        this.player2 = new UserPlayer(player2Id, this, false);
        this.board = new Board();
    }

    public ChessGame(String id, int humanId, GameDifficulty difficulty, boolean botWhite) {
        this.id = id;
        this.player1 = new UserPlayer(humanId, this, !botWhite);
        this.player2 = new BotPlayer(this, botWhite, difficulty);
        this.board = new Board();
    }

    public void addMoveHistory(Move chessMove) {
        this.moveHistories.add(new MoveHistory(board.getPiece(chessMove.getFrom()).name(), chessMove.getFrom().name(),
                chessMove.getTo().name(),
                chessMove.getPromotion().name()));
    }
}
