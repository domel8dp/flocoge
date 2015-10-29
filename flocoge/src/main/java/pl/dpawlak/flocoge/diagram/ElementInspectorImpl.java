package pl.dpawlak.flocoge.diagram;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ElementInspectorImpl implements ElementInspector {

    private final Logger log;
    private final InspectionContext context;

    public ElementInspectorImpl(Logger log, InspectionContext context) {
        this.log = log;
        this.context = context;
    }

    @Override
    public void skipElements() {
        ModelElement nextElement = context.getElement();
        while (context.isValid() && nextElement != null && nextElement.shape == Shape.SKIP) {
            nextElement = getNextElement(nextElement);
        }
        context.replaceElement(nextElement);
    }

    private ModelElement getNextElement(ModelElement element) {
        int connectionsCount = element.connections.size();
        if (connectionsCount <= 1) {
            return connectionsCount > 0 ? element.connections.get(0).target : null;
        } else {
            setError("Diagram error (element '{}' has more than one branch)", element.label);
            return null;
        }
    }

    @Override
    public boolean isValidAndElementExists() {
        return context.isValidAndElementExists();
    }

    @Override
    public void validateBranches() {
        Iterator<Map.Entry<String, Integer>> branchesIterator = context.getCurrentBranchesIterator();
        while (context.isValid() && branchesIterator.hasNext()) {
            Map.Entry<String, Integer> branch = branchesIterator.next();
            MergePointsImpl impl = new MergePointsImpl(log, context, branch.getKey(), branch.getValue());
            new MergePointsFacade(impl, null).inspectNode();
        }
    }

    @Override
    public boolean isValid() {
        return context.isValid();
    }

    @Override
    public void validateAndTransformElementLabel() {
        ModelElement element = context.getElement();
        if (ModelNamesUtils.validateElementLabel(element.label)) {
            element.label = ModelNamesUtils.convertElementLabel(element.label);
        } else {
            setError("Diagram error (element has invalid label: '{}')", element.label);
        }
    }

    @Override
    public void validateConnections() {
        ModelElement element = context.getElement();
        if (element.shape == Shape.DECISION) {
            if (element.connections.size() <= 1) {
                setError("Diagram error (decision element '{}' does not have enough branches)", element.label);
            }
        } else {
            if (element.connections.size() > 1) {
                setError("Diagram error (element '{}' has more than one branch)", element.label);
            }
        }
    }

    @Override
    public void validateAndTransformConnectionsLabels() {
        ModelElement element = context.getElement();
        if (element.shape == Shape.DECISION) {
            for(ModelConnection connection : element.connections) {
                if (ModelNamesUtils.validateElementLabel(connection.label)) {
                    connection.label = ModelNamesUtils.convertConnectionLabel(connection.label);
                } else {
                    setError("Diagram error (decision element '{}' branch has invalid label: '{}')", element.label,
                        connection.label);
                    return;
                }
            }
            convertBooleanBranches();
        }
    }

    private void convertBooleanBranches() {
        ModelElement element = context.getElement();
        if (element.connections.size() == 2) {
            String first = element.connections.get(0).label;
            String second = element.connections.get(1).label;
            if (("Y".equals(first) || "YES".equals(first) || "TRUE".equals(first)) &&
                    ("N".equals(second) || "NO".equals(second) || "FALSE".equals(second))) {
                element.connections.get(0).label = ModelConnection.TRUE;
                element.connections.get(1).label = ModelConnection.FALSE;
            } else if (("N".equals(first) || "NO".equals(first) || "FALSE".equals(first)) &&
                    ("Y".equals(second) || "YES".equals(second) || "TRUE".equals(second))) {
                element.connections.get(0).label = ModelConnection.FALSE;
                element.connections.get(1).label = ModelConnection.TRUE;
                Collections.swap(element.connections, 0, 1);
            }
        }
    }

    private void setError(String msg, Object... objects) {
        context.markInvalid();
        log.error(msg, objects);
    }
}
