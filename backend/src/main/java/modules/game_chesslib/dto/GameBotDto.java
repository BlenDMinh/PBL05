package modules.game_chesslib.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameBotDto implements GamePlayerDto{
    boolean white;
    int difficulty;
}
