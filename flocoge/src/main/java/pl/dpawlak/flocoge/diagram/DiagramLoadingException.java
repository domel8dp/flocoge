package pl.dpawlak.flocoge.diagram;

/**
 * Created by dpawlak on Dec 17, 2014
 */
public class DiagramLoadingException extends Exception {

    private static final long serialVersionUID = -1357460204450644245L;
    
    public DiagramLoadingException(Throwable cause) {
        super(cause);
    }
    
    public DiagramLoadingException(String message) {
        super(message);
    }

}
