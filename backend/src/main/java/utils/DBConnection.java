package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnecttion() {
        Connection cons = null;
        System.out.println(Env.DB_HOST);
        try {
            Class.forName("org.postgresql.Driver");
            cons = DriverManager.getConnection("jdbc:postgresql://" + Env.DB_HOST + "/" + Env.POSTGRES_DB,
                    Env.POSTGRES_USER, Env.POSTGRES_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cons;
    }

}