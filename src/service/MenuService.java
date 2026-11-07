package service;

import database.Database;
import model.Category;
import model.MenuItem;

import java.util.List;

// The menu rules: what customers can see, and how the admin changes dishes.
public class MenuService {
    // The type is the interface, so any class that implements Database<MenuItem> works here.
    private final Database<MenuItem> menu;

    public MenuService(Database<MenuItem> menu) {
        this.menu = menu;
    }

    // Every dish, sold out or not. For staff and admin.
    public List<MenuItem> getAllItems() {
        return menu.findAll();
    }

    // Only the dishes customers can order.
    public List<MenuItem> getAvailableItems() {
        List<MenuItem> items = menu.findAll();
        items.removeIf(item -> !item.isAvailable()); // lambda: "remove it if it is not available"
        return items;
    }

    public List<MenuItem> getByCategory(Category category) {
        List<MenuItem> items = getAvailableItems();
        items.removeIf(item -> item.getCategory() != category);
        return items;
    }

    public List<MenuItem> search(String text) {
        String wanted = text.trim().toLowerCase();
        List<MenuItem> items = getAvailableItems();
        items.removeIf(item -> !item.getName().toLowerCase().contains(wanted));
        return items;
    }

    public MenuItem addItem(String name, Category category, int price) throws ValidationException {
        try {
            MenuItem item = new MenuItem(0, name, category, price, true);
            item.setId(menu.insert(item));
            return item;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public void updateItem(int id, String name, Category category, int price) throws ValidationException {
        MenuItem item = findOrFail(id);
        try {
            item.setName(name);
            item.setCategory(category);
            item.setPrice(price);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
        menu.update(item);
    }

    public void deleteItem(int id) throws ValidationException {
        try {
            menu.delete(id);
        } catch (RuntimeException e) {
            // The database refuses because old orders still point to this dish.
            throw new ValidationException("This dish is in old orders. Mark it as sold out instead.");
        }
    }

    public void setAvailable(int id, boolean available) throws ValidationException {
        MenuItem item = findOrFail(id);
        item.setAvailable(available);
        menu.update(item);
    }

    private MenuItem findOrFail(int id) throws ValidationException {
        MenuItem item = menu.findById(id);
        if (item == null) {
            throw new ValidationException("Dish " + id + " does not exist");
        }
        return item;
    }
}
