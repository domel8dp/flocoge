package pl.dpawlak.flocoge.log;

public class VerboseLogger extends ErrorLogger {

    public VerboseLogger(boolean printStack, ModelPrinter modelPrinter) {
        super(printStack, modelPrinter);
    }

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public void log(String msg, Object... objects) {
        System.out.println(Formatter.buildMsg(msg, objects));
    }
}
