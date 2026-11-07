package ui;

import model.Customer;
import model.Order;
import model.OrderItem;
import service.OrderService;
import service.ValidationException;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

// Shows the cart and its total, and places the order for a table.
public class CartDialog extends JDialog {
    private final OrderService orderService;
    private final Customer customer;
    private final List<OrderItem> cart; // the same list object as in MenuFrame, so clearing it here clears it there
    private final JTextArea cartText = new JTextArea(10, 30);
    private final JTextField tableField = new JTextField(4);

    public CartDialog(JFrame owner, OrderService orderService, Customer customer, List<OrderItem> cart) {
        super(owner, "Your cart", true);
        this.orderService = orderService;
        this.customer = customer;
        this.cart = cart;

        cartText.setEditable(false);
        JButton orderButton = new JButton("Place order");
        JButton clearButton = new JButton("Empty cart");
        orderButton.addActionListener(e -> onPlaceOrder());
        clearButton.addActionListener(e -> {
            cart.clear();
            showCart();
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Table number:"));
        bottom.add(tableField);
        bottom.add(orderButton);
        bottom.add(clearButton);

        add(new JScrollPane(cartText), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        showCart();
        pack();
        setLocationRelativeTo(owner);
    }

    private void showCart() {
        StringBuilder text = new StringBuilder();
        int total = 0;
        for (OrderItem line : cart) {
            text.append(line).append("\n"); // uses OrderItem.toString()
            total += line.getSubtotal();
        }
        text.append("\nTOTAL: ").append(total).append(" VND");
        cartText.setText(text.toString());
    }

    private void onPlaceOrder() {
        try {
            int table = Integer.parseInt(tableField.getText().trim());
            Order order = orderService.placeOrder(customer, table, cart);
            JOptionPane.showMessageDialog(this, "Order " + order.getId() + " is " + order.getStatus());
            cart.clear();
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Type a table number, for example 4.");
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot order", JOptionPane.ERROR_MESSAGE);
        }
    }
}
