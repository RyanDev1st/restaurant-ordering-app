package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// One customer order for one table, with its lines and its status.
public class Order {
    private int id;
    private int customerId;
    private int tableNumber;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private final List<OrderItem> items = new ArrayList<>();

    // New order made by a customer: it starts as PENDING, now.
    public Order(int customerId, int tableNumber) {
        this(0, customerId, tableNumber, OrderStatus.PENDING, LocalDateTime.now());
    }

    // Order loaded from the database.
    public Order(int id, int customerId, int tableNumber, OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.tableNumber = tableNumber;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Overloading: three methods share the name addItem but take different inputs.
    public void addItem(MenuItem item) {
        addItem(item, 1);
    }

    public void addItem(MenuItem item, int qty) {
        // qty is a copy of the caller's number (call by value).
        // item is a copy of the reference, so it points to the same MenuItem object.
        items.add(new OrderItem(item, qty));
    }

    public void addItem(OrderItem line) {
        items.add(line);
    }

    // Give back a copy, so no one can change our list from outside.
    public List<OrderItem> getItems() { return new ArrayList<>(items); }

    public int getTotal() {
        int total = 0;
        for (OrderItem line : items) {
            total += line.getSubtotal();
        }
        return total;
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public int getId() { return id; }
    public int getCustomerId() { return customerId; }
    public int getTableNumber() { return tableNumber; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Order " + id + " - table " + tableNumber + " - " + status + " - " + getTotal() + " VND";
    }
}
