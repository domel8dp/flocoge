package pl.dpawlak.flocoge.log;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.model.FlocogeModel;

public interface Logger {

    void error(String msg);

    void error(String msg, Object... objects);

    void log(String msg);

    void log(String msg, Object... objects);

    void trace(String msg);

    void trace(String msg, Object... objects);

    void printBareModel(FlocogeModel model);

    void printModel(FlocogeModel model);

    class Factory {

        public static Logger createStartupLogger() {
            return new VerboseLogger(true, null, null);
        }

        public static Logger createStdOutLogger(Configuration config) {
            ModelPrinter bareModelPrinter = config.printBareModel ? new BareModelPrinter() : null;
            ModelPrinter modelPrinter = config.printModel ? new TransformedModelPrinter() : null;
            if (config.trace) {
                return new TraceLogger(bareModelPrinter, modelPrinter);
            } else if (config.verbose) {
                return new VerboseLogger(config.stacktrace, bareModelPrinter, modelPrinter);
            } else {
                return new ErrorLogger(config.stacktrace, bareModelPrinter, modelPrinter);
            }
        }
    }

    class Formatter {

        public static String buildMsg(String msg, Object... objects) {
            String[] parts = msg.split("\\{\\}", objects.length + 1);
            StringBuilder sb = new StringBuilder(2 * msg.length());
            for (int i = 0; i < parts.length - 1; i++) {
                sb.append(parts[i]);
                sb.append(objects[i] != null ? objects[i].toString() : "null");
            }
            sb.append(parts[parts.length - 1]);
            return sb.toString();
        }
    }
}
