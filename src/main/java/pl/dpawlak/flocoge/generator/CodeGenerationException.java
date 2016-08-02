package pl.dpawlak.flocoge.generator;

public class CodeGenerationException extends Exception {

    private static final long serialVersionUID = 3195512213509028424L;

    public CodeGenerationException(Throwable cause) {
        super(cause);
    }

    public CodeGenerationException(String message) {
        super(message);
    }
}
