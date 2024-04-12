package modules.chat.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageResponseDto {
    Integer id;
    String content;
    Integer senderId;
    Integer receiverId;
    String sendedAt;

    public MessageResponseDto(Integer id, String content, Integer senderId, Integer receiverId, Date sendedAt) {
        this.id = id;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.sendedAt = sendedAt.toString();
    }

    public MessageResponseDto(String content) {
        this.content = content;
    }

    public MessageResponseDto(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        content = rs.getString("content");
        senderId = rs.getInt("sender_id");
        receiverId = rs.getInt("receiver_id");
        sendedAt = rs.getTimestamp("sended_at").toString();
    }
}
