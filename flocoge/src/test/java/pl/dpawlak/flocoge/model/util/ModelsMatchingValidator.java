package pl.dpawlak.flocoge.model.util;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Map;

import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

public class ModelsMatchingValidator {

    private final Map<String, ModelElement> paths;
    private final Map<String, ModelElement> expectedPaths;

    public ModelsMatchingValidator(FlocogeModel model, FlocogeModel expectedModel) {
        paths = model.startElements;
        expectedPaths = expectedModel.startElements;
    }

    public void validate() {
        assertEquals(expectedPaths.size(), paths.size());
        Iterator<Map.Entry<String, ModelElement>> expectedPathsIt = expectedPaths.entrySet().iterator();
        Iterator<Map.Entry<String, ModelElement>> pathsIt = paths.entrySet().iterator();
        while (expectedPathsIt.hasNext() && pathsIt.hasNext()) {
            Map.Entry<String, ModelElement> expectedPath = expectedPathsIt.next();
            Map.Entry<String, ModelElement> path = pathsIt.next();
            assertEquals(expectedPath.getKey(), path.getKey());
            compareElements(expectedPath.getValue(), path.getValue());
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
