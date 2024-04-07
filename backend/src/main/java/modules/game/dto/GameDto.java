package modules.game.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modules.game.common.GameStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    int id;
    GameStatus status;
    int player1Id;
    int player2Id;
    Date createdAt;

    public GameDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        status = GameStatus.fromInt(rs.getInt("status"));
        player1Id = rs.getInt("player1_id");
        player2Id = rs.getInt("player2_id");
        createdAt = rs.getDate("created_at");
    }
}
