package modules.game.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.custom.piece.ChessPiece;
import modules.game.custom.player.GamePlayer;
import modules.game.custom.player.UserPlayer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChessGame {
    int id;
    GamePlayer player1;
    GamePlayer player2;
    boolean player1Turn;
    GameRule gameRule;
    ChessPiece[][] gameState = new ChessPiece[8][8];

    public ChessGame(int id, int player1Id, int player2Id) {
        this.id = id;
        this.player1Turn = true;
        this.player1 = new UserPlayer(player1Id, this);
        this.player2 = new UserPlayer(player2Id, this);
    }

    public void nextTurn() {
        player1Turn = !player1Turn;
    }
}
