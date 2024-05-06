package modules.game_chesslib.common.nested;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.MoveHistory;
import modules.game_chesslib.dto.GamePlayerDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerJoinedResponse {
    String fen;
    boolean white;
    GamePlayerDto gamePlayer;
    List<MoveHistory> moveHistories;

    public PlayerJoinedResponse(String fen, boolean white, GamePlayerDto gamePlayer, List<MoveHistory> moveHistories,
            GameRule gameRule) {
        this.fen = fen;
        this.white = white;
        this.gamePlayer = gamePlayer;
        this.moveHistories = moveHistories;
        this.gameRule = gameRule;
    }

    public PlayerJoinedResponse(String fen, boolean white, GamePlayerDto gamePlayer, List<MoveHistory> moveHistories) {
        this.fen = fen;
        this.white = white;
        this.gamePlayer = gamePlayer;
        this.moveHistories = moveHistories;
    }

    Long whiteRemainMillis;
    Long blackRemainMillis;
    GameRule gameRule;
}
