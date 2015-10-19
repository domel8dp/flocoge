package pl.dpawlak.flocoge.diagram;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.DecissionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelValidator {

    private final Logger log;

    private FlocogeModel model;
    private boolean valid;
    private String error;

    public ModelValidator(Logger log) {
        this.log = log;
    }

    public boolean validate(FlocogeModel model) {
        this.model = model;
        log.log("Validating model");
        valid = true;
        error = "";
        Iterator<ModelElement> startElementIterator = model.startElements.iterator();
        while (valid && startElementIterator.hasNext()) {
            ModelElement startElement = startElementIterator.next();
            addDecissionMeta(startElement, 1);
            Map<String, Integer> branches = addBranch(new HashMap<String, Integer>(), startElement, 0);
            traverseBranch(startElement, branches);
        }
        return valid;
    }

    private Map<String, Integer> addBranch(Map<String, Integer> currentBranches, ModelElement element, int branchIndex) {
        Map<String, Integer> branches = new HashMap<>(currentBranches);
        if (element != null) {
            branches.put(element.id, branchIndex);
        }
        return branches;
    }

    private void addDecissionMeta(ModelElement element, int branchCount) {
        if (element != null) {
            model.decissions.put(element.id, new DecissionMeta(element.id, branchCount));
        }
    }

    private void traverseBranch(ModelElement startElement, Map<String, Integer> currentBranches) {
        ModelElement element = startElement;
        while (valid && element != null) {
            element = skipUnimportant(element);
            if (valid && element != null) {
                checkBranches(element, currentBranches);
                if (valid && element.shape == Shape.DECISION) {
                    if (checkDecision(element)) {
                        addDecissionMeta(element, element.connections.size());
                        Iterator<ModelConnection> connectionsIterator = element.connections.iterator();
                        int branchIndex = 0;
                        while (valid && connectionsIterator.hasNext()) {
                            Map<String, Integer> newBranches = addBranch(currentBranches, element, branchIndex++);
                            traverseBranch(connectionsIterator.next().target, newBranches);
                        }
                        element = null;
                    }
                } else if (valid && checkElement(element)) {
                    element = getNextElement(element);
                }
            }
        }
    }

    private ModelElement skipUnimportant(ModelElement element) {
        ModelElement nextElement = element;
        while (valid && nextElement != null && nextElement.shape == Shape.SKIP) {
            nextElement = getNextElement(nextElement);
        }
        return nextElement;
    }

    private void checkBranches(ModelElement element, Map<String, Integer> currentBranches) {
        Iterator<Map.Entry<String, Integer>> branchesIterator = currentBranches.entrySet().iterator();
        while (valid && branchesIterator.hasNext()) {
            Map.Entry<String, Integer> branch = branchesIterator.next();
            MergePointsImpl impl = new MergePointsImpl(model, element, branch.getKey(), branch.getValue(),
                currentBranches);
            new MergePointsFacade(impl, null).inspectNode();
            if (!impl.isValid()) {
                setError(impl.getError());
            }
        }
    }

    private boolean checkElement(ModelElement element) {
        if (!ModelNamesUtils.validateElementLabel(element.label)) {
            setError("Diagram error (element has invalid label: '" + element.label + "')");
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDecision(ModelElement element) {
        if (element.connections.size() <= 1) {
            setError("Diagram error (decision element '" + element.label + "' does not have enough branches)");
            return false;
        } else if (ModelNamesUtils.validateElementLabel(element.label)) {
            return checkDecisionBranches(element);
        } else {
            setError("Diagram error (decision element has invalid label: '" + element.label + "')");
            return false;
        }
    }

    private boolean checkDecisionBranches(ModelElement element) {
        for(ModelConnection connection : element.connections) {
            if (!ModelNamesUtils.validateElementLabel(connection.label)) {
                setError("Diagram error (decision element '" + element.label + "' branch has invalid label: '" +
                    connection.label + "')");
                return false;
            }
        }
        return true;
    }

    private ModelElement getNextElement(ModelElement element) {
        int connectionsCount = element.connections.size();
        if (connectionsCount <= 1) {
            return connectionsCount > 0 ? element.connections.get(0).target : null;
        } else {
            setError("Diagram error (element '" + element.label + "' has more than one branch)");
            return null;
        }
    }

    private void setError(String error) {
        valid = false;
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
