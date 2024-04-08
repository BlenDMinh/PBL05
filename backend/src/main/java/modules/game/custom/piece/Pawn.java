package modules.game.custom.piece;

import modules.game.custom.ChessGame;
import modules.game.custom.Position;

public class Pawn extends ChessPiece {

    public Pawn(Position from, boolean white, ChessGame chessGame) {
        super(from, white, chessGame);
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

        // White pawn first double step
        if (isWhite() && !isMoved() && toCol == fromCol && toRow == fromRow + 2 && getBoard()[toRow][toCol] == null &&
                getBoard()[fromRow + 1][fromCol] == null) {
            return true;
        }

        // Black pawn first double step
        if (!isWhite() && !isMoved() && toCol == fromCol && toRow == fromRow - 2 && getBoard()[toRow][toCol] == null &&
                getBoard()[fromRow - 1][fromCol] == null) {
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

    public boolean isReachedEndOfRow() {
        int row = this.getFrom().getRow();
        if ((isWhite() && row == 7) || (!isWhite() && row == 0)) {
            return true;
        }
        return false;
    }

    public void promotePawn(String pieceType) {
        // Instantiate the promoted piece based on the provided pieceType
        ChessPiece promotedPiece = null;
        int row = this.getFrom().getRow();
        int col = this.getFrom().getCol();
        switch (pieceType.toLowerCase()) {
            case "Rook":
                promotedPiece = new Rook(new Position(row, col), isWhite(), getChessGame());
                break;
            case "Queen":
                promotedPiece = new Queen(new Position(row, col), isWhite(), getChessGame());
                break;
            case "Bishop":
                promotedPiece = new Bishop(new Position(row, col), isWhite(), getChessGame());
                break;
            case "Knight":
                promotedPiece = new Knight(new Position(row, col), isWhite(), getChessGame());
                break;
            default:
                // Handle invalid piece type
                break;
        }

        // Replace the pawn with the promoted piece on the board
        if (promotedPiece != null) {
            getChessGame().getBoard()[row][col] = promotedPiece;
        }
    }

    @Override
    public String toString() {
        return isWhite() ? ChessPieceType.WhitePawn : ChessPieceType.BlackPawn;
    }
}
