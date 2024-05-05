package modules.game_chesslib.algo;

import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LastMoveInfo {
    String pieceMove = null;
    String pieceCapture = null;
    Side side = null;
    Move move = null;
    String promotion = null;
}
