package pl.dpawlak.flocoge.diagram;

import org.junit.Test;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.TestModels;
import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.mockito.Mockito.mock;

public class ModelLoaderTest {

    private static final File PLAIN_DIAGRAM = new File("src/diagramTest/resources/TestDiagramPlain");

    @Test
    public void testLoadingModel() throws XMLStreamException, DiagramLoadingException, FileNotFoundException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(PLAIN_DIAGRAM));
        ModelLoader loader = new ModelLoader(factory, mock(Logger.class));
        FlocogeModel model = new FlocogeModel();
        loader.loadModel(model, reader, reader.nextTag().asStartElement());
        new ModelsMatchingValidator(model, TestModels.createTestFileModel()).validate();
    }
}
