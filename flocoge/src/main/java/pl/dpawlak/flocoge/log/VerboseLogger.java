package pl.dpawlak.flocoge.log;

/**
 * Created by dpawlak on Mar 17, 2015
 */
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
