package modules.game_chesslib.custom.chessgame;

import com.github.bhlangonijr.chesslib.Board;

import modules.game_chesslib.custom.GameDifficulty;
import modules.game_chesslib.custom.player.BotPlayer;
import modules.game_chesslib.custom.player.HumanPlayer;

public class GameBot extends ChessGame {

    public GameBot(String id, int humanId, GameDifficulty difficulty, boolean botWhite) {
        this.id = id;
        if (botWhite) {
            this.blackPlayer = new HumanPlayer(humanId, this, false);
            this.whitePlayer = new BotPlayer(this, true, difficulty);
        } else {
            this.whitePlayer = new HumanPlayer(humanId, this, true);
            this.blackPlayer = new BotPlayer(this, false, difficulty);
        }
        this.board = new Board();
    }

    public HumanPlayer getHumanPlayer() {
        return (HumanPlayer) (this.whitePlayer instanceof HumanPlayer ? this.whitePlayer : this.blackPlayer);
    }

    public BotPlayer getBotPlayer() {
        return (BotPlayer) (this.whitePlayer instanceof BotPlayer ? this.whitePlayer : this.blackPlayer);
    }
}
