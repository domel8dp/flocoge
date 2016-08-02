package pl.dpawlak.flocoge;

import java.lang.Thread.UncaughtExceptionHandler;

import pl.dpawlak.flocoge.config.CommandLineConfigParser;
import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.log.Logger;

public class Main {

    public static void main(String[] args) {
        Logger startupLogger = init();
        CommandLineConfigParser parser = new CommandLineConfigParser(startupLogger);
        if (parser.parse(args)) {
            Configuration config = parser.getConfiguration();
            Logger log = Logger.Factory.createStdOutLogger(config);
            try {
                new Flocoge(config, log).generate();
                log.log("Done.");
            } catch (FlocogeException ex) {
                System.exit(1);
            }
        } else {
            System.exit(1);
        }
    }

    private static Logger init() {
        final Logger startupLogger = Logger.Factory.createStartupLogger();
        Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                startupLogger.error("Unexpected error, please report it using: " +
                    "https://github.com/domel8dp/flocoge/issues", e);
                System.exit(1);
            }
        });
        return startupLogger;
    }
}
