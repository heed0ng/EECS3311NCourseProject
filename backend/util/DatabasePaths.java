package backend.util;

public final class DatabasePaths {

    public static final String DATABASE_FILE_NAME = "booking_platform.db";

    private DatabasePaths() {
    }

    public static String databaseFilePath() {
        return System.getProperty("user.dir") + "/" + DATABASE_FILE_NAME;
    }
}