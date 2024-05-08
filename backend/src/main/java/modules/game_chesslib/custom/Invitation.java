package modules.game_chesslib.custom;

import java.util.UUID;

import javax.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    String id;
    Session from;
    GameRule gameRule;
    GameSide fromSide;
    boolean rated;

    public Invitation(Session from, GameRule gameRule, GameSide fromSide, boolean rated) {
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.gameRule = gameRule;
        this.fromSide = fromSide;
        this.rated = rated;
    }
}
