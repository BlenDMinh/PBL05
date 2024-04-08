package modules.game.custom.piece;


import modules.game.custom.ChessGame;
import modules.game.custom.Position;

public class Knight extends ChessPiece {

    public Knight(Position position, boolean white, ChessGame chessGame) {
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

        // Knight moves in "L" shape: 2 squares in one direction and 1 square in another
        // direction
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    @Override
    public String toString() {
        return isWhite() ? ChessPieceType.WhiteKnight : ChessPieceType.BlackKnight;
    }
}
