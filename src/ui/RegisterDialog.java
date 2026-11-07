package ui;

import service.AuthService;
import service.ValidationException;

import javax.swing.*;
import java.awt.GridLayout;

// A small form where a new customer makes an account.
public class RegisterDialog extends JDialog {
    private final AuthService authService;
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JTextField fullNameField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);

    public RegisterDialog(JFrame owner, AuthService authService) {
        super(owner, "New customer account", true); // true = the window behind waits until this one closes
        this.authService = authService;

        JButton registerButton = new JButton("Create account");
        registerButton.addActionListener(e -> onRegister());

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Username"));
        panel.add(usernameField);
        panel.add(new JLabel("Password (4+ characters)"));
        panel.add(passwordField);
        panel.add(new JLabel("Full name"));
        panel.add(fullNameField);
        panel.add(new JLabel("Phone (10 digits)"));
        panel.add(phoneField);
        panel.add(new JLabel());
        panel.add(registerButton);
        add(panel);

        pack();
        setLocationRelativeTo(owner);
    }

    private void onRegister() {
        try {
            authService.register(usernameField.getText(), new String(passwordField.getPassword()),
                    fullNameField.getText(), phoneField.getText());
            JOptionPane.showMessageDialog(this, "Account created. You can log in now.");
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot register", JOptionPane.ERROR_MESSAGE);
        }
    }
}
