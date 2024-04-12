package modules.chat.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithLastMessageDto {
    private int id;
    private String displayName;
    private boolean online;
    private String avatarUrl;
    MessageResponseDto message;

    public UserWithLastMessageDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        displayName = rs.getString("display_name");
        online = rs.getBoolean("online");
        avatarUrl = rs.getString("avatar_url");
        int messageId = rs.getInt("m_id");
        String content = rs.getString("content");
        int senderId = rs.getInt("sender_id");
        int receiverId = rs.getInt("receiver_id");
        Date sendedAt = rs.getTimestamp("sended_at");
        message = new MessageResponseDto(messageId, content, senderId, receiverId, sendedAt);
    }
}
