package pl.dpawlak.flocoge.diagram.util;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import pl.dpawlak.flocoge.diagram.ModelConnection;
import pl.dpawlak.flocoge.diagram.ModelElement;
import pl.dpawlak.flocoge.diagram.ModelElement.Shape;

/**
 * Created by dpawlak on Jan 8, 2015
 */
public class ModelBuilder {
    
    private final Map<String, ModelElement> elements;
    private final Deque<ModelElement> branchStack;
    private ModelElement lastElement;
    
    public ModelBuilder () {
        elements = new LinkedHashMap<>();
        branchStack = new LinkedList<>();
    }
    
    public Map<String, ModelElement> build() {
        return elements;
    }
    
    public ModelBuilder startPath(String id, Shape shape, String label) {
        branchStack.clear();
        lastElement = new ModelElement();
        lastElement.id = id;
        lastElement.shape = shape;
        lastElement.label = label;
        elements.put(id, lastElement);
        return this;
    }
    
    public ModelBuilder connectElement(Shape shape, String label) {
        return connectElement(null, shape, label, null);
    }
    
    public ModelBuilder connectElement(Shape shape, String label, String connectionLabel) {
        return connectElement(null, shape, label, connectionLabel);
    }
    
    public ModelBuilder connectElement(String id, Shape shape, String label, String connectionLabel) {
        ModelElement previousElement = lastElement;
        lastElement = new ModelElement();
        lastElement.id = id;
        lastElement.shape = shape;
        lastElement.label = label;
        ModelConnection connection = new ModelConnection();
        connection.label = connectionLabel;
        connection.target = lastElement;
        previousElement.connections.add(connection);
        return this;
    }
    
    public ModelBuilder branch() {
        branchStack.add(lastElement);
        return this;
    }
    
    public ModelBuilder end() {
        lastElement = branchStack.removeLast();
        return this;
    }
}