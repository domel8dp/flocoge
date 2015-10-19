package pl.dpawlak.flocoge.model.util;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

public class ModelsMatchingValidator {

    private final Collection<ModelElement> elements;
    private final Collection<ModelElement> expectedElements;

    public ModelsMatchingValidator(FlocogeModel model, FlocogeModel expectedModel) {
        elements = model.startElements;
        expectedElements = expectedModel.startElements;
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
        ModelElement expected = expectedElement;
        ModelElement actual = element;
        if (expected != null && actual != null) {
            assertEquals(expected.label, actual.label);
            assertEquals(expected.shape, actual.shape);
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
}
