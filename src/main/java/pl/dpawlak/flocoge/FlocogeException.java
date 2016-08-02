package pl.dpawlak.flocoge;

public class FlocogeException extends Exception {

    private static final long serialVersionUID = -146363263847184153L;

    public FlocogeException(Throwable cause) {
        super(cause);
    }

    public FlocogeException(String message) {
        super(message);
    }
}
