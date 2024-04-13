package modules.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

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
}
