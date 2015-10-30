package pl.dpawlak.flocoge.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class FlocogeModel {

    public final Collection<ModelElement> startElements;
    public final Map<String, ModelElement> elements;
    public final Map<String, DecisionMeta> decisions;

    private boolean externalCallsPresent;

    public FlocogeModel() {
        startElements = new LinkedList<>();
        elements = new LinkedHashMap<>();
        decisions = new LinkedHashMap<>();
    }

    public boolean areExternalCallsPresent() {
        return externalCallsPresent;
    }

    public void markExternalCallsPresent() {
        this.externalCallsPresent = true;
    }
}
