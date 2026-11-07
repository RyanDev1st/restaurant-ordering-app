package service;

import model.Order;
import model.OrderItem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

// Writes an order's receipt to a text file.
public class ReceiptWriter {

    public void save(Order order, File file) throws IOException {
        // try-with-resources: the file is closed for us, even if writing fails.
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("RESTAURANT RECEIPT");
            out.println("Order " + order.getId() + ", table " + order.getTableNumber());
            out.println("Time: " + order.getCreatedAt().withNano(0));
            out.println("Status: " + order.getStatus());
            out.println("------------------------------");
            for (OrderItem line : order.getItems()) {
                out.println(line); // uses OrderItem.toString()
            }
            out.println("------------------------------");
            out.println("TOTAL: " + order.getTotal() + " VND");
        }
    }
}
