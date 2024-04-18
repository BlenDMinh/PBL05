package modules.friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import modules.friend.dto.FriendDto;
import modules.friend.dto.FullFriendRequestDto;
import modules.friend.dto.PaginationFriendDto;
import modules.friend.dto.PaginationFullFriendRequestDto;
import utils.ConnectionPool;

public class FriendRepository {
    static Logger logger = Logger.getLogger("ChatRepository");

    public boolean createFriendRequest(int senderId, int receiverId) {
        boolean created = false;
        Connection conn = null;
        String sql = "insert into friend_requests (sender_id, receiver_id) VALUES (?, ?)";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            int count = stmt.executeUpdate();
            if (count > 0) {
                created = true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return created;
    }

    public int countFriendRequestOfReciever(int receiverId) {
        int result = 0;
        String query = "select count(*) from friend_requests where receiver_id = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, receiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return result;
    }

    public PaginationFullFriendRequestDto getPaginationFriendRequestOfReceiver(int receiverId, int page, int size) {
        List<FullFriendRequestDto> friendRequestDtos = new ArrayList<>();
        int totalElements = 0, totalPages = 0;
        Connection conn = null;
        String sql = "select * from friend_requests where receiver_id = ? order by created_at offset ? limit ?";
        try {
            conn = ConnectionPool.getConnection();
            totalElements = this.countFriendRequestOfReciever(receiverId);
            totalPages = (int) Math.ceil((double) totalElements / size);
            int upToFull = totalPages * size - totalElements;
            int offset = 0;
            if (totalElements >= size) {
                offset = (totalPages - page) * size - upToFull;
            }
            if (offset < 0){
                size = size + offset;
                offset = 0;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, receiverId);
            stmt.setInt(2, offset);
            stmt.setInt(3, size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friendRequestDtos.add(new FullFriendRequestDto(rs));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        Collections.reverse(friendRequestDtos);
        return new PaginationFullFriendRequestDto(friendRequestDtos, totalPages, totalElements);
    }

    public boolean deleteFriendRequestAndCreateFriendShip(int senderId, int receiverId) {
        boolean created = false;
        Connection conn = null;
        String sqlDelete = "delete from friend_requests where sender_id = ? and receiver_id = ?";
        String sqlInsert = "insert into friendships (user_id, friend_id) VALUES (?, ?)";
        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(sqlDelete);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            int count = stmt.executeUpdate();
            if (count == 0) {
                throw new Exception();
            }
            stmt = conn.prepareStatement(sqlInsert);
            stmt.setInt(1, Math.min(senderId, receiverId));
            stmt.setInt(2, Math.max(senderId, receiverId));
            count = stmt.executeUpdate();
            if (count > 0) {
                created = true;
                conn.commit();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException e1) {

            }
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return created;
    }

    public boolean deleteFriendRequest(int senderId, int receiverId) {
        boolean deleted = false;
        Connection conn = null;
        String sql = "delete from friend_requests where sender_id = ? and receiver_id = ?";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            int count = stmt.executeUpdate();
            if (count > 0) {
                deleted = true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return deleted;
    }

    public boolean deleteFriendShip(int userId, int friendId) {
        boolean deleted = false;
        Connection conn = null;
        String sql = "delete from friendships where user_id = ? and friend_id = ?";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Math.min(userId, friendId));
            stmt.setInt(2, Math.max(userId, friendId));
            int count = stmt.executeUpdate();
            if (count > 0) {
                deleted = true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return deleted;
    }

    public int countFriend(int userId, String keyword) {
        int result = 0;
        String sql = "select count(*) from users inner join"
                + " (select * from friendships where user_id = ? or friend_id = ?) as t1"
                + " on users.id = "
                + " case when t1.user_id = ? then t1.friend_id"
                + " else t1.user_id end"
                + " where users.display_name ilike ? or users.email ilike ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setString(4, "%" + keyword + "%");
            stmt.setString(5, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return result;
    }

    public PaginationFriendDto getPaginationFriendDto(int userId, int page, int size, String keyword) {
        List<FriendDto> friendDtos = new ArrayList<>();
        int totalElements = 0, totalPages = 0;
        Connection conn = null;
        String sql = "select * from users inner join"
                + " (select * from friendships where user_id = ? or friend_id = ?) as t1"
                + " on users.id = "
                + " case when t1.user_id = ? then t1.friend_id"
                + " else t1.user_id end"
                + " where users.display_name ilike ? or users.email ilike ?"
                + " offset ? limit ?";
        try {
            conn = ConnectionPool.getConnection();
            totalElements = this.countFriend(userId, keyword);
            totalPages = (int) Math.ceil((double) totalElements / size);
            int offset = 0;
            if (totalElements >= size) {
                offset = (page - 1) * size;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setString(4, "%" + keyword + "%");
            stmt.setString(5, "%" + keyword + "%");
            stmt.setInt(6, offset);
            stmt.setInt(7, size);
            stmt.setString(5, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friendDtos.add(new FriendDto(rs));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return new PaginationFriendDto(friendDtos, totalPages, totalElements);
    }
}
