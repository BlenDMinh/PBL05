package modules.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import common.Role;
import modules.profile.dto.GameHistoryDto;
import modules.profile.dto.PaginationGameHistoryDto;
import modules.profile.dto.PlayerDto;
import utils.ConnectionPool;

public class ProfileRepository {
    static Logger logger = Logger.getLogger("ProfileRepository");

    public PlayerDto getPlayerById(int id) {
        PlayerDto playerDto = null;
        String query = "select * from users where id = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                playerDto = new PlayerDto(rs);
            }
        } catch (SQLException e) {
            // Handle database-related exceptions
            logger.error("SQLException occurred: " + e.getMessage(), e);
            // Optionally, you might throw a custom exception or perform error recovery
        } catch (Exception e) {
            // Handle other types of exceptions
            logger.error("Exception occurred: " + e.getMessage(), e);
            // Optionally, you might throw a custom exception or perform error recovery
        } finally {
            // Release resources in a finally block
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return playerDto;
    }

    public int countGameHistoryByPlayerId(int playerId) {
        int result = 0;
        String query = "select count(*) from games"
                + " where (player1_id = ? or player2_id = ?)"
                + " and status in (2,3,4)";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId);
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

    public PaginationGameHistoryDto getPaginationGameHistoryByPlayerId(int playerId, int page, int size) {
        List<GameHistoryDto> gameHistoryDtos = new ArrayList<>();
        String query = "select * from games"
                + " where (player1_id = ? or player2_id = ?)"
                + " and status in (2,3,4)"
                + " order by created_at asc"
                + " offset ? limit ?";
        int totalElements = 0, totalPages = 0;
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            totalElements = this.countGameHistoryByPlayerId(playerId);
            totalPages = (int) Math.ceil((double) totalElements / size);
            int upToFull = totalPages * size - totalElements;
            int offset = 0;
            if (totalElements >= size) {
                offset = (totalPages - page) * size - upToFull;
            }
            if (offset < 0) {
                size = size + offset;
                offset = 0;
            }
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, playerId);
            stmt.setInt(2, playerId);
            stmt.setInt(3, offset);
            stmt.setInt(4, size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                gameHistoryDtos.add(new GameHistoryDto(rs));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            // Release resources in a finally block
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        Collections.reverse(gameHistoryDtos);
        return new PaginationGameHistoryDto(gameHistoryDtos, totalPages, totalElements);
    }

    public boolean updateAvartarUrl(int userId, String url) {
        boolean result = false;
        Connection conn = null;
        String sql = "update users set avatar_url = ? where id = ?";
        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, url);
            stmt.setInt(2, userId);
            int numRow = stmt.executeUpdate();
            if (numRow == 0) {
                throw new Exception();
            }
            result = true;
            conn.commit();
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
        return result;
    }

    public List<PlayerDto> getTopNPlayerByDisplaynameOrEmail(int n, String keyword) {
        List<PlayerDto> playerDtos = new ArrayList<>();
        String query = "select * from users"
                + " where (email ilike ?"
                + " or display_name ilike ?)"
                + " and role = ?"
                + " offset 0 limit ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setInt(3, Role.PLAYER.getValue());
            stmt.setInt(4, n);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                playerDtos.add(new PlayerDto(rs));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            // Release resources in a finally block
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return playerDtos;
    }
}
