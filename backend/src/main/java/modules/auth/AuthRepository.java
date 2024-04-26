package modules.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import common.HttpStatusCode;
import common.dto.UserPasswordDto;
import exceptions.CustomException;
import modules.auth.dto.PlayerRegisterDto;
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

    public String insertIntoPlayerRegister(String displayName, String email, String password, String verifyCode) {
        if (getUserByEmail(email) != null) {
            throw new CustomException(HttpStatusCode.CONFLICT, "Email already registered");
        }
        Connection conn = null;
        String id = null;
        String sql = "insert into player_registers (display_name, email, password, verify_code) VALUES (?, ?, ?, ?) returning id";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, displayName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, verifyCode);
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

    public PlayerRegisterDto findByIdAndVerifyCode(String id, String code) {
        PlayerRegisterDto playerRegisterDto = null;
        String query = "select * from player_registers where id = ? and verify_code = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, id);
            stmt.setString(2, code);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                playerRegisterDto = new PlayerRegisterDto(rs);
            }
        } catch (Exception e) {
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return playerRegisterDto;
    }

    public boolean deletePlayerRegisterAndCreatePlayer(String id, String code) {
        PlayerRegisterDto playerRegisterDto = findByIdAndVerifyCode(id, code);
        if (playerRegisterDto == null) {
            return false;
        }
        boolean result = false;
        Connection conn = null;
        String sqlDelete = "delete from player_registers where id = ? and verify_code = ?";
        String sqlInsert = "insert into users (display_name, email, password) values (?, ?, ?)";
        try {
            conn = ConnectionPool.getConnection();
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement(sqlDelete);
            stmt.setString(1, id);
            stmt.setString(2, code);
            int numRow = stmt.executeUpdate();
            if (numRow == 0) {
                throw new Exception();
            }
            stmt = conn.prepareStatement(sqlInsert);
            stmt.setString(1, playerRegisterDto.getDisplayName());
            stmt.setString(2, playerRegisterDto.getEmail());
            stmt.setString(3, playerRegisterDto.getPassword());
            numRow = stmt.executeUpdate();
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
}
