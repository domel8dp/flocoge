package pl.dpawlak.flocoge.diagram;

import static org.mockito.Mockito.*;

import java.io.File;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class DiagramLoaderTest {

    private static final File ENCRYPTED_DIAGRAM = new File("src/test/resources/TestDiagramCompressed.xml");
    private static final File PLAIN_DIAGRAM = new File("src/test/resources/TestDiagramPlain");
    private static final File INVALID_FILE = new File("build.gradle");

    @Test
    public void testLoadingEncryptedDiagram() throws DiagramLoadingException {
        FlocogeModel model = new FlocogeModel();
        ModelLoader modelLoader = mockModelLoader();
        DiagramLoader loader = new DiagramLoader(prepareConfig(ENCRYPTED_DIAGRAM), modelLoader, mock(Logger.class));
        loader.loadDiagram(model);
        verify(modelLoader).loadModel(eq(model), any(XMLEventReader.class), argThat(new StartElementMatcher()));
    }

    @Test
    public void testLoadingPlainDiagram() throws DiagramLoadingException {
        FlocogeModel model = new FlocogeModel();
        ModelLoader modelLoader = mockModelLoader();
        DiagramLoader loader = new DiagramLoader(prepareConfig(PLAIN_DIAGRAM), modelLoader, mock(Logger.class));
        loader.loadDiagram(model);
        verify(modelLoader).loadModel(eq(model), any(XMLEventReader.class), argThat(new StartElementMatcher()));
    }

    @Test(expected = DiagramLoadingException.class)
    public void testLoadingInvalidDiagramFile() throws DiagramLoadingException {
        FlocogeModel model = new FlocogeModel();
        ModelLoader modelLoader = mockModelLoader();
        DiagramLoader loader = new DiagramLoader(prepareConfig(INVALID_FILE), modelLoader, mock(Logger.class));
        loader.loadDiagram(model);
    }

    private ModelLoader mockModelLoader() {
        ModelLoader modelLoader = mock(ModelLoader.class);
        when(modelLoader.getFactory()).thenReturn(XMLInputFactory.newInstance());
        return modelLoader;
    }

    private Configuration prepareConfig(File diagramFile) {
        return new Configuration(diagramFile, null, null, null, false, false, false, false, false);
    }

    private static class StartElementMatcher extends ArgumentMatcher<StartElement> {

        @Override
        public boolean matches(Object argument) {
            StartElement element = (StartElement)argument;
            return "mxGraphModel".equals(element.getName().getLocalPart());
        }
    }
}
