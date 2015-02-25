package pl.dpawlak.flocoge.diagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import pl.dpawlak.flocoge.model.CommonTestModels;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

/**
 * Created by dpawlak on Dec 30, 2014
 */
public class ModelLoaderTest {
    
    private static final File PLAIN_DIAGRAM = new File("src/test/resources/TestDiagramPlain");
    
    @Test
    public void testLoadingModel() throws XMLStreamException, DiagramLoadingException, FileNotFoundException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(PLAIN_DIAGRAM));
        
        ModelLoader loader = new ModelLoader(factory);
        Collection<ModelElement> startElements = loader.loadModel(reader, reader.nextTag().asStartElement());
        
        new ModelsMatchingValidator(startElements, CommonTestModels.createTestFileModel()).validate();
    }

}
