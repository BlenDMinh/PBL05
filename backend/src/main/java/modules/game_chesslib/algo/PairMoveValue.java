package modules.game_chesslib.algo;
import com.github.bhlangonijr.chesslib.move.Move;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PairMoveValue {
    Move bestMove;
    double value;
}
