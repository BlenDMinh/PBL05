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
    double minutePerTurn;
    double totalMinutePerPlayer;
    int turnAroundStep;
    double turnAroundPlusTime;
}
