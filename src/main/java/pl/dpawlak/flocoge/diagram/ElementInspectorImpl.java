package pl.dpawlak.flocoge.diagram;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
            log.trace("  Skipping {}(label: '{}', id: {}) ...", nextElement.shape.name(), nextElement.label,
                nextElement.id);
            context.removeElement(nextElement.id);
            nextElement = getNextElement(nextElement);
        }
        if (nextElement != null && context.getElement() != null && !nextElement.id.equals(context.getElement().id)) {
            log.trace("Inspecting {}(label: '{}', id: {}) ...", nextElement.shape.name(), nextElement.label,
                nextElement.id);
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
        log.trace("  Validating branches...");
        Iterator<Map.Entry<String, Integer>> branchesIterator = context.getCurrentBranchesIterator();
        while (context.isValid() && branchesIterator.hasNext()) {
            Map.Entry<String, Integer> branch = branchesIterator.next();
            log.trace("  Inspecting merge point for decision id: {}, branch index: {} ...", branch.getKey(),
                branch.getValue());
            MergePointsImpl impl = new MergePointsImpl(log, context, branch.getKey(), branch.getValue());
            new MergePointsFacade(impl).inspectNode();
        }
    }

    @Override
    public boolean isValid() {
        return context.isValid();
    }

    @Override
    public void validateAndTransformElementLabel() {
        log.trace("  Inspecting element label...");
        ModelElement element = context.getElement();
        if (element.shape == Shape.START && (element.label == null || element.label.trim().length() == 0)) {
            return;
        }
        boolean valid = ModelNamesUtils.validateElementLabel(element.label);
        if (valid) {
            element.label = ModelNamesUtils.convertElementLabel(element.label);
            valid = !ModelNamesUtils.isReservedWord(element.label);
        }
        if (!valid) {
            setError("Diagram error (element has invalid label: '{}')", element.label);
        }
    }

    @Override
    public void validateConnections() {
        log.trace("  Validating connections...");
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
        log.trace("  Inspecting connection labels...");
        ModelElement element = context.getElement();
        if (element.shape == Shape.DECISION) {
            Set<String> branchLabels = new HashSet<>();
            for (ModelConnection connection : element.connections) {
                if (ModelNamesUtils.validateElementLabel(connection.label)) {
                    connection.label = ModelNamesUtils.convertConnectionLabel(connection.label);
                    if (!branchLabels.contains(connection.label)) {
                        branchLabels.add(connection.label);
                    } else {
                        setError("Diagram error (decision element '{}' has multiple branches with same label: '{}')",
                                element.label, connection.label);
                            return;
                    }
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

    @Override
    public void markIfExternalCall() {
        if (context.getShape() == Shape.OFF_PAGE_REF) {
            context.markExternalCall();
        }
    }

    private void setError(String msg, Object... objects) {
        context.markInvalid();
        log.error(msg, objects);
    }
}
