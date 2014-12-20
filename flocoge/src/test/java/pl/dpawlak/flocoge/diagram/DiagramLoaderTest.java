package pl.dpawlak.flocoge.diagram;

import static org.mockito.Mockito.*;

import java.io.File;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;

import org.junit.Test;
import org.mockito.ArgumentMatcher;

import pl.dpawlak.flocoge.config.Configuration;

/**
 * Created by dpawlak on Dec 17, 2014
 */
public class DiagramLoaderTest {
    
    private static final File ENCRYPTED_DIAGRAM = new File("src/test/resources/TestDiagramCompressed.xml");
    private static final File PLAIN_DIAGRAM = new File("src/test/resources/TestDiagramPlain");
    private static final File INVALID_FILE = new File("build.gradle");

    @Test
    public void testLoadingEncryptedDiagram() throws DiagramLoadingException {
        ModelLoader modelLoader = mock(ModelLoader.class);
        DiagramLoader loader = new DiagramLoader(new Configuration(ENCRYPTED_DIAGRAM, null, null), modelLoader);
        loader.loadDiagram();
        verify(modelLoader).loadModel(any(XMLEventReader.class), argThat(new StartElementMatcher()));
    }

    @Test
    public void testLoadingPlainDiagram() throws DiagramLoadingException {
        ModelLoader modelLoader = mock(ModelLoader.class);
        DiagramLoader loader = new DiagramLoader(new Configuration(PLAIN_DIAGRAM, null, null), modelLoader);
        loader.loadDiagram();
        verify(modelLoader).loadModel(any(XMLEventReader.class), argThat(new StartElementMatcher()));
    }

    @Test(expected = DiagramLoadingException.class)
    public void testLoadingInvalidDiagramFile() throws DiagramLoadingException {
        ModelLoader modelLoader = mock(ModelLoader.class);
        DiagramLoader loader = new DiagramLoader(new Configuration(INVALID_FILE, null, null), modelLoader);
        loader.loadDiagram();
    }
    
    private static class StartElementMatcher extends ArgumentMatcher<StartElement> {

        @Override
        public boolean matches(Object argument) {
            StartElement element = (StartElement)argument;
            return "mxGraphModel".equals(element.getName().getLocalPart());
        }
    }
}
