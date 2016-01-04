package pl.dpawlak.flocoge.log;

import pl.dpawlak.flocoge.model.FlocogeModel;

public abstract class BaseLogger implements Logger {

    protected final ModelPrinter bareModelPrinter;
    protected final ModelPrinter modelPrinter;

    public BaseLogger(ModelPrinter bareModelPrinter, ModelPrinter modelPrinter) {
        this.bareModelPrinter = bareModelPrinter;
        this.modelPrinter = modelPrinter;
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
    public void error(String msg) { }

    @Override
    public void error(String msg, Object... objects) { }

    @Override
    public void log(String msg) { }

    @Override
    public void log(String msg, Object... objects) { }

    @Override
    public void trace(String msg) { }

    @Override
    public void trace(String msg, Object... objects) { }
}
