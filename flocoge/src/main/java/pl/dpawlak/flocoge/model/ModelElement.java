package pl.dpawlak.flocoge.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by dpawlak on Jan 5, 2015
 */
public class ModelElement {
    
    public enum Shape {EVENT, OPERATION, DECISION, ON_PAGE_REF, OFF_PAGE_REF, SKIP}
    
    public String id;
    public Shape shape;
    public String label;
    public final List<ModelConnection> connections;
    
    public ModelElement() {
        connections = new LinkedList<>();
    }
}