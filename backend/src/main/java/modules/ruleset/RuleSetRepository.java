package modules.ruleset;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import modules.ruleset.dto.RuleSetDto;
import utils.ConnectionPool;

public class RuleSetRepository {
    static Logger logger = Logger.getLogger("RuleSetRepository");

    public List<RuleSetDto> getAllRuleSet() {
        List<RuleSetDto> result = new ArrayList<>();
        String query = "select * from rulesets";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new RuleSetDto(rs));
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

    public List<RuleSetDto> getAllRuleSet(boolean published) {
        List<RuleSetDto> result = new ArrayList<>();
        String query = "select * from rulesets where published = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setBoolean(1, published);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new RuleSetDto(rs));
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

    public RuleSetDto getById(int id, boolean published) {
        RuleSetDto result = null;
        String query = "select * from rulesets where id = ? and published = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setBoolean(2, published);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = new RuleSetDto(rs);
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

    public RuleSetDto getById(int id) {
        RuleSetDto result = null;
        String query = "select * from rulesets where id = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result = new RuleSetDto(rs);
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

    public boolean addOne(String name, String detail, String description) {
        boolean created = false;
        String query = "insert into rulesets (name, detail, description, published)"
                + " values (?, to_json(?::json), to_json(?::json), ?)";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, detail);
            stmt.setString(3, description);
            stmt.setBoolean(4, false);
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

    public boolean udpateRuleSet(int id, String name, String detail, String description, boolean published) {
        boolean updated = false;
        String query = "update rulesets set name = ?, detail = to_json(?::json), description = to_json(?::json), published = ? where id = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, detail);
            stmt.setString(3, description);
            stmt.setBoolean(4, published);
            stmt.setInt(5, id);
            int count = stmt.executeUpdate();
            if (count > 0) {
                updated = true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                ConnectionPool.releaseConnection(conn);
            }
        }
        return updated;
    }

    public boolean deleteOne(int id) {
        boolean deleted = false;
        String query = "delete from rulesets where id = ?";
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
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
}
