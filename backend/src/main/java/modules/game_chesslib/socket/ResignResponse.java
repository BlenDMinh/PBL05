package modules.game_chesslib.socket;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.custom.MoveHistory;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResignResponse {
    String fen;
    boolean white;
    boolean resignSide;
    List<MoveHistory> moveHistories;
    public ResignResponse(String fen, boolean white, boolean resignSide, List<MoveHistory> moveHistories) {
        this.fen = fen;
        this.white = white;
        this.resignSide = resignSide;
        this.moveHistories = moveHistories;
    }
    Long whiteRemainMillis;
    Long blackRemainMillis;
}
