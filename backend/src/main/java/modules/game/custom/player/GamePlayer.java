package modules.game.custom.player;

import javax.swing.text.Position;

import modules.game.custom.ChessGame;
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

    abstract void makeAction(Position from, Position to);
}
