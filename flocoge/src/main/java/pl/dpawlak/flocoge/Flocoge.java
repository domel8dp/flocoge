package pl.dpawlak.flocoge;

import javax.xml.stream.XMLInputFactory;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.config.ConfigurationUtils;
import pl.dpawlak.flocoge.config.InvalidConfigurationException;
import pl.dpawlak.flocoge.diagram.DiagramLoader;
import pl.dpawlak.flocoge.diagram.DiagramLoadingException;
import pl.dpawlak.flocoge.diagram.ModelInspector;
import pl.dpawlak.flocoge.diagram.ModelLoader;
import pl.dpawlak.flocoge.generator.CodeGenerationException;
import pl.dpawlak.flocoge.generator.CodeGenerator;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class Flocoge {

    private final Logger log;
    private final Configuration config;
    private final FlocogeModel model;
    private final ModelLoader modelLoader;
    private final DiagramLoader diagramLoader;
    private final ModelInspector inspector;
    private final CodeGenerator generator;

    public Flocoge(Configuration config, Logger log) {
        this.log = log;
        this.config = config;
        model = new FlocogeModel();
        modelLoader = new ModelLoader(XMLInputFactory.newInstance(), log);
        diagramLoader = new DiagramLoader(config, modelLoader, log);
        inspector = new ModelInspector(log);
        generator = new CodeGenerator(config, log);
    }

    public void generate() throws FlocogeException {
        try {
            ConfigurationUtils.check(config);
            diagramLoader.loadDiagram(model);
            log.printBareModel(model);
            if (inspector.inspect(model)) {
                log.printModel(model);
                generator.generate(model);
            } else {
                throw new FlocogeException("Model inspection failed");
            }
        } catch (InvalidConfigurationException configEx) {
            log.error("Diagram loading failed ({})", configEx.getMessage(), configEx);
            throw new FlocogeException(configEx);
        } catch (DiagramLoadingException loadingEx) {
            log.error("Diagram loading failed ({})", loadingEx.getMessage(), loadingEx);
            throw new FlocogeException(loadingEx);
        } catch (CodeGenerationException generationEx) {
            log.error("Code generation failed ({})", generationEx.getMessage(), generationEx);
            throw new FlocogeException(generationEx);
        }
    }

    Flocoge(Logger log, Configuration config, FlocogeModel model, ModelLoader modelLoader, DiagramLoader diagramLoader,
            ModelInspector inspector, CodeGenerator generator) {
        this.log = log;
        this.config = config;
        this.model = model;
        this.modelLoader = modelLoader;
        this.diagramLoader = diagramLoader;
        this.inspector = inspector;
        this.generator = generator;
    }
}
