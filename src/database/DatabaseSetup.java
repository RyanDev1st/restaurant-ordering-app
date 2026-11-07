package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// Creates the tables and the sample data the first time the app runs.
public class DatabaseSetup {

    private DatabaseSetup() { }

    public static void createIfMissing() {
        if (Files.exists(Path.of("restaurant.db"))) {
            return; // already made on an earlier run
        }
        runScript("resources/schema.sql");
        runScript("resources/seed.sql");
    }

    // Reads a .sql file and runs each statement in it. Statements end with ";".
    private static void runScript(String path) {
        try (Connection connection = DatabaseConnection.open();
             Statement statement = connection.createStatement()) {
            String script = Files.readString(Path.of(path));
            for (String sql : script.split(";")) {
                if (!sql.isBlank()) {
                    statement.execute(sql);
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Could not run " + path + ": " + e.getMessage(), e);
        }
    }
}
