package pl.dpawlak.flocoge.model;

import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import pl.dpawlak.flocoge.model.ModelElement.Shape;

/**
 * Created by dpawlak on Jan 16, 2015
 */
public class ModelValidator {
    
    private final Map<String, ModelElement> startElements;
    private final Deque<Set<String>> visitedElements;
    
    private boolean valid;
    private String error;

    public ModelValidator(Map<String, ModelElement> startElements) {
        this.startElements = startElements;
        visitedElements = new LinkedList<>();
    }
    
    public boolean validate() {
        valid = true;
        error = "";
        Iterator<ModelElement> startElementIterator = startElements.values().iterator();
        while (valid && startElementIterator.hasNext()) {
            visitedElements.clear();
            visitedElements.add(new HashSet<String>());
            traverseBranch(startElementIterator.next());
        }
        return valid;
    }
    
    private void traverseBranch(ModelElement startElement) {
        Set<String> thisBranchElements = visitedElements.getLast();
        ModelElement element = startElement;
        while (valid && element != null) {
            element = skipUnimportant(element);
            if (valid && element != null && !alreadyVisited(element)) {
                thisBranchElements.add(element.id);
                if (element.shape == Shape.DECISION) {
                    if (checkDecision(element)) {
                        Iterator<ModelConnection> connectionsIterator = element.connections.iterator();
                        while (valid && connectionsIterator.hasNext()) {
                            visitedElements.add(new HashSet<String>());
                            traverseBranch(connectionsIterator.next().target);
                        }
                        element = null;
                    }
                } else if (checkElement(element)) {
                    element = getNextElement(element);
                }
            }
        }
        visitedElements.removeLast();
    }

    private ModelElement skipUnimportant(ModelElement element) {
        ModelElement nextElement = element;
        while (valid && nextElement != null && nextElement.shape == Shape.SKIP) {
            nextElement = getNextElement(nextElement);
        }
        return nextElement;
    }
    
    private boolean alreadyVisited(ModelElement element) {
        Iterator<Set<String>> visitedElementsIterator = visitedElements.descendingIterator();
        while (visitedElementsIterator.hasNext()) {
            if (visitedElementsIterator.next().contains(element.id)) {
                setError("Diagram error (element '" + element.label + "' is part of a loop)");
                return true;
            }
        }
        return false;
    }

    private boolean checkElement(ModelElement element) {
        if (ModelNamesUtils.validateElementLabel(element.label)) {
            element.label = ModelNamesUtils.convertElementLabel(element.label);
            return true;
        } else {
            setError("Diagram error (element has invalid label: '" + element.label + "')");
            return false;
        }
    }
    
    private boolean checkDecision(ModelElement element) {
        if (element.connections.size() <= 1) {
            setError("Diagram error (decision element '" + element.label + "' does not have enough branches)");
            return false;
        } else if (ModelNamesUtils.validateElementLabel(element.label)) {
            element.label = ModelNamesUtils.convertElementLabel(element.label);
            return checkDecisionBranches(element);
        } else {
            setError("Diagram error (decision element has invalid label: '" + element.label + "')");
            return false;
        }
    }
    
    private boolean checkDecisionBranches(ModelElement element) {
        for(ModelConnection connection : element.connections) {
            if (ModelNamesUtils.validateElementLabel(connection.label)) {
                connection.label = ModelNamesUtils.convertConnectionLabel(connection.label);
            } else {
                setError("Diagram error (decision element '" + element.label + "' branch has invalid label: '" +
                    connection.label + "')");
                return false;
            }
        }
        convertBooleanBranches(element);
        return true;
    }
    
    private void convertBooleanBranches(ModelElement element) {
        if (element.connections.size() == 2) {
            String first = element.connections.get(0).label;
            String second = element.connections.get(1).label;
            if (("Y".equals(first) || "YES".equals(first) || "TRUE".equals(first)) &&
                    ("N".equals(second) || "NO".equals(second) || "FALSE".equals(second))) {
                element.connections.get(0).label = "true";
                element.connections.get(1).label = "false";
            } else if (("N".equals(first) || "NO".equals(first) || "FALSE".equals(first)) &&
                    ("Y".equals(second) || "YES".equals(second) || "TRUE".equals(second))) {
                element.connections.get(0).label = "false";
                element.connections.get(1).label = "true";
            }
        }
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
