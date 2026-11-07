package model;

// One line of an order: a dish, how many, and the price when it was ordered.
public class OrderItem {
    private MenuItem menuItem;
    private int quantity;
    private int unitPrice;

    // New line: copy today's price, so a later price change does not touch this order.
    public OrderItem(MenuItem menuItem, int quantity) {
        this(menuItem, quantity, menuItem.getPrice());
    }

    // Line loaded from the database: keep the price that was saved.
    public OrderItem(MenuItem menuItem, int quantity, int unitPrice) {
        this.menuItem = menuItem;
        setQuantity(quantity);
        this.unitPrice = unitPrice;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public int getUnitPrice() { return unitPrice; }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = quantity;
    }

    public int getSubtotal() { return unitPrice * quantity; }

    @Override
    public String toString() {
        return quantity + " x " + menuItem.getName() + " = " + getSubtotal() + " VND";
    }
}
