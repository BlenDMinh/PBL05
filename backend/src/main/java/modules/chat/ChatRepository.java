package modules.chat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import modules.chat.dto.MessageResponseDto;
import modules.chat.dto.UserInChatDto;
import utils.ConnectionPool;

public class ChatRepository {
    static Logger logger = Logger.getLogger("ChatRepository");

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
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return new MessageResponseDto(messageId, content, senderId, receiverId, sendedAt);
    }

    public List<UserInChatDto> getUserInChatOfSender(int senderId) {
        List<UserInChatDto> userInChatDtos = new ArrayList<>();
        String query = "SELECT id, avatar_url, display_name, online, m.* FROM users"
                + " INNER JOIN (SELECT receiver_id, MAX(now()::TIMESTAMP - sended_at::TIMESTAMP) AS nearest_date FROM messages WHERE sender_id = ?"
                + " GROUP BY receiver_id"
                + " ORDER BY nearest_date DESC) AS m"
                + " ON users.id = m.receiver_id";

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userInChatDtos.add(new UserInChatDto(rs));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            // Release resources in a finally block
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return userInChatDtos;
    }
}
