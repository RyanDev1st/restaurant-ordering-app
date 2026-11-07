package ui;

import model.Customer;
import model.Order;
import service.OrderService;
import service.ReceiptWriter;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

// Shows a customer's orders and their status, and saves a receipt.
public class OrderStatusFrame extends JFrame {
    private final OrderService orderService;
    private final Customer customer;
    private final ReceiptWriter receiptWriter = new ReceiptWriter();
    private final JList<Order> orderList = new JList<>();

    public OrderStatusFrame(OrderService orderService, Customer customer) {
        this.orderService = orderService;
        this.customer = customer;

        JButton refreshButton = new JButton("Refresh");
        JButton receiptButton = new JButton("Save receipt");
        refreshButton.addActionListener(e -> loadOrders());
        receiptButton.addActionListener(e -> onSaveReceipt());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(refreshButton);
        bottom.add(receiptButton);

        add(new JScrollPane(orderList), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadOrders();
        setTitle("My orders - " + customer.getFullName());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // close only this window, not the app
        setSize(500, 350);
        setLocationRelativeTo(null);
    }

    private void loadOrders() {
        orderList.setListData(orderService.getOrdersOf(customer).toArray(new Order[0]));
    }

    private void onSaveReceipt() {
        Order order = orderList.getSelectedValue();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Pick an order first.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("receipt-" + order.getId() + ".txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return; // the user pressed Cancel
        }
        try {
            receiptWriter.save(order, chooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "Receipt saved.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save: " + ex.getMessage());
        }
    }
}
