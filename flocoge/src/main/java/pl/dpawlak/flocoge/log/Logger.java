package pl.dpawlak.flocoge.log;

import java.util.Collection;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.model.ModelElement;

/**
 * Created by dpawlak on Mar 17, 2015
 */
public interface Logger {

    void error(String msg);
    
    void error(String msg, Object... objects);
    
    void log(String msg);
    
    void log(String msg, Object... objects);
    
    void trace(String msg);
    
    void trace(String msg, Object... objects);
    
    void printModel(Collection<ModelElement> model);
    
    public static class Factory {
        
        public static Logger createStartupLogger() {
            return new VerboseLogger(true, null);
        }
        
        public static Logger create(Configuration config) {
            ModelPrinter modelPrinter = null;
            if (config.printModel) {
                modelPrinter = new ModelPrinter();
            }
            if (config.trace) {
                return new TraceLogger(modelPrinter);
            } else if (config.verbose) {
                return new VerboseLogger(config.stacktrace, modelPrinter);
            } else {
                return new ErrorLogger(config.stacktrace, modelPrinter);
            }
        }
    }
    
    public static class Formatter {
        
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
