package pl.dpawlak.flocoge.log;

import pl.dpawlak.flocoge.model.FlocogeModel;

public class ErrorLogger implements Logger {

    private final boolean printStack;
    private final ModelPrinter bareModelPrinter;
    private final ModelPrinter modelPrinter;
    
    public ErrorLogger(boolean printStack, ModelPrinter bareModelPrinter, ModelPrinter modelPrinter) {
        this.printStack = printStack;
        this.bareModelPrinter = bareModelPrinter;
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

    public void printBareModel(FlocogeModel model) {
        if (bareModelPrinter != null) {
            bareModelPrinter.print(model);
        }
    }

    @Override
    public void printModel(FlocogeModel model) {
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
