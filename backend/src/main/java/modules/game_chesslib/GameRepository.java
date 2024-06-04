package modules.game_chesslib;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import common.GameStatus;
import modules.game_chesslib.dto.GameDto;
import modules.game_chesslib.dto.GameLogDto;
import utils.ConnectionPool;

public class GameRepository {
    static Logger logger = Logger.getLogger("GameRepository");

    public String createOne(int firstId, int secondId, int rulesetId) throws Exception {
        Connection conn = null;
        String gameId = null;
        String sql = "insert into games (player1_id, player2_id, ruleset_id, status) VALUES (?, ?, ?, ?)";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, firstId);
            stmt.setInt(2, secondId);
            stmt.setInt(3, rulesetId);
            stmt.setInt(4, GameStatus.WAITING.getValue());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                gameId = rs.getString(1);
                if (logger.isDebugEnabled()) {
                    logger.info("Game created with id " + gameId);
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

    public GameDto getById(String id) {
        Connection conn = null;
        GameDto gameDto = null;
        String sql = "select games.*, rulesets.name, rulesets.detail, rulesets.description, rulesets.published from games"
                + " join rulesets on games.ruleset_id = rulesets.id"
                + " where games.id = ?";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                gameDto = new GameDto(rs);
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return gameDto;
    }

    public boolean insertGameLog(String messageDtos, String gameId, int playerId, String fen,
            Date createdAt) {
        boolean result = false;
        Connection conn = null;
        String sql = "insert into game_logs (fen, message, player_id, game_id, created_at) VALUES (?, to_json(?::json), ?, ?, ?)";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, fen);
            stmt.setString(2, messageDtos);
            stmt.setInt(3, playerId);
            stmt.setString(4, gameId);
            stmt.setTimestamp(5, new Timestamp(createdAt.getTime()));
            int count = stmt.executeUpdate();
            if (count > 0) {
                result = true;
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return result;
    }

    public List<GameLogDto> getGameLogs(String gameId) {
        Connection conn = null;
        List<GameLogDto> gameLogDtos = new ArrayList<>();
        String sql = "select * from game_logs where game_id = ? order by created_at asc";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, gameId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                gameLogDtos.add(new GameLogDto(rs));
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return gameLogDtos;
    }

    public boolean updateElo(int whiteId, int whiteElo, int blackId, int blackElo) {
        boolean result = false;
        Connection conn = null;
        String sql = "UPDATE users " +
                "SET elo = CASE " +
                "    WHEN id = ? THEN ? " +
                "    WHEN id = ? THEN ? " +
                "    ELSE elo " +
                "END " +
                "WHERE id IN (?, ?);";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, whiteId);
            stmt.setInt(2, whiteElo);
            stmt.setInt(3, blackId);
            stmt.setInt(4, blackElo);
            stmt.setInt(5, whiteId);
            stmt.setInt(6, blackId);
            int count = stmt.executeUpdate();
            if (count > 0) {
                result = true;
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return result;
    }

    public boolean updateGameStatus(String gameId, GameStatus gameStatus) {
        boolean result = false;
        Connection conn = null;
        String sql = "UPDATE games " +
                "SET status = ? " +
                "WHERE id = ?";
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameStatus.getValue());
            stmt.setString(2, gameId);
            int count = stmt.executeUpdate();
            if (count > 0) {
                result = true;
            }
        } catch (SQLException e) {
            logger.error("SQLException occurred: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Exception occurred: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return result;
    }
}
