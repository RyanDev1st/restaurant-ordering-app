package model;

// A guest who orders food.
public class Customer extends User {
    private String phone;

    public Customer(int id, String username, String password, String fullName, String phone) {
        super(id, username, password, fullName);
        setPhone(phone);
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone must have 10 digits");
        }
        this.phone = phone;
    }

    @Override
    public Role getRole() { return Role.CUSTOMER; }

    @Override
    public boolean canManageMenu() { return false; }
}
