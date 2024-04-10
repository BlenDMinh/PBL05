package modules.game_chesslib.custom;

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
    int minutePerTurn;
    int totalMinutePerPlayer;
    int turnAroundStep;
    int turnAroundPlusTime;
}
