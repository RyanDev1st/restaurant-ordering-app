package model;

// One table in the restaurant.
public class DiningTable {
    private int number;
    private int seats;

    public DiningTable(int number, int seats) {
        this.number = number;
        setSeats(seats);
    }

    public int getNumber() { return number; }
    public int getSeats() { return seats; }

    public void setSeats(int seats) {
        if (seats <= 0) {
            throw new IllegalArgumentException("A table needs at least 1 seat");
        }
        this.seats = seats;
    }
}
