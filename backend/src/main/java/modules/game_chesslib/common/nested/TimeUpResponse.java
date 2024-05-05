package modules.game_chesslib.common.nested;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeUpResponse {
    String fen;
    boolean white;
    Long whiteRemainMillis;
    Long blackRemainMillis;
}
