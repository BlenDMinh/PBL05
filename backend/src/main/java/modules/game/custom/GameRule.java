package modules.game.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameRule {
    int id;
    String ruleName;
    int totalMinutePerPlayer;
    int minutePerTurn;
    int turnAroundStep;
    int turnAroundPlusTime;
}
