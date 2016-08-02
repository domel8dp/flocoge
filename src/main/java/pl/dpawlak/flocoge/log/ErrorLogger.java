package pl.dpawlak.flocoge.log;

public class ErrorLogger extends BaseLogger {

    private final boolean printStack;
    
    public ErrorLogger(boolean printStack, ModelPrinter bareModelPrinter, ModelPrinter modelPrinter) {
        super(bareModelPrinter, modelPrinter);
        this.printStack = printStack;
    }

    @Override
    public void error(String msg) {
        System.err.println(msg);
    }

    @Override
    public void error(String msg, Object... objects) {
        System.err.println(Formatter.buildMsg(msg, objects));
        if (printStack) {
            Object last = objects[objects.length - 1];
            if (last instanceof Throwable) {
                ((Throwable)last).printStackTrace();
            }
        }
    }
}
