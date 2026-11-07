package database;

import model.DiningTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Reads and writes the "dining_tables" table. The table number is chosen by us, not by the database.
public class DiningTableDatabase implements Database<DiningTable> {

    @Override
    public List<DiningTable> findAll() {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM dining_tables ORDER BY number")) {
            return readTables(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read tables: " + e.getMessage(), e);
        }
    }

    @Override
    public DiningTable findById(int number) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM dining_tables WHERE number = ?")) {
            ps.setInt(1, number);
            List<DiningTable> found = readTables(ps);
            return found.isEmpty() ? null : found.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read the table: " + e.getMessage(), e);
        }
    }

    @Override
    public int insert(DiningTable table) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("INSERT INTO dining_tables (number, seats) VALUES (?, ?)")) {
            ps.setInt(1, table.getNumber());
            ps.setInt(2, table.getSeats());
            ps.executeUpdate();
            return table.getNumber();
        } catch (SQLException e) {
            throw new RuntimeException("Could not save the table: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(DiningTable table) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("UPDATE dining_tables SET seats = ? WHERE number = ?")) {
            ps.setInt(1, table.getSeats());
            ps.setInt(2, table.getNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update the table: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int number) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM dining_tables WHERE number = ?")) {
            ps.setInt(1, number);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete the table: " + e.getMessage(), e);
        }
    }

    private List<DiningTable> readTables(PreparedStatement ps) throws SQLException {
        List<DiningTable> tables = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            tables.add(new DiningTable(rs.getInt("number"), rs.getInt("seats")));
        }
        return tables;
    }
}
