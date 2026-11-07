package service;

// Thrown when the username or password is wrong.
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
