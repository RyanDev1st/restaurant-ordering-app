package ui;

import model.Category;
import model.Customer;
import model.MenuItem;
import model.OrderItem;
import service.MenuService;
import service.OrderService;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

// The customer's main window: browse the menu, fill a cart, open the cart or the order list.
public class MenuFrame extends JFrame {
    private final MenuService menuService;
    private final OrderService orderService;
    private final Customer customer;
    private final List<OrderItem> cart = new ArrayList<>();

    private final JComboBox<String> categoryBox = new JComboBox<>();
    private final JTextField searchField = new JTextField(12);
    private final JList<MenuItem> menuList = new JList<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JLabel cartLabel = new JLabel();

    public MenuFrame(MenuService menuService, OrderService orderService, Customer customer) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.customer = customer;

        // Top: filter by category, or search by name.
        categoryBox.addItem("ALL");
        for (Category category : Category.values()) {
            categoryBox.addItem(category.name());
        }
        categoryBox.addActionListener(e -> showCategory());
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> menuList.setListData(menuService.search(searchField.getText()).toArray(new MenuItem[0])));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Type:"));
        top.add(categoryBox);
        top.add(new JLabel("   Name:"));
        top.add(searchField);
        top.add(searchButton);

        // Bottom: quantity, add to cart, open cart, my orders.
        JButton addButton = new JButton("Add to cart");
        JButton cartButton = new JButton("View cart / Order");
        JButton ordersButton = new JButton("My orders");
        addButton.addActionListener(e -> onAddToCart());
        cartButton.addActionListener(e -> onOpenCart());
        ordersButton.addActionListener(e -> new OrderStatusFrame(orderService, customer).setVisible(true));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Qty:"));
        bottom.add(quantitySpinner);
        bottom.add(addButton);
        bottom.add(cartButton);
        bottom.add(ordersButton);
        bottom.add(cartLabel);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(menuList), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        showCategory();
        updateCartLabel();
        setTitle("Menu - " + customer.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
    }

    private void showCategory() {
        String choice = (String) categoryBox.getSelectedItem();
        List<MenuItem> items;
        if (choice.equals("ALL")) {
            items = menuService.getAvailableItems();
        } else {
            items = menuService.getByCategory(Category.valueOf(choice));
        }
        menuList.setListData(items.toArray(new MenuItem[0]));
    }

    private void onAddToCart() {
        MenuItem item = menuList.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pick a dish first.");
            return;
        }
        int quantity = (Integer) quantitySpinner.getValue();
        cart.add(new OrderItem(item, quantity));
        updateCartLabel();
    }

    private void onOpenCart() {
        new CartDialog(this, orderService, customer, cart).setVisible(true); // waits here until it closes
        updateCartLabel();
        showCategory(); // a dish may have sold out in the meantime
    }

    private void updateCartLabel() {
        cartLabel.setText("   Cart: " + cart.size() + " line(s)");
    }
}
