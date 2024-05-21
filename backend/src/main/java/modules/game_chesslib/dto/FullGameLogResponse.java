package modules.game_chesslib.dto;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.profile.dto.PlayerDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FullGameLogResponse {
    GameDto game;
    PlayerDto whitePlayer, blackPlayer;
    List<GameLogDto> gameLogs;
}
