package modules.game.custom;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.custom.player.GamePlayer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameAction {
    int id;
    GamePlayer player;
    Position from;
    Position to;
    Date time;
}
