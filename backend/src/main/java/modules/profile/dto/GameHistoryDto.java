package modules.profile.dto;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameHistoryDto {
    String id;
    int player1Id;
    int player2Id;
    String status;
    String createdAt;

    public GameHistoryDto(ResultSet rs) throws SQLException{
        id = rs.getString("id");
        player1Id=rs.getInt("player1_id");
        player2Id=rs.getInt("player2_id");
        status = GameStatus.fromInt(rs.getInt("status")).name();
        createdAt = rs.getTimestamp("created_at").toString();
    }
}
