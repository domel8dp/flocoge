package pl.dpawlak.flocoge.log;

public class TraceLogger extends VerboseLogger {

    public TraceLogger(ModelPrinter modelPrinter) {
        super(true, modelPrinter);
    }

    @Override
    public void trace(String msg) {
        System.out.println(msg);
    }

    @Override
    public void trace(String msg, Object... objects){
        System.out.println(Formatter.buildMsg(msg, objects));
    }
}
