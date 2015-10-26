package pl.dpawlak.flocoge.diagram;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ElementInspectorImpl implements ElementInspector {

    private final InspectionContext context;

    public ElementInspectorImpl(InspectionContext context) {
        this.context = context;
    }

    @Override
    public void skipElements() {
        ModelElement nextElement = context.currentElement;
        while (context.valid && nextElement != null && nextElement.shape == Shape.SKIP) {
            nextElement = getNextElement(nextElement);
        }
        context.previousElementConnection.target = nextElement;
        context.currentElement = nextElement;
    }

    private ModelElement getNextElement(ModelElement element) {
        int connectionsCount = element.connections.size();
        if (connectionsCount <= 1) {
            return connectionsCount > 0 ? element.connections.get(0).target : null;
        } else {
            context.setError("Diagram error (element '" + element.label + "' has more than one branch)");
            return null;
        }
    }

    @Override
    public boolean isValidAndElementExists() {
        return context.valid && context.currentElement != null;
    }

    @Override
    public void validateBranches() {
        Iterator<Map.Entry<String, Integer>> branchesIterator = context.currentBranches.entrySet().iterator();
        while (context.valid && branchesIterator.hasNext()) {
            Map.Entry<String, Integer> branch = branchesIterator.next();
            MergePointsImpl impl = new MergePointsImpl(context.model, context.currentElement, branch.getKey(),
                branch.getValue(), context.currentBranches);
            new MergePointsFacade(impl, null).inspectNode();
            if (!impl.isValid()) {
                context.setError(impl.getError());
            }
        }
    }

    @Override
    public boolean isValid() {
        return context.valid;
    }

    @Override
    public void validateAndTransformElementLabel() {
        if (ModelNamesUtils.validateElementLabel(context.currentElement.label)) {
            context.currentElement.label = ModelNamesUtils.convertElementLabel(context.currentElement.label);
        } else {
            context.setError("Diagram error (element has invalid label: '" + context.currentElement.label + "')");
        }
    }

    @Override
    public void validateConnections() {
        if (context.currentElement.shape == Shape.DECISION) {
            if (context.currentElement.connections.size() <= 1) {
                context.setError("Diagram error (decision element '" + context.currentElement.label +
                    "' does not have enough branches)");
            }
        } else {
            if (context.currentElement.connections.size() > 1) {
                context.setError("Diagram error (element '" + context.currentElement.label +
                    "' has more than one branch)");
            }
        }
    }

    @Override
    public void validateAndTransformConnectionsLabels() {
        if (context.currentElement.shape == Shape.DECISION) {
            for(ModelConnection connection : context.currentElement.connections) {
                if (ModelNamesUtils.validateElementLabel(connection.label)) {
                    connection.label = ModelNamesUtils.convertConnectionLabel(connection.label);
                } else {
                    context.setError("Diagram error (decision element '" + context.currentElement.label +
                        "' branch has invalid label: '" + connection.label + "')");
                    return;
                }
            }
            convertBooleanBranches();
        }
    }

    private void convertBooleanBranches() {
        if (context.currentElement.connections.size() == 2) {
            String first = context.currentElement.connections.get(0).label;
            String second = context.currentElement.connections.get(1).label;
            if (("Y".equals(first) || "YES".equals(first) || "TRUE".equals(first)) &&
                    ("N".equals(second) || "NO".equals(second) || "FALSE".equals(second))) {
                context.currentElement.connections.get(0).label = ModelConnection.TRUE;
                context.currentElement.connections.get(1).label = ModelConnection.FALSE;
            } else if (("N".equals(first) || "NO".equals(first) || "FALSE".equals(first)) &&
                    ("Y".equals(second) || "YES".equals(second) || "TRUE".equals(second))) {
                context.currentElement.connections.get(0).label = ModelConnection.FALSE;
                context.currentElement.connections.get(1).label = ModelConnection.TRUE;
                Collections.swap(context.currentElement.connections, 0, 1);
            }
        }
    }
}
