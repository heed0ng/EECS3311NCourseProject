package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Originally, I had class/static variable that started from 1 and everytime constructor called,
// this.ClassName.id = String.format(%s-%d,getClass().getSimpleName(), id++);
// However, this created too many and a massive errors.

public class IdGenerator {
    private static final Pattern NUMERIC_SUFFIX_PATTERN = Pattern.compile("-(\\d+)$");
    private final DatabaseManager databaseManager;

    public IdGenerator(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public synchronized String nextId(String tableName, String idColumnName, String prefix) {
        String sql = "SELECT " + idColumnName + " FROM " + tableName + " WHERE " + idColumnName + " LIKE ?";
        int max = 0;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, prefix + "-%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int value = this.extractSuffix(resultSet.getString(1));
                    if (value > max) max = value;
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to generate sequential ID for prefix " + prefix + ".", exception);
        }
        return prefix + "-" + (max + 1);
    }

    private int extractSuffix(String identifier) {
        if (identifier == null) return 0;
        Matcher matcher = NUMERIC_SUFFIX_PATTERN.matcher(identifier);
        if (!matcher.find()) return 0;
        return Integer.parseInt(matcher.group(1));
    }
}