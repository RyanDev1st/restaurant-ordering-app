package model;

// Holds what every person in the app has: an id, a login and a name.
public abstract class User {
    private int id;
    private String username;
    private String password;
    private String fullName;

    // protected: only the child classes (Customer, Staff, Admin) call this.
    protected User(int id, String username, String password, String fullName) {
        this.id = id;
        setUsername(username);
        setPassword(password);
        setFullName(fullName);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }

    public void setId(int id) { this.id = id; }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Password needs at least 4 characters");
        }
        this.password = password;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName.trim();
    }

    // Each kind of user must answer these in its own way.
    public abstract Role getRole();
    public abstract boolean canManageMenu();

    @Override
    public String toString() {
        return fullName + " (" + getRole() + ")";
    }
}
