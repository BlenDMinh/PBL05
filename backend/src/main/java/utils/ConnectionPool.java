package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import common.Env;

public class ConnectionPool {
    private static final int POOL_SIZE = 10;
    private static final BlockingQueue<Connection> CONNECTION_POOL = new ArrayBlockingQueue<>(POOL_SIZE);

    static {
        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                Connection conn = createConnection();
                if (conn == null)
                    throw new SQLException();
                CONNECTION_POOL.add(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                --i;
            }
        }
    }

    private ConnectionPool() {
    }

    public static Connection getConnection() throws InterruptedException, SQLException {
        return CONNECTION_POOL.take();
    }

    public static void releaseConnection(Connection connection) {
        CONNECTION_POOL.add(connection);
    }

    private static Connection createConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://" + Env.DB_HOST + "/" + Env.POSTGRES_DB,
                    Env.POSTGRES_USER, Env.POSTGRES_PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
