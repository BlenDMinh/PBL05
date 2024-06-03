package modules.game_chesslib.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import common.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    String id;
    GameStatus status;
    int whiteId;
    int blackId;
    String createdAt;
    RuleSetDto ruleSet;

    public GameDto(ResultSet rs) throws JsonMappingException, JsonProcessingException, SQLException {
        id = rs.getString("id");
        status = GameStatus.fromInt(rs.getInt("status"));
        whiteId = rs.getInt("player1_id");
        blackId = rs.getInt("player2_id");
        createdAt = rs.getTimestamp("created_at").toString();
        Gson gson = new Gson();
        ruleSet = new RuleSetDto(rs.getInt("ruleset_id"),
                rs.getString("name"),
                gson.fromJson(rs.getString("detail"), JsonObject.class),
                gson.fromJson(rs.getString("description"), JsonObject.class),
                rs.getBoolean("published"));
    }
}
