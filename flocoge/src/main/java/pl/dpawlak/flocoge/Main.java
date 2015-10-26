package pl.dpawlak.flocoge;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.xml.stream.XMLInputFactory;

import pl.dpawlak.flocoge.config.CommandLineConfigParser;
import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.diagram.DiagramLoader;
import pl.dpawlak.flocoge.diagram.DiagramLoadingException;
import pl.dpawlak.flocoge.diagram.ModelInspector;
import pl.dpawlak.flocoge.diagram.ModelLoader;
import pl.dpawlak.flocoge.generator.CodeGenerationException;
import pl.dpawlak.flocoge.generator.CodeGenerator;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class Main {

    public static void main(String[] args) {
        Logger startupLogger = init();
        CommandLineConfigParser parser = new CommandLineConfigParser(startupLogger);
        if (parser.parse(args)) {
            Configuration config = parser.getConfiguration();
            Logger log = Logger.Factory.create(config);
            try {
                FlocogeModel model = new FlocogeModel();
                ModelLoader modelLoader = new ModelLoader(XMLInputFactory.newInstance());
                DiagramLoader diagramLoader = new DiagramLoader(config, modelLoader, log);
                diagramLoader.loadDiagram(model);
                ModelInspector inspector = new ModelInspector(log);
                if (inspector.inspect(model)) {
                    log.printModel(model);
                    CodeGenerator generator = new CodeGenerator(config, log);
                    generator.generate(model);
                } else {
                    log.error("Model validation failed ({})", inspector.getError());
                    System.exit(1);
                }
            } catch (DiagramLoadingException loadingEx) {
                log.error("Diagram loading failed ({})", loadingEx.getMessage(), loadingEx);
                System.exit(1);
            } catch (CodeGenerationException generationEx) {
                log.error("Code generation failed ({})", generationEx.getMessage(), generationEx);
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
