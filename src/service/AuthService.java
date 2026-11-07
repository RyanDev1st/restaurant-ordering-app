package service;

import database.UserDatabase;
import model.Customer;
import model.Role;
import model.Staff;
import model.User;

import java.util.ArrayList;
import java.util.List;

// Logging in, and making or removing accounts.
public class AuthService {
    private final UserDatabase users;

    public AuthService(UserDatabase users) {
        this.users = users;
    }

    public User login(String username, String password) throws AuthException {
        User user = users.findByUsername(username.trim());
        // Same message for both cases, so nobody can guess which usernames exist.
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthException("Wrong username or password");
        }
        return user;
    }

    public Customer register(String username, String password, String fullName, String phone)
            throws ValidationException {
        checkUsernameFree(username);
        try {
            Customer customer = new Customer(0, username, password, fullName, phone);
            customer.setId(users.insert(customer));
            return customer;
        } catch (IllegalArgumentException e) {
            // A setter in the model said a value is wrong. Pass its message on as a rule error.
            throw new ValidationException(e.getMessage());
        }
    }

    public List<Staff> getStaff() {
        List<Staff> staff = new ArrayList<>();
        for (User user : users.findByRole(Role.STAFF)) {
            staff.add((Staff) user); // safe: every user with role STAFF was made as a Staff
        }
        return staff;
    }

    public Staff addStaff(String username, String password, String fullName, String position)
            throws ValidationException {
        checkUsernameFree(username);
        try {
            Staff staff = new Staff(0, username, password, fullName, position);
            staff.setId(users.insert(staff));
            return staff;
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    // Only staff accounts can be removed here, never a customer or the admin.
    public void removeStaff(int id) throws ValidationException {
        User user = users.findById(id);
        if (user == null || user.getRole() != Role.STAFF) {
            throw new ValidationException("Only staff accounts can be removed");
        }
        users.delete(id);
    }

    private void checkUsernameFree(String username) throws ValidationException {
        if (username != null && users.findByUsername(username.trim()) != null) {
            throw new ValidationException("The username \"" + username + "\" is already taken");
        }
    }
}
