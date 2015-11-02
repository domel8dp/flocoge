package pl.dpawlak.flocoge.model.util;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelBuilder {

    private final FlocogeModel model;
    private final Deque<ModelElement> branchStack;
    private final Map<String, ModelElement> bookmarks;

    private ModelElement lastElement;
    private int id;

    public ModelBuilder () {
        model = new FlocogeModel();
        branchStack = new LinkedList<>();
        bookmarks = new HashMap<>();
    }

    public FlocogeModel build() {
        return model;
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
        model.startElements.add(lastElement);
        model.elements.put(id, lastElement);
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
        model.elements.put(id, element);
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

    public ModelBuilder markBookmark(String name) {
        bookmarks.put(name, lastElement);
        return this;
    }

    public ModelBuilder connectBookmark(String name) {
        return connectBookmark(name, null);
    }

    public ModelBuilder connectBookmark(String name, String connectionLabel) {
        ModelElement previousElement = lastElement;
        lastElement = bookmarks.get(name);
        ModelConnection connection = new ModelConnection();
        connection.label = connectionLabel;
        connection.target = lastElement;
        previousElement.connections.add(connection);
        return this;
    }
}
