package pl.dpawlak.flocoge.diagram;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class PathNameExtractorImpl implements PathNameExtractor {

    private final Logger log;

    private ModelElement element;
    private boolean valid;
    private String label;

    public PathNameExtractorImpl(Logger log, ModelElement element) {
        this.log = log;
        this.element = element;
        valid = true;
    }

    @Override
    public boolean hasStartElement() {
        return element.shape == Shape.START;
    }

    @Override
    public boolean isEmptyOrStart() {
        return element.label == null || element.label.trim().length() == 0 || "start".equals(element.label);
    }

    @Override
    public void useNextElementLabel() {
        skipElements();
        useElementLabel();
    }

    @Override
    public void useElementLabel() {
        if (element != null) {
            label = element.label;
        } else {
            setError("Diagram error (path without valid start element)");
        }
    }

    private void skipElements() {
        while (element != null) {
            if (!element.connections.isEmpty()) {
                element = element.connections.get(0).target;
                if (element != null && (!hasStartElement() || !isEmptyOrStart())) {
                    break;
                }
            } else {
                element = null;
            }
        }
    }

    private void setError(String msg, Object... objects) {
        log.error(msg, objects);
        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    public String getLabel() {
        return label;
    }
}
