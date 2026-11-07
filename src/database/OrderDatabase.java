package database;

import model.Category;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Reads and writes the "orders" table and its lines in the "order_items" table.
public class OrderDatabase implements Database<Order> {

    @Override
    public List<Order> findAll() {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM orders ORDER BY id")) {
            return readOrders(c, ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read orders: " + e.getMessage(), e);
        }
    }

    @Override
    public Order findById(int id) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM orders WHERE id = ?")) {
            ps.setInt(1, id);
            List<Order> found = readOrders(c, ps);
            return found.isEmpty() ? null : found.get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read the order: " + e.getMessage(), e);
        }
    }

    public List<Order> findByStatus(OrderStatus status) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM orders WHERE status = ? ORDER BY id")) {
            ps.setString(1, status.name());
            return readOrders(c, ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read orders: " + e.getMessage(), e);
        }
    }

    public List<Order> findByCustomer(int customerId) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM orders WHERE customer_id = ? ORDER BY id DESC")) {
            ps.setInt(1, customerId);
            return readOrders(c, ps);
        } catch (SQLException e) {
            throw new RuntimeException("Could not read orders: " + e.getMessage(), e);
        }
    }

    // Saves the order and all its lines together (a transaction): either everything is saved, or nothing.
    @Override
    public int insert(Order order) {
        String orderSql = "INSERT INTO orders (customer_id, table_number, status, created_at) VALUES (?, ?, ?, ?)";
        String lineSql = "INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.open()) {
            c.setAutoCommit(false); // start the transaction
            try (PreparedStatement ps = c.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement linePs = c.prepareStatement(lineSql)) {
                ps.setInt(1, order.getCustomerId());
                ps.setInt(2, order.getTableNumber());
                ps.setString(3, order.getStatus().name());
                ps.setString(4, order.getCreatedAt().toString());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                int orderId = keys.getInt(1);

                for (OrderItem line : order.getItems()) {
                    linePs.setInt(1, orderId);
                    linePs.setInt(2, line.getMenuItem().getId());
                    linePs.setInt(3, line.getQuantity());
                    linePs.setInt(4, line.getUnitPrice());
                    linePs.executeUpdate();
                }
                c.commit(); // every row worked: save them all
                return orderId;
            } catch (SQLException e) {
                c.rollback(); // something failed: undo every row of this order
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not save the order: " + e.getMessage(), e);
        }
    }

    // Only the status and the table can change after an order is placed.
    @Override
    public void update(Order order) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement ps = c.prepareStatement("UPDATE orders SET status = ?, table_number = ? WHERE id = ?")) {
            ps.setString(1, order.getStatus().name());
            ps.setInt(2, order.getTableNumber());
            ps.setInt(3, order.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update the order: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = DatabaseConnection.open();
             PreparedStatement lines = c.prepareStatement("DELETE FROM order_items WHERE order_id = ?");
             PreparedStatement ps = c.prepareStatement("DELETE FROM orders WHERE id = ?")) {
            lines.setInt(1, id);
            lines.executeUpdate(); // the lines first, because they point to the order
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete the order: " + e.getMessage(), e);
        }
    }

    // Runs the query, makes the Order objects, then loads the lines of each order.
    private List<Order> readOrders(Connection c, PreparedStatement ps) throws SQLException {
        List<Order> orders = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            orders.add(new Order(
                    rs.getInt("id"),
                    rs.getInt("customer_id"),
                    rs.getInt("table_number"),
                    OrderStatus.valueOf(rs.getString("status")),
                    LocalDateTime.parse(rs.getString("created_at"))));
        }
        for (Order order : orders) {
            loadLines(c, order);
        }
        return orders;
    }

    // Reads the lines of one order, together with their dish (JOIN), and adds them to the order.
    private void loadLines(Connection c, Order order) throws SQLException {
        String sql = "SELECT oi.quantity, oi.unit_price, m.* FROM order_items oi JOIN menu_items m ON m.id = oi.menu_item_id WHERE oi.order_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, order.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MenuItem dish = new MenuItem(rs.getInt("id"), rs.getString("name"),
                        Category.valueOf(rs.getString("category")), rs.getInt("price"), rs.getBoolean("available"));
                order.addItem(new OrderItem(dish, rs.getInt("quantity"), rs.getInt("unit_price")));
            }
        }
    }
}
