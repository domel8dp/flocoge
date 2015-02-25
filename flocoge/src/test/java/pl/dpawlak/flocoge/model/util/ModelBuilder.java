package pl.dpawlak.flocoge.model.util;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

/**
 * Created by dpawlak on Jan 8, 2015
 */
public class ModelBuilder {
    
    private final Collection<ModelElement> elements;
    private final Deque<ModelElement> branchStack;
    
    private ModelElement lastElement;
    private int id;
    
    public ModelBuilder () {
        elements = new LinkedList<>();
        branchStack = new LinkedList<>();
    }
    
    public Collection<ModelElement> build() {
        return elements;
    }
    
    public ModelBuilder startPath(Shape shape, String label) {
        return startPath(String.valueOf(++id), shape, label);
    }
    
    public ModelBuilder startPath(String id, Shape shape, String label) {
        branchStack.clear();
        lastElement = new ModelElement();
        lastElement.id = id;
        lastElement.shape = shape;
        lastElement.label = label;
        elements.add(lastElement);
        return this;
    }
    
    public ModelBuilder connectElement(Shape shape, String label) {
        return connectElement(String.valueOf(++id), shape, label, null);
    }
    
    public ModelBuilder connectElement(String id, Shape shape, String label) {
        return connectElement(id, shape, label, null);
    }
    
    public ModelBuilder connectElement(Shape shape, String label, String connectionLabel) {
        return connectElement(String.valueOf(++id), shape, label, connectionLabel);
    }
    
    public ModelBuilder connectElement(String id, Shape shape, String label, String connectionLabel) {
        ModelElement element = new ModelElement();
        element.id = id;
        element.shape = shape;
        element.label = label;
        return connectElement(element, connectionLabel);
    }
    
    public ModelBuilder connectElement(ModelElement element) {
        return connectElement(element, null);
    }
    
    public ModelBuilder connectElement(ModelElement element, String connectionLabel) {
        ModelElement previousElement = lastElement;
        lastElement = element;
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

    public ModelElement getLastElement() {
        return lastElement;
    }
}