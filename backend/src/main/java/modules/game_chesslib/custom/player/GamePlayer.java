package modules.game_chesslib.custom.player;

import modules.game_chesslib.custom.chessgame.ChessGame;

public abstract class GamePlayer {
    protected ChessGame game;
    protected boolean white;

    public GamePlayer() {
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public boolean isWhite() {
        return white;
    }

    public void setWhite(boolean white) {
        this.white = white;
    }

}
