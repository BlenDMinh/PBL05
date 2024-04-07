package modules.game.custom.piece;

import modules.game.custom.Position;

public class Pawn extends ChessPiece {
    public Pawn(Position from, boolean white, ChessPiece[][] board) {
        super(from, white, board);
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

        // White pawn moves forward
        if (isWhite() && toCol == fromCol && toRow == fromRow + 1 && getBoard()[toRow][toCol] == null) {
            return true;
        }

        // Black pawn moves forward
        if (!isWhite() && toCol == fromCol && toRow == fromRow - 1 && getBoard()[toRow][toCol] == null) {
            return true;
        }

        // White pawn captures diagonally
        if (isWhite() && Math.abs(toCol - fromCol) == 1 && toRow == fromRow + 1 &&
                getBoard()[toRow][toCol] != null && !getBoard()[toRow][toCol].isWhite()) {
            return true;
        }

        // Black pawn captures diagonally
        if (!isWhite() && Math.abs(toCol - fromCol) == 1 && toRow == fromRow - 1 &&
                getBoard()[toRow][toCol] != null && getBoard()[toRow][toCol].isWhite()) {
            return true;
        }

        return false;
    }
}
