package ui;

import model.Customer;
import model.User;
import service.AuthException;
import service.AuthService;
import service.MenuService;
import service.OrderService;

import javax.swing.*;
import java.awt.GridLayout;

// The first window: log in, or open the register form. Then opens the right window for the user.
public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final MenuService menuService;
    private final OrderService orderService;
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginFrame(AuthService authService, MenuService menuService, OrderService orderService) {
        this.authService = authService;
        this.menuService = menuService;
        this.orderService = orderService;

        JButton loginButton = new JButton("Log in");
        JButton registerButton = new JButton("Register");
        loginButton.addActionListener(e -> onLogin());
        registerButton.addActionListener(e -> new RegisterDialog(this, authService).setVisible(true));

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(registerButton);
        panel.add(loginButton);
        add(panel);

        getRootPane().setDefaultButton(loginButton); // Enter key = Log in
        setTitle("Restaurant - Log in");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // centre of the screen
    }

    private void onLogin() {
        try {
            User user = authService.login(usernameField.getText(), new String(passwordField.getPassword()));
            openHomeWindow(user);
            dispose(); // close the login window
        } catch (AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Polymorphism: we do not know which kind of User we got. Each kind answers canManageMenu() its own way.
    private void openHomeWindow(User user) {
        if (user.canManageMenu()) {
            new AdminMenuFrame(authService, menuService, orderService).setVisible(true);
        } else if (user instanceof Customer) {
            new MenuFrame(menuService, orderService, (Customer) user).setVisible(true);
        } else {
            new StaffOrdersFrame(orderService, menuService).setVisible(true);
        }
    }
}
