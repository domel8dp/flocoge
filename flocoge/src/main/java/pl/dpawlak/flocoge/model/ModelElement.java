package pl.dpawlak.flocoge.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelElement {

    public enum Shape { EVENT, OPERATION, DECISION, ON_PAGE_REF, OFF_PAGE_REF, SKIP }

    public final List<ModelConnection> connections;
    public final Map<String, List<Integer>> branches;

    public String id;
    public Shape shape;
    public String label;

    public ModelElement() {
        connections = new LinkedList<>();
        branches = new HashMap<>();
    }
}
