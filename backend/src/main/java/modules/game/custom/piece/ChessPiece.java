package modules.game.custom.piece;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.custom.Position;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ChessPiece {
  Position from;
  boolean white;
  ChessPiece[][] board;

  public abstract boolean isValidMove(Position to);

  public void doMove(Position to) {
    int fromRow = from.getRow();
    int fromCol = from.getCol();
    int toRow = to.getRow();
    int toCol = to.getCol();

    // Update the position of the pawn
    this.getFrom().setRow(toRow);
    this.getFrom().setCol(toCol);

    // Move the pawn on the board
    board[toRow][toCol] = this;
    board[fromRow][fromCol] = null;
  }
}
