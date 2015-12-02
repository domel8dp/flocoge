package pl.dpawlak.flocoge.model.util;

import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import pl.dpawlak.flocoge.model.DecisionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelBuilder {

    private final FlocogeModel model;
    private final Deque<ModelElement> branchStack;
    private final Map<String, Bookmark> bookmarks;
    private final Map<String, Integer> branches;

    private ModelElement lastElement;
    private int id;

    public ModelBuilder () {
        model = new FlocogeModel();
        branchStack = new LinkedList<>();
        bookmarks = new HashMap<>();
        branches = new HashMap<>();
    }

    public FlocogeModel build() {
        return model;
    }

    public ModelBuilder startPath(Shape shape, String label) {
        return startPath(String.valueOf(++id), shape, label);
    }

    public ModelBuilder startPath(Shape shape, String label, String pathLabel) {
        return startPath(String.valueOf(++id), shape, label, pathLabel);
    }

    public ModelBuilder startPath(String id, Shape shape, String label) {
        preparePath(id, shape, label);
        model.startElements.put(id, lastElement);
        return this;
    }

    public ModelBuilder startPath(String id, Shape shape, String label, String pathLabel) {
        preparePath(id, shape, label);
        model.startElements.put(pathLabel, lastElement);
        return this;
    }

    private void preparePath(String id, Shape shape, String label) {
        branchStack.clear();
        branches.clear();
        lastElement = new ModelElement();
        lastElement.id = id;
        lastElement.shape = shape;
        lastElement.label = label;
        model.elements.put(id, lastElement);
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
        model.elements.put(element.id, element);
        lastElement = element;
        ModelConnection connection = new ModelConnection();
        connection.label = connectionLabel;
        connection.target = lastElement;
        previousElement.connections.add(connection);
        if (element.shape == ModelElement.Shape.OFF_PAGE_REF) {
            model.markExternalCallsPresent();
        }
        return this;
    }

    public ModelBuilder branch() {
        DecisionMeta meta = updateDecisionMetaOnBranch();
        model.decisions.put(lastElement.id, meta);
        branchStack.add(lastElement);
        return this;
    }

    private DecisionMeta updateDecisionMetaOnBranch() {
        DecisionMeta meta = model.decisions.get(lastElement.id);
        if (meta != null) {
            String[] mergePoints = meta.mergePoints;
            meta = new DecisionMeta(lastElement.id, mergePoints.length + 1, meta.openDecissions);
            System.arraycopy(mergePoints, 0, meta.mergePoints, 0, mergePoints.length);
            branches.put(lastElement.id, meta.mergePoints.length - 1);
        } else {
            meta = new DecisionMeta(lastElement.id, 1, Collections.<String>emptySet());
            branches.put(lastElement.id, 0);
        }
        
        return meta;
    }

    public ModelBuilder end() {
        lastElement = branchStack.removeLast();
        branches.remove(lastElement.id);
        return this;
    }

    public ModelBuilder markBookmark(String name) {
        bookmarks.put(name, new Bookmark(lastElement, branches));
        return this;
    }

    public ModelBuilder connectBookmark(String name) {
        return connectBookmark(name, null);
    }

    public ModelBuilder connectBookmark(String name, String connectionLabel) {
        ModelElement previousElement = lastElement;
        Bookmark bookmark = bookmarks.get(name);
        lastElement = bookmark.element;
        ModelConnection connection = new ModelConnection();
        connection.label = connectionLabel;
        connection.target = lastElement;
        previousElement.connections.add(connection);
        updateMergePoints(bookmark);
        return this;
    }

    private void updateMergePoints(Bookmark bookmark) {
        for (Map.Entry<String, Integer> branch : branches.entrySet()) {
            Integer branchIndex = branch.getValue();
            Integer bookmarkBranchIndex = bookmark.branches.get(branch.getKey());
            if (bookmarkBranchIndex != null && !branchIndex.equals(bookmarkBranchIndex)) {
                DecisionMeta meta = model.decisions.get(branch.getKey());
                meta.mergePoints[branchIndex] = meta.mergePoints[bookmarkBranchIndex] = bookmark.element.id;
            }
        }
    }

    private static class Bookmark {
        public final ModelElement element;
        public final Map<String, Integer> branches;
        
        public Bookmark(ModelElement element, Map<String, Integer> branches) {
            this.element = element;
            this.branches = new HashMap<>(branches);
        }
    }
}
