package modules.game.common.nested;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.dto.GamePlayerDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinedResponse {
    String rawGameState;
    boolean white;
    GamePlayerDto gamePlayer;
}
