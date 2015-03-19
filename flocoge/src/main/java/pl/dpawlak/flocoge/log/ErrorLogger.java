package pl.dpawlak.flocoge.log;

import java.util.Collection;

import pl.dpawlak.flocoge.model.ModelElement;

/**
 * Created by dpawlak on Mar 17, 2015
 */
public class ErrorLogger implements Logger {

    private final boolean printStack;
    private final ModelPrinter modelPrinter;
    
    public ErrorLogger(boolean printStack, ModelPrinter modelPrinter) {
        this.printStack = printStack;
        this.modelPrinter = modelPrinter;
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

    @Override
    public void printModel(Collection<ModelElement> model) {
        if (modelPrinter != null) {
            modelPrinter.print(model);
        }
    }

    @Override
    public void log(String msg) { }

    @Override
    public void log(String msg, Object... objects) { }

    @Override
    public void trace(String msg) { }

    @Override
    public void trace(String msg, Object... objects) { }
}
