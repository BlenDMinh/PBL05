package modules.chat.dto;

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
public class UserInChatDto {
    private int id;
    private String displayName;
    private boolean online;
    private String avatarUrl;
    private String lastChatTime;

    public UserInChatDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        displayName = rs.getString("display_name");
        online = rs.getBoolean("online");
        avatarUrl = rs.getString("avatar_url");
        lastChatTime = rs.getTimestamp("nearest_date").toString();
    }
}
