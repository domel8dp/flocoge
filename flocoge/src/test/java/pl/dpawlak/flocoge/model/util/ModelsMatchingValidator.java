package pl.dpawlak.flocoge.model.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

/**
 * Created by dpawlak on Jan 8, 2015
 */
public class ModelsMatchingValidator {
    
    private final Collection<ModelElement> elements;
    private final Collection<ModelElement> expectedElements;
    
    public ModelsMatchingValidator(Collection<ModelElement> elements, Collection<ModelElement> expectedElements) {
        this.elements = elements;
        this.expectedElements = expectedElements;
    }
    
    public void validate() {
        assertEquals(expectedElements.size(), elements.size());
        Iterator<ModelElement> expectedElementsIt = expectedElements.iterator();
        Iterator<ModelElement> elementsIt = elements.iterator();
        while (expectedElementsIt.hasNext() && elementsIt.hasNext()) {
            compareElements(expectedElementsIt.next(), elementsIt.next());
        }
    }
    
    private void compareElements(ModelElement expectedElement, ModelElement element) {
        ModelElement expected = skipUnimportant(expectedElement);
        ModelElement actual = skipUnimportant(element);
        if (expected != null && actual != null) {
            assertEquals(expected.shape, actual.shape);
            assertEquals(expected.label, actual.label);
            if (expected.connections.size() == 0 && actual.connections.size() == 1) {
                compareElements(null, actual.connections.get(0).target);
            } else {
                assertEquals(expected.connections.size(), actual.connections.size());
                Iterator<ModelConnection> expectedConnectionsIt = expected.connections.iterator();
                Iterator<ModelConnection> connectionsIt = actual.connections.iterator();
                while (expectedConnectionsIt.hasNext() && connectionsIt.hasNext()) {
                    ModelConnection expectedConnection = expectedConnectionsIt.next();
                    ModelConnection connection = connectionsIt.next();
                    assertEquals(expectedConnection.label, connection.label);
                    compareElements(expectedConnection.target, connection.target);
                }
            }
        } else {
            assertNull(expected);
            assertNull(actual);
        }
    }
    
    private ModelElement skipUnimportant(ModelElement element) {
        ModelElement nextElement = element;
        while (nextElement != null && nextElement.shape == Shape.SKIP) {
            int connectionsCount = nextElement.connections.size();
            assertTrue(connectionsCount <= 1);
            nextElement = connectionsCount > 0 ? nextElement.connections.get(0).target : null;
        }
        return nextElement;
    }
}