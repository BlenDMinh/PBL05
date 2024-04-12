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
import modules.chat.dto.UserWithLastMessageDto;
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

    public List<UserWithLastMessageDto> getUserWithLastMessageDtoOfUser(int userId) {
        List<UserWithLastMessageDto> userInChatDtos = new ArrayList<>();
        String query = "SELECT * FROM users INNER JOIN "
                + "(SELECT messages.id as m_id, messages.content, messages.sender_id, messages.receiver_id, messages.sended_at "
                + "FROM messages "
                + "WHERE (LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id), sended_at::TIMESTAMP) "
                + "IN (SELECT LEAST(sender_id, receiver_id) AS id1, GREATEST(sender_id, receiver_id) AS id2, MAX(sended_at::TIMESTAMP) "
                + "FROM messages "
                + "WHERE sender_id = ? OR receiver_id = ? "
                + "GROUP BY id1, id2)) as m2 "
                + "ON users.id = "
                + "CASE WHEN m2.sender_id = ? THEN m2.receiver_id "
                + "ELSE m2.sender_id END "
                + "ORDER BY sended_at DESC;";

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userInChatDtos.add(new UserWithLastMessageDto(rs));
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
