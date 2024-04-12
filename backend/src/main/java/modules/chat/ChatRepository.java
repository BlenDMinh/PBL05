package modules.chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import modules.chat.dto.MessageResponseDto;
import utils.ConnectionPool;

public class ChatRepository {
    public MessageResponseDto createOne(String content, int senderId, int receiverId) throws Exception {
        Connection conn = null;
        Integer messageId = null;
        Date sendedAt = null;
        String sql = "insert into messages (content, sender_id, receiver_id) VALUES (?, ?, ?) returning id, sended_at";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, content);
            stmt.setInt(2, senderId);
            stmt.setInt(3, receiverId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                messageId = rs.getInt("id");
                sendedAt = rs.getTimestamp("sended_at");
            } else {
                throw new SQLException("Cannot create message");
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return new MessageResponseDto(messageId, content, senderId, receiverId, sendedAt);
    }
}
