package model;

// A restaurant worker who runs the order queue.
public class Staff extends User {
    private String position;

    public Staff(int id, String username, String password, String fullName, String position) {
        super(id, username, password, fullName);
        setPosition(position);
    }

    public String getPosition() { return position; }

    public void setPosition(String position) {
        if (position == null || position.isBlank()) {
            throw new IllegalArgumentException("Job title cannot be empty");
        }
        this.position = position.trim();
    }

    @Override
    public Role getRole() { return Role.STAFF; }

    @Override
    public boolean canManageMenu() { return false; }
}
