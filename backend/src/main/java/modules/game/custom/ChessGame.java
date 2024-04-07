package modules.game.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.custom.piece.ChessPiece;
import modules.game.custom.player.GamePlayer;

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

    public ChessGame(int id) {
        this.id = id;
        this.player1Turn = true;
    }
}
