package modules.game_chesslib.custom;

import com.github.bhlangonijr.chesslib.Board;

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

    public ChessGame(String id, int player1Id, int player2Id) {
        this.id = id;
        this.player1 = new UserPlayer(player1Id, this, true);
        this.player2 = new UserPlayer(player2Id, this, false);
        this.board = new Board();
    }

    public ChessGame(String id, int humanId, GameDifficulty difficulty, boolean botWhite){
        this.id = id;
        this.player1 = new UserPlayer(humanId, this, !botWhite);
        this.player2 = new BotPlayer(this, botWhite, difficulty);
        this.board = new Board();
    }
}
