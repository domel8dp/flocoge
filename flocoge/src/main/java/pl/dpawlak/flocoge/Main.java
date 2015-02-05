package pl.dpawlak.flocoge;

import java.util.Map;

import javax.xml.stream.XMLInputFactory;

import pl.dpawlak.flocoge.config.CommandLineConfigParser;
import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.diagram.DiagramLoader;
import pl.dpawlak.flocoge.diagram.DiagramLoadingException;
import pl.dpawlak.flocoge.diagram.ModelLoader;
import pl.dpawlak.flocoge.model.ModelElement;

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
                Map<String, ModelElement> model = diagramLoader.loadDiagram();
            }
        } catch (DiagramLoadingException loadingEx) {
            System.err.println("Code generation failed with reason: " + loadingEx.getMessage());
            System.exit(1);
        }
    }
}
