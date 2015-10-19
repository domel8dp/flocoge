package pl.dpawlak.flocoge.diagram;

public class DiagramLoadingException extends Exception {

    private static final long serialVersionUID = -1357460204450644245L;

    public DiagramLoadingException(Throwable cause) {
        super(cause);
    }

    public DiagramLoadingException(String message) {
        super(message);
    }
}
