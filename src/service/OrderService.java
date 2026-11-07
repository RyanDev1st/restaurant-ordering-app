package service;

import database.Database;
import database.OrderDatabase;
import model.Customer;
import model.DiningTable;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import model.OrderStatus;

import java.util.List;

// The order rules: what makes a valid order, and how an order moves from step to step.
public class OrderService {
    private final OrderDatabase orders;
    private final Database<MenuItem> menu;
    private final Database<DiningTable> tables;

    public OrderService(OrderDatabase orders, Database<MenuItem> menu, Database<DiningTable> tables) {
        this.orders = orders;
        this.menu = menu;
        this.tables = tables;
    }

    public Order placeOrder(Customer customer, int tableNumber, List<OrderItem> cart) throws ValidationException {
        checkCart(cart);
        if (tables.findById(tableNumber) == null) {
            throw new ValidationException("Table " + tableNumber + " does not exist");
        }
        Order order = new Order(customer.getId(), tableNumber);
        for (OrderItem line : cart) {
            order.addItem(line);
        }
        order.setId(orders.insert(order));
        return order;
    }

    // Orders that staff still have to work on: not served and not cancelled.
    public List<Order> getQueue() {
        List<Order> queue = orders.findAll();
        queue.removeIf(o -> o.getStatus() == OrderStatus.SERVED || o.getStatus() == OrderStatus.CANCELLED);
        return queue;
    }

    public List<Order> getOrdersOf(Customer customer) {
        return orders.findByCustomer(customer.getId());
    }

    public void advanceStatus(int orderId) throws ValidationException {
        Order order = findOrFail(orderId);
        try {
            order.setStatus(order.getStatus().next());
        } catch (IllegalStateException e) {
            throw new ValidationException(e.getMessage());
        }
        orders.update(order);
    }

    public void cancel(int orderId) throws ValidationException {
        Order order = findOrFail(orderId);
        if (order.getStatus() == OrderStatus.SERVED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("A " + order.getStatus() + " order cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orders.update(order);
    }

    // The cart must not be empty, and every dish must still be on sale right now.
    private void checkCart(List<OrderItem> cart) throws ValidationException {
        if (cart == null || cart.isEmpty()) {
            throw new ValidationException("Your cart is empty");
        }
        for (OrderItem line : cart) {
            MenuItem now = menu.findById(line.getMenuItem().getId()); // read again: it may have changed
            if (now == null || !now.isAvailable()) {
                throw new ValidationException(line.getMenuItem().getName() + " is sold out");
            }
        }
    }

    private Order findOrFail(int orderId) throws ValidationException {
        Order order = orders.findById(orderId);
        if (order == null) {
            throw new ValidationException("Order " + orderId + " does not exist");
        }
        return order;
    }
}
