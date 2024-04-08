package modules.game.custom.piece;

import javax.websocket.Session;

import modules.game.custom.ChessGame;
import modules.game.custom.Position;

public class Rook extends ChessPiece {
    public Rook(Position position, boolean white, ChessGame chessGame, Session playerSession) {
        super(position, white, chessGame);
    }

    public Rook(Position position, boolean white, ChessGame chessGame) {
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

        // Check if the move is vertical or horizontal
        if (fromRow == toRow || fromCol == toCol) {
            // Check if there are no pieces blocking the path
            if (isPathClear(fromRow, fromCol, toRow, toCol)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return isWhite() ? ChessPieceType.WhiteRook : ChessPieceType.BlackRook;
    }
}
