package ui;

import model.MenuItem;
import model.Order;
import model.OrderItem;
import service.MenuService;
import service.OrderService;
import service.ValidationException;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

// The staff window: the queue of open orders, move them on or cancel them, mark dishes sold out.
public class StaffOrdersFrame extends JFrame {
    private final OrderService orderService;
    private final MenuService menuService;
    private final JList<Order> queueList = new JList<>();
    private final JTextArea detailText = new JTextArea(8, 25);
    private final JComboBox<MenuItem> dishBox = new JComboBox<>();

    public StaffOrdersFrame(OrderService orderService, MenuService menuService) {
        this.orderService = orderService;
        this.menuService = menuService;

        detailText.setEditable(false);
        queueList.addListSelectionListener(e -> showDetails());

        JButton nextButton = new JButton("Next status");
        JButton cancelButton = new JButton("Cancel order");
        JButton refreshButton = new JButton("Refresh");
        nextButton.addActionListener(e -> onNextStatus());
        cancelButton.addActionListener(e -> onCancel());
        refreshButton.addActionListener(e -> loadData());
        JPanel orderButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderButtons.add(nextButton);
        orderButtons.add(cancelButton);
        orderButtons.add(refreshButton);

        JButton soldOutButton = new JButton("Sold out");
        JButton backButton = new JButton("Available again");
        soldOutButton.addActionListener(e -> onSetAvailable(false));
        backButton.addActionListener(e -> onSetAvailable(true));
        JPanel dishButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dishButtons.add(new JLabel("Dish:"));
        dishButtons.add(dishBox);
        dishButtons.add(soldOutButton);
        dishButtons.add(backButton);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(orderButtons);
        bottom.add(dishButtons);

        add(new JScrollPane(queueList), BorderLayout.CENTER);
        add(new JScrollPane(detailText), BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
        loadData();
        setTitle("Order queue");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 450);
        setLocationRelativeTo(null);
    }

    private void loadData() {
        queueList.setListData(orderService.getQueue().toArray(new Order[0]));
        dishBox.removeAllItems();
        for (MenuItem item : menuService.getAllItems()) {
            dishBox.addItem(item);
        }
        detailText.setText("");
    }

    // Shows the lines of the order that is selected in the list.
    private void showDetails() {
        Order order = queueList.getSelectedValue();
        if (order == null) {
            return;
        }
        StringBuilder text = new StringBuilder("Order " + order.getId() + ", table " + order.getTableNumber() + "\n\n");
        for (OrderItem line : order.getItems()) {
            text.append(line).append("\n");
        }
        detailText.setText(text.toString());
    }

    private void onNextStatus() {
        Order order = queueList.getSelectedValue();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Pick an order first.");
            return;
        }
        try {
            orderService.advanceStatus(order.getId());
            loadData();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not allowed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        Order order = queueList.getSelectedValue();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Pick an order first.");
            return;
        }
        try {
            orderService.cancel(order.getId());
            loadData();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not allowed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSetAvailable(boolean available) {
        MenuItem item = (MenuItem) dishBox.getSelectedItem();
        if (item == null) {
            return;
        }
        try {
            menuService.setAvailable(item.getId(), available);
            JOptionPane.showMessageDialog(this, item.getName() + (available ? " is available again." : " is sold out."));
            loadData();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not allowed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
