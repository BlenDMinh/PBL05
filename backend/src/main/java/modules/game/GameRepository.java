package modules.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import modules.game.dto.GameDto;
import utils.ConnectionPool;

public class GameRepository {
    static Logger logger = Logger.getLogger("AuthRepository");

    public Integer createOne(int firstId, int secondId, int rulesetId) throws Exception {
        Connection conn = null;
        Integer gameId = null;
        String sql = "insert into games (player1_id, player2_id, ruleset_id) VALUES (?, ?, ?)";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, firstId);
            stmt.setInt(2, secondId);
            stmt.setInt(3, rulesetId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                gameId = rs.getInt(1);
                if (logger.isDebugEnabled()) {
                    logger.info("Game created with id " + gameId.intValue());
                }
            } else {
                throw new SQLException("Cannot create game");
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return gameId;
    }

    public GameDto getById(int id) throws SQLException, Exception {
        Connection conn = null;
        GameDto gameDto = null;
        String sql = "select * from games where id = ?";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                gameDto = new GameDto(rs);
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return gameDto;
    }
}
