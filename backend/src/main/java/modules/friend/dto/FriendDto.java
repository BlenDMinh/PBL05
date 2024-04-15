package modules.friend.dto;

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
public class FriendDto {
    private int id;
    private String displayName;
    private String email;
    private boolean online;
    private String avatarUrl;
    private int elo;
    private String friendFrom;

    public FriendDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        displayName = rs.getString("display_name");
        online = rs.getBoolean("online");
        avatarUrl = rs.getString("avatar_url");
        email = rs.getString("email");
        elo = rs.getInt("elo");
        friendFrom = rs.getTimestamp("created_at").toString();
    }
}
