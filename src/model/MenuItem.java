package model;

// One dish or drink on the menu.
public class MenuItem {
    private int id;
    private String name;
    private Category category;
    private int price;          // in VND, which has no decimals
    private boolean available;

    public MenuItem(int id, String name, Category category, int price, boolean available) {
        this.id = id;
        setName(name);
        this.category = category;
        setPrice(price);
        this.available = available;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getPrice() { return price; }
    public boolean isAvailable() { return available; }

    public void setId(int id) { this.id = id; }
    public void setCategory(Category category) { this.category = category; }
    public void setAvailable(boolean available) { this.available = available; }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Dish name cannot be empty");
        }
        this.name = name.trim();
    }

    public void setPrice(int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be more than 0");
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " - " + price + " VND" + (available ? "" : " (sold out)");
    }
}
