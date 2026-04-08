package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private final String databaseUrl;
    private final String username;
    private final String password;

    public DatabaseManager(String host, int port, String databaseName, String username, String password) {
        this.databaseUrl = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
        this.username = username;
        this.password = password;
        this.loadPostgresDriver();
    }

    private void loadPostgresDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("PostgreSQL JDBC driver not found. Add the postgresql dependency to pom.xml.", exception);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.databaseUrl, this.username, this.password);
    }
}