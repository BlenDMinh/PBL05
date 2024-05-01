package modules.game_chesslib.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.custom.GameDifficulty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameBotDto implements GamePlayerDto{
    boolean white;
    GameDifficulty difficulty;
}
