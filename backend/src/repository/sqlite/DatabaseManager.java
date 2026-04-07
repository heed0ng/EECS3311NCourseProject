package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final String databaseUrl;

    public DatabaseManager(String databaseFilePath) {
        this.databaseUrl = "jdbc:sqlite:" + databaseFilePath;
        loadSqliteDriver();
    }

    private void loadSqliteDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("SQLite JDBC driver not found. Add sqlite-jdbc JAR to the project build path.", exception);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.databaseUrl);
    }
}