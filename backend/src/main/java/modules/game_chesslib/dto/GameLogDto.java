package modules.game_chesslib.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;

import common.socket.SocketMessageDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameLogDto {
    int id;
    String fen;
    SocketMessageDto message;
    int playerId;
    String gameId;
    String createdAt;

    public GameLogDto(ResultSet rs) throws SQLException {
        Gson gson = new Gson();
        id = rs.getInt("id");
        fen = rs.getString("fen");
        message = gson.fromJson(rs.getString("message"), SocketMessageDto.class);
        playerId = rs.getInt("player_id");
        gameId = rs.getString("game_id");
        createdAt = rs.getTimestamp("created_at").toString();
    }
}
