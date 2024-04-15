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
public class FullFriendRequestDto {
    int senderId;
    int receiverId;
    String createdAt;

    public FullFriendRequestDto(ResultSet rs) throws SQLException {
        senderId = rs.getInt("sender_id");
        receiverId = rs.getInt("receiver_id");
        createdAt = rs.getTimestamp("created_at").toString();
    }
}
