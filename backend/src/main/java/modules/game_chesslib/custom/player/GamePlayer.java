package modules.game_chesslib.custom.player;

import modules.game_chesslib.custom.ChessGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class GamePlayer {
    protected int id;
    protected ChessGame game;
    protected boolean white;

}
