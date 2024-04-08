package modules.game.custom.piece;


import modules.game.custom.ChessGame;
import modules.game.custom.Position;

public class Bishop extends ChessPiece {


    public Bishop(Position position, boolean white, ChessGame chessGame) {
        super(position, white, chessGame);
    }

    @Override
    public boolean isValidMove(Position to) {
        int fromRow = getFrom().getRow();
        int fromCol = getFrom().getCol();
        int toRow = to.getRow();
        int toCol = to.getCol();

        // Check if the destination is within the board boundaries
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }

        // Check if the destination square is occupied by a friendly piece
        if (getBoard()[toRow][toCol] != null && getBoard()[toRow][toCol].isWhite() == isWhite()) {
            return false;
        }

        // Check if the move is diagonal
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        if (rowDiff == colDiff) {
            // Check if there are no pieces blocking the diagonal path
            if (isPathClear(fromRow, fromCol, toRow, toCol)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return isWhite() ? ChessPieceType.WhiteBishop : ChessPieceType.BlackBishop;
    }
}