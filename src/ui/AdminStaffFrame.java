package ui;

import model.Staff;
import service.AuthService;
import service.ValidationException;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

// Lets the admin add and remove staff accounts.
public class AdminStaffFrame extends JFrame {
    private final AuthService authService;
    private final JList<Staff> staffList = new JList<>();
    private final JTextField usernameField = new JTextField(8);
    private final JTextField passwordField = new JTextField(8);
    private final JTextField fullNameField = new JTextField(12);
    private final JTextField positionField = new JTextField(8);

    public AdminStaffFrame(AuthService authService) {
        this.authService = authService;

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Full name:"));
        form.add(fullNameField);
        form.add(new JLabel("Job:"));
        form.add(positionField);

        JButton addButton = new JButton("Add staff");
        JButton removeButton = new JButton("Remove selected");
        addButton.addActionListener(e -> onAdd());
        removeButton.addActionListener(e -> onRemove());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(removeButton);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.add(form);
        bottom.add(buttons);

        add(new JScrollPane(staffList), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadStaff();
        setTitle("Admin - Staff accounts");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
    }

    private void loadStaff() {
        staffList.setListData(authService.getStaff().toArray(new Staff[0]));
    }

    private void onAdd() {
        try {
            authService.addStaff(usernameField.getText(), passwordField.getText(),
                    fullNameField.getText(), positionField.getText());
            loadStaff();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not allowed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemove() {
        Staff staff = staffList.getSelectedValue();
        if (staff == null) {
            JOptionPane.showMessageDialog(this, "Pick a staff member first.");
            return;
        }
        try {
            authService.removeStaff(staff.getId());
            loadStaff();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not allowed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
