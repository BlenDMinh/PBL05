package modules.ruleset.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RuleSetDto {
    int id;
    String name;
    JsonObject detail;
    JsonObject description;
    boolean published;
    String createdAt;

    public RuleSetDto(ResultSet rs) throws JsonSyntaxException, SQLException {
        Gson gson = new Gson();
        id = rs.getInt("id");
        name = rs.getString("name");
        detail = gson.fromJson(rs.getString("detail"), JsonObject.class);
        description = gson.fromJson(rs.getString("description"), JsonObject.class);
        published = rs.getBoolean("published");
        createdAt = rs.getTimestamp("created_at").toString();
    }
}
