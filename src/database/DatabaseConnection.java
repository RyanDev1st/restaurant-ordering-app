package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Opens a link to the database file. The database address is written here and nowhere else.
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:restaurant.db";

    // private: this class only has a static method, so nobody needs an object of it.
    private DatabaseConnection() { }

    public static Connection open() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        // SQLite does not check foreign keys unless we switch it on for each connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }
}
