package modules.game.custom.piece;


import lombok.Getter;
import lombok.Setter;
import modules.game.custom.ChessGame;
import modules.game.custom.Position;

@Getter
@Setter
public class King extends ChessPiece {


    public King(Position position, boolean white, ChessGame chessGame) {
        super(position, white, chessGame);
    }

    Rook rookInCastle;

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

        // Check if the move is within one square in any direction
        if (Math.abs(toRow - fromRow) <= 1 && Math.abs(toCol - fromCol) <= 1) {
            return true;
        }

        return false;
    }

    public boolean isCastlingAllowed() {
        // Check if the king and rook have not moved
        if (isMoved() || rookInCastle.isMoved()) {
            return false;
        }
        // Check empty squares between king and root
        if (!isPathClear(this.getFrom().getRow(), this.getFrom().getCol(), rookInCastle.getFrom().getRow(),
                rookInCastle.getFrom().getCol())) {
            return false;
        }
        // Check if the king is not in check
        if (isSquareAttacked(from.getRow(), from.getCol())) {
            return false;
        }
        // Check if the king passes through a square that is attacked by an enemy piece
        if (isSquareAttacked(from.getRow(), rookInCastle.getFrom().getCol() + 1)) {
            return false;
        }
        return true;
    }

    public void doCastle() {
        int rookRow = rookInCastle.getFrom().getRow();
        int rookCol = rookInCastle.getFrom().getCol() + 2;
        int kingRow = rookInCastle.getFrom().getRow();
        int kingCol = rookInCastle.getFrom().getCol() + 1;
        board[rookRow][rookCol] = rookInCastle;
        board[rookInCastle.getFrom().getRow()][rookInCastle.getFrom().getCol()] = null;
        board[kingRow][kingCol] = this;
        board[this.getFrom().getRow()][this.getFrom().getCol()] = null;

        rookInCastle.setFrom(new Position(rookRow, rookCol));
        this.setFrom(new Position(kingRow, kingCol));
    }

    @Override
    public String toString() {
        return isWhite() ? ChessPieceType.WhiteKing : ChessPieceType.BlackKing;
    }
}