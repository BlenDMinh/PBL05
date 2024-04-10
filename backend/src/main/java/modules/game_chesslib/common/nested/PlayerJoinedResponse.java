package modules.game_chesslib.common.nested;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.dto.GamePlayerDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinedResponse {
    String fen;
    boolean white;
    GamePlayerDto gamePlayer;
}
