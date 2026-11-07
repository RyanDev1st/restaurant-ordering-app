package app;

import database.DatabaseSetup;
import database.DiningTableDatabase;
import database.MenuItemDatabase;
import database.OrderDatabase;
import database.UserDatabase;
import service.AuthService;
import service.MenuService;
import service.OrderService;
import ui.LoginFrame;

import javax.swing.SwingUtilities;

// Starts the app: makes the database if needed, connects the layers, opens the login window.
public class Main {
    public static void main(String[] args) {
        DatabaseSetup.createIfMissing();

        // Bottom layer: one database class per table.
        UserDatabase users = new UserDatabase();
        MenuItemDatabase menuItems = new MenuItemDatabase();
        OrderDatabase orders = new OrderDatabase();
        DiningTableDatabase tables = new DiningTableDatabase();

        // Middle layer: the services get the database classes they need.
        AuthService authService = new AuthService(users);
        MenuService menuService = new MenuService(menuItems);
        OrderService orderService = new OrderService(orders, menuItems, tables);

        // Top layer: Swing windows must be created on Swing's own thread.
        SwingUtilities.invokeLater(() -> new LoginFrame(authService, menuService, orderService).setVisible(true));
    }
}
