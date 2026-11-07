package model;

// The steps an order goes through.
public enum OrderStatus {
    PENDING, PREPARING, READY, SERVED, CANCELLED;

    // The step after this one. A served or cancelled order has no next step.
    public OrderStatus next() {
        switch (this) {
            case PENDING:   return PREPARING;
            case PREPARING: return READY;
            case READY:     return SERVED;
            default: throw new IllegalStateException("A " + this + " order has no next step");
        }
    }
}
