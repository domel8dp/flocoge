package pl.dpawlak.flocoge;

import java.util.Collection;

import javax.xml.stream.XMLInputFactory;

import pl.dpawlak.flocoge.config.CommandLineConfigParser;
import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.diagram.DiagramLoader;
import pl.dpawlak.flocoge.diagram.DiagramLoadingException;
import pl.dpawlak.flocoge.diagram.ModelLoader;
import pl.dpawlak.flocoge.generator.CodeGenerationException;
import pl.dpawlak.flocoge.generator.CodeGenerator;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelTransformer;
import pl.dpawlak.flocoge.model.ModelValidator;

/**
 * Created by dpawlak on Dec 14, 2014
 */
public class Main {

    public static void main(String[] args) {
        try {
            CommandLineConfigParser parser = new CommandLineConfigParser();
            if (parser.parse(args)) {
                Configuration config = parser.getConfiguration();
                ModelLoader modelLoader = new ModelLoader(XMLInputFactory.newInstance());
                DiagramLoader diagramLoader = new DiagramLoader(config, modelLoader);
                Collection<ModelElement> model = diagramLoader.loadDiagram();
                ModelValidator validator = new ModelValidator(model);
                if (validator.validate()) {
                    ModelTransformer transformer = new ModelTransformer(model);
                    model = transformer.transform();
                    CodeGenerator generator = new CodeGenerator(model, config);
                    generator.generate();
                } else {
                    System.err.println("Code generation failed with reason: " + validator.getError());
                    System.exit(1);
                }
            }
        } catch (DiagramLoadingException loadingEx) {
            System.err.println("Diagram loading failed with reason: " + loadingEx.getMessage());
            System.exit(1);
        } catch (CodeGenerationException generationEx) {
            System.err.println("Code generation failed with reason: " + generationEx.getMessage());
            System.exit(1);
        }
    }
}
