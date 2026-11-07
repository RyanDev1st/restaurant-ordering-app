package service;

// Thrown when a rule fails: empty cart, bad price, sold-out dish, a step that is not allowed.
// It extends Exception (a checked exception), so the compiler makes the windows catch it.
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
