package modules.game_chesslib.custom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveHistory {
    String piece;
    String from;
    String to;
    String promotion;
}
