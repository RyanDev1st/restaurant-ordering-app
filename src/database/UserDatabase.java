package database;

import model.Admin;
import model.Customer;
import model.Role;
import model.Staff;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Reads and writes the "users" table. Customers, staff and admins all live in this one table.
public class UserDatabase implements Database<User> {

    @Override
    public List<User> findAll() {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users")) {
            return readUsers(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read users: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(int id) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            List<User> found = readUsers(ps);
            return found.isEmpty() ? null : found.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read user: " + e.getMessage(), e);
        }
    }

    public User findByUsername(String username) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            List<User> found = readUsers(ps);
            return found.isEmpty() ? null : found.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read user: " + e.getMessage(), e);
        }
    }

    public List<User> findByRole(Role role) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE role = ?")) {
            ps.setString(1, role.name());
            return readUsers(ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read users: " + e.getMessage(), e);
        }
    }

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role, phone, position) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillColumns(ps, user);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Could not save user: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, role = ?, phone = ?, position = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fillColumns(ps, user);
            ps.setInt(7, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update user: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete user: " + e.getMessage(), e);
        }
    }

    // Puts the user's values into the 6 "?" marks used by insert and update.
    private void fillColumns(PreparedStatement ps, User user) throws SQLException {
        String phone = null;
        String position = null;
        if (user instanceof Customer) {
            phone = ((Customer) user).getPhone();
        }
        if (user instanceof Staff) { // true for Admin too, because an Admin is a Staff
            position = ((Staff) user).getPosition();
        }
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        ps.setString(4, user.getRole().name());
        ps.setString(5, phone);
        ps.setString(6, position);
    }

    // Runs the query and turns each row into the right kind of User, using the "role" column.
    private List<User> readUsers(PreparedStatement ps) throws SQLException {
        List<User> users = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String fullName = rs.getString("full_name");
            Role role = Role.valueOf(rs.getString("role"));
            if (role == Role.CUSTOMER) {
                users.add(new Customer(id, username, password, fullName, rs.getString("phone")));
            } else if (role == Role.STAFF) {
                users.add(new Staff(id, username, password, fullName, rs.getString("position")));
            } else {
                users.add(new Admin(id, username, password, fullName, rs.getString("position")));
            }
        }
        return users;
    }
}
