package database;

import model.Category;
import model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Reads and writes the "menu_items" table.
public class MenuItemDatabase implements Database<MenuItem> {

    @Override
    public List<MenuItem> findAll() {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM menu_items ORDER BY category, name")) {
            return readItems(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read the menu: " + e.getMessage(), e);
        }
    }

    @Override
    public MenuItem findById(int id) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM menu_items WHERE id = ?")) {
            ps.setInt(1, id);
            List<MenuItem> found = readItems(ps);
            return found.isEmpty() ? null : found.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read the dish: " + e.getMessage(), e);
        }
    }

    @Override
    public int insert(MenuItem item) {
        String sql = "INSERT INTO menu_items (name, category, price, available) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillColumns(ps, item);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save the dish: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(MenuItem item) {
        String sql = "UPDATE menu_items SET name = ?, category = ?, price = ?, available = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fillColumns(ps, item);
            ps.setInt(5, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update the dish: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM menu_items WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete the dish: " + e.getMessage(), e);
        }
    }

    // Puts the dish's values into the first 4 "?" marks.
    private void fillColumns(PreparedStatement ps, MenuItem item) throws SQLException {
        ps.setString(1, item.getName());
        ps.setString(2, item.getCategory().name());
        ps.setInt(3, item.getPrice());
        ps.setBoolean(4, item.isAvailable());
    }

    // Runs the query and turns each row into a MenuItem object.
    private List<MenuItem> readItems(PreparedStatement ps) throws SQLException {
        List<MenuItem> items = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            items.add(new MenuItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    Category.valueOf(rs.getString("category")),
                    rs.getInt("price"),
                    rs.getBoolean("available")));
        }
        return items;
    }
}
