package modules.game.common.nested;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.custom.Position;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    String rawGameState;
    boolean white;
    Position from;
    Position to;
    String piece;
}
