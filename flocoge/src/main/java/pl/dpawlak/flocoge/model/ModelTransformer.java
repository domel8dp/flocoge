package pl.dpawlak.flocoge.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import pl.dpawlak.flocoge.model.ModelElement.Shape;

/**
 * Created by dpawlak on Feb 25, 2015
 */
public class ModelTransformer {
    
    private final Collection<ModelElement> startElements;

    public ModelTransformer(Collection<ModelElement> startElements) {
        this.startElements = startElements;
    }
    
    public Collection<ModelElement> transform() {
        LinkedList<ModelElement> transformed = new LinkedList<>();
        int onPageRefFirstIndex = 0;
        for (ModelElement startElement : startElements) {
            ModelElement compactedBranch = compactBranch(startElement);
            if (compactedBranch != null) {
                transformBranch(compactedBranch);
                if (compactedBranch.shape != Shape.ON_PAGE_REF) {
                    transformed.add(onPageRefFirstIndex++, compactedBranch);
                } else {
                    transformed.add(compactedBranch);
                }
            }
        }
        return transformed;
    }
    
    private ModelElement compactBranch(ModelElement startElement) {
        ModelElement compactedStartElement = skipUnimportant(startElement);
        ModelElement element = compactedStartElement;
        while (element != null) {
            if (element.shape == Shape.DECISION) {
                for (ModelConnection connection : element.connections) {
                    connection.target = compactBranch(connection.target);
                }
                element = null;
            } else if (!element.connections.isEmpty()) {
                ModelElement nextElement = getNextElement(element);
                nextElement = skipUnimportant(nextElement);
                element.connections.get(0).target = nextElement;
                element = nextElement;
            } else {
                element = null;
            }
        }
        return compactedStartElement;
    }
    
    private void transformBranch(ModelElement startElement) {
        ModelElement element = startElement;
        while (element != null) {
            if (element.shape == Shape.DECISION) {
                transformDecision(element);
                for (ModelConnection connection : element.connections) {
                    transformBranch(connection.target);
                }
                element = null;
            } else {
                transformElement(element);
                element = getNextElement(element);
            }
        }
    }

    private ModelElement skipUnimportant(ModelElement element) {
        ModelElement nextElement = element;
        while (nextElement != null && nextElement.shape == Shape.SKIP) {
            nextElement = getNextElement(nextElement);
        }
        return nextElement;
    }

    private void transformElement(ModelElement element) {
        element.label = ModelNamesUtils.convertElementLabel(element.label);
    }
    
    private void transformDecision(ModelElement element) {
        element.label = ModelNamesUtils.convertElementLabel(element.label);
        transformDecisionBranches(element);
    }
    
    private void transformDecisionBranches(ModelElement element) {
        for(ModelConnection connection : element.connections) {
            connection.label = ModelNamesUtils.convertConnectionLabel(connection.label);
        }
        convertBooleanBranches(element);
    }
    
    private void convertBooleanBranches(ModelElement element) {
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
    
    private ModelElement getNextElement(ModelElement element) {
        int connectionsCount = element.connections.size();
        return connectionsCount > 0 ? element.connections.get(0).target : null;
    }

}
