package modules.game_chesslib.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game_chesslib.custom.GameRule;
import modules.game_chesslib.custom.GameSide;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteToGameRequest {
    int opponentId;
    GameRule gameRule;
    GameSide meSide;
    boolean rated;

    public InviteToGameRequest(int opponentId, GameRule gameRule, GameSide meSide, boolean rated) {
        this.opponentId = opponentId;
        this.gameRule = gameRule;
        this.meSide = meSide;
        this.rated = rated;
    }

    String invitationId;

    public GameSide revertSide() {
        if (meSide == null)
            return null;
        switch (meSide) {
            case WHITE:
                return GameSide.BLACK;
            case BLACK:
                return GameSide.WHITE;
            default:
                return GameSide.RANDOM;
        }
    }
}
