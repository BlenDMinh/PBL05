package modules.game.custom.player;

import javax.swing.text.Position;

import modules.game.custom.ChessGame;

public class UserPlayer extends GamePlayer {

    @Override
    void makeAction(Position from, Position to) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'makeAction'");
    }

    public UserPlayer(int id, ChessGame game) {
        super(id, game);
    }
}
