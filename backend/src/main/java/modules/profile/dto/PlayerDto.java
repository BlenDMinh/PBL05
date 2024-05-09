package modules.profile.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private int id;
    private String displayName;
    private String email;
    private boolean online;
    private String avatarUrl;
    private int elo;
    private String createdAt;

    public PlayerDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        displayName = rs.getString("display_name");
        email = rs.getString("email");
        online = rs.getBoolean("online");
        avatarUrl = rs.getString("avatar_url");
        elo = rs.getInt("elo");
        createdAt = rs.getTimestamp("created_at").toString();
    }
}
