package modules.game.custom.piece;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.custom.ChessGame;
import modules.game.custom.Position;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ChessPiece {
  protected Position from;
  protected boolean white;
  protected ChessPiece[][] board;
  protected ChessGame chessGame;
  protected boolean moved;

  public ChessPiece(Position from, boolean white, ChessGame chessGame) {
    this.from = from;
    this.white = white;
    this.chessGame = chessGame;
    this.board = chessGame.getBoard();
    this.moved = false;
  }

  public abstract boolean isValidMove(Position to);

  public void doMove(Position to) {
    int fromRow = from.getRow();
    int fromCol = from.getCol();
    int toRow = to.getRow();
    int toCol = to.getCol();
    this.getFrom().setRow(toRow);
    this.getFrom().setCol(toCol);

    board[toRow][toCol] = this;
    board[fromRow][fromCol] = null;

    setMoved(true);
  }

  // Helper method to check if the path between two positions is clear
  protected boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
    int rowIncrement = Integer.compare(toRow, fromRow);
    int colIncrement = Integer.compare(toCol, fromCol);
    int currentRow = fromRow + rowIncrement;
    int currentCol = fromCol + colIncrement;

    while (currentRow != toRow || currentCol != toCol) {
      if (getBoard()[currentRow][currentCol] != null) {
        return false; // There's a piece blocking the path
      }
      currentRow += rowIncrement;
      currentCol += colIncrement;
    }
    return true;
  }

  protected boolean isSquareAttacked(int row, int col) {
    for (int i = 0; i < 8; i++) {
      for (int j = 0; j < 8; j++) {
        ChessPiece piece = board[i][j];
        if (piece != null && piece.isWhite() != isWhite() && piece.isValidMove(new Position(row, col))) {
          return true;
        }
      }
    }
    return false;
  }
}
