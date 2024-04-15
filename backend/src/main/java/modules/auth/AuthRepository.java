package modules.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import common.dto.UserPasswordDto;
import utils.ConnectionPool;

public class AuthRepository {
    static Logger logger = Logger.getLogger("AuthRepository");

    public UserPasswordDto getUserByEmail(String email) {
        UserPasswordDto userPasswordDto = null;
        String query = "select * from users where email = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userPasswordDto = new UserPasswordDto(rs);
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
        return userPasswordDto;
    }

    public String register(String displayName, String email, String password, String verifyCode) {
        Connection conn = null;
        String id = null;
        String sql = "insert into player_registers (display_name, email, password, verify_code) VALUES (?, ?, ?) returning id";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, displayName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(3, verifyCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getString("id");
            } else {
                throw new SQLException("Cannot create message");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return id;
    }
}
