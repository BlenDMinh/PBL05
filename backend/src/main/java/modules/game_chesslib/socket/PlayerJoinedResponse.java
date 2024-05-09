package modules.game_chesslib.socket;

import java.util.List;

import common.GameStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.MoveHistory;
import modules.game_chesslib.dto.GamePlayerDto;

@Getter
@Setter
@NoArgsConstructor
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
    Long whiteRemainMillisInTurn;
    Long blackRemainMillisInTurn;
    GameRule gameRule;
    GameStatus gameStatus;

    public PlayerJoinedResponse(String fen, boolean white, GamePlayerDto gamePlayer, List<MoveHistory> moveHistories,
            Long whiteRemainMillis, Long blackRemainMillis, GameRule gameRule, Long whiteRemainMillisInTurn,
            Long blackRemainMillisInTurn, GameStatus gameStatus) {
        this.fen = fen;
        this.white = white;
        this.gamePlayer = gamePlayer;
        this.moveHistories = moveHistories;
        this.whiteRemainMillis = whiteRemainMillis;
        this.blackRemainMillis = blackRemainMillis;
        this.whiteRemainMillisInTurn = whiteRemainMillisInTurn;
        this.blackRemainMillisInTurn = blackRemainMillisInTurn;
        this.gameRule = gameRule;
        this.gameStatus = gameStatus;
    }
}
