package modules.game_chesslib.custom.player;

import lombok.Getter;
import lombok.Setter;
import modules.game_chesslib.custom.GameDifficulty;
import modules.game_chesslib.custom.chessgame.ChessGame;

@Getter
@Setter
public class BotPlayer extends GamePlayer {
    GameDifficulty difficulty;
    double sum = 0;

    public BotPlayer(ChessGame chessGame, boolean white, GameDifficulty difficulty) {
        this.difficulty = difficulty;
        this.setGame(game);
        this.setWhite(white);
    }

    public BotPlayer(ChessGame chessGame, boolean white) {
        this.difficulty = GameDifficulty.EASIEST;
        this.setGame(game);
        this.setWhite(white);
    }
}
