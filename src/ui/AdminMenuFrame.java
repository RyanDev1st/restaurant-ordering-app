package ui;

import model.Category;
import model.MenuItem;
import service.AuthService;
import service.MenuService;
import service.OrderService;
import service.ValidationException;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

// The admin's main window: add, change and remove dishes. Also opens the order queue and the staff list.
public class AdminMenuFrame extends JFrame {
    private final MenuService menuService;
    private final JList<MenuItem> menuList = new JList<>();
    private final JTextField nameField = new JTextField(15);
    private final JComboBox<Category> categoryBox = new JComboBox<>(Category.values());
    private final JTextField priceField = new JTextField(8);

    public AdminMenuFrame(AuthService authService, MenuService menuService, OrderService orderService) {
        this.menuService = menuService;

        // When a dish is clicked, copy its values into the form so the admin can change them.
        menuList.addListSelectionListener(e -> fillForm(menuList.getSelectedValue()));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Type:"));
        form.add(categoryBox);
        form.add(new JLabel("Price (VND):"));
        form.add(priceField);

        JButton addButton = new JButton("Add new");
        JButton updateButton = new JButton("Save changes");
        JButton deleteButton = new JButton("Delete");
        JButton queueButton = new JButton("Order queue");
        JButton staffButton = new JButton("Staff accounts");
        addButton.addActionListener(e -> onAdd());
        updateButton.addActionListener(e -> onUpdate());
        deleteButton.addActionListener(e -> onDelete());
        queueButton.addActionListener(e -> new StaffOrdersFrame(orderService, menuService).setVisible(true));
        staffButton.addActionListener(e -> new AdminStaffFrame(authService).setVisible(true));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(queueButton);
        buttons.add(staffButton);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(form);
        bottom.add(buttons);

        add(new JScrollPane(menuList), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadMenu();
        setTitle("Admin - Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 450);
        setLocationRelativeTo(null);
    }

    private void loadMenu() {
        menuList.setListData(menuService.getAllItems().toArray(new MenuItem[0]));
    }

    private void fillForm(MenuItem item) {
        if (item == null) {
            return;
        }
        nameField.setText(item.getName());
        categoryBox.setSelectedItem(item.getCategory());
        priceField.setText(String.valueOf(item.getPrice()));
    }

    private void onAdd() {
        try {
            menuService.addItem(nameField.getText(), (Category) categoryBox.getSelectedItem(), readPrice());
            loadMenu();
        } catch (ValidationException ex) {
            showError(ex);
        }
    }

    private void onUpdate() {
        MenuItem item = menuList.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pick a dish first.");
            return;
        }
        try {
            menuService.updateItem(item.getId(), nameField.getText(), (Category) categoryBox.getSelectedItem(), readPrice());
            loadMenu();
        } catch (ValidationException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        MenuItem item = menuList.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pick a dish first.");
            return;
        }
        try {
            menuService.deleteItem(item.getId());
            loadMenu();
        } catch (ValidationException ex) {
            showError(ex);
        }
    }

    // Turns the price text into a number. Text that is not a number becomes a rule error.
    private int readPrice() throws ValidationException {
        try {
            return Integer.parseInt(priceField.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("The price must be a whole number, for example 45000");
        }
    }

    private void showError(ValidationException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Not allowed", JOptionPane.ERROR_MESSAGE);
    }
}
