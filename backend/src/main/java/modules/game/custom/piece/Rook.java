package modules.game.custom.piece;

import modules.game.custom.Position;

public class Rook extends ChessPiece{
    public Rook(Position position, boolean white, ChessPiece[][] board){
        super(position, white,board);
    }

    @Override
    public boolean isValidMove(Position to) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isValidMove'");
    }

}
