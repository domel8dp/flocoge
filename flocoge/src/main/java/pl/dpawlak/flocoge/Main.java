package pl.dpawlak.flocoge;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;

import javax.xml.stream.XMLInputFactory;

import pl.dpawlak.flocoge.config.CommandLineConfigParser;
import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.diagram.DiagramLoader;
import pl.dpawlak.flocoge.diagram.DiagramLoadingException;
import pl.dpawlak.flocoge.diagram.ModelLoader;
import pl.dpawlak.flocoge.generator.CodeGenerationException;
import pl.dpawlak.flocoge.generator.CodeGenerator;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelTransformer;
import pl.dpawlak.flocoge.model.ModelValidator;

/**
 * Created by dpawlak on Dec 14, 2014
 */
public class Main {

    public static void main(String[] args) {
        Logger startupLogger = init();
        CommandLineConfigParser parser = new CommandLineConfigParser(startupLogger);
        if (parser.parse(args)) {
            Configuration config = parser.getConfiguration();
            Logger log = Logger.Factory.create(config);
            try {
                ModelLoader modelLoader = new ModelLoader(XMLInputFactory.newInstance());
                DiagramLoader diagramLoader = new DiagramLoader(config, modelLoader, log);
                Collection<ModelElement> model = diagramLoader.loadDiagram();
                ModelValidator validator = new ModelValidator(model, log);
                if (validator.validate()) {
                    ModelTransformer transformer = new ModelTransformer(model, log);
                    model = transformer.transform();
                    log.printModel(model);
                    CodeGenerator generator = new CodeGenerator(model, config, log);
                    generator.generate();
                } else {
                    log.error("Model validation failed ({})", validator.getError());
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
