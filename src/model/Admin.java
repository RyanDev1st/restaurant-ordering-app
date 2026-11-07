package model;

// A staff member who can also manage the menu and the staff accounts.
public class Admin extends Staff {

    public Admin(int id, String username, String password, String fullName, String position) {
        super(id, username, password, fullName, position);
    }

    @Override
    public Role getRole() { return Role.ADMIN; }

    @Override
    public boolean canManageMenu() { return true; } // replaces the "false" from Staff
}
