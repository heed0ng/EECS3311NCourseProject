package repository.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private final String databaseUrl;

    public DatabaseManager(String databaseFilePath) {
        this.databaseUrl = "jdbc:sqlite:" + databaseFilePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.databaseUrl);
    }
}
