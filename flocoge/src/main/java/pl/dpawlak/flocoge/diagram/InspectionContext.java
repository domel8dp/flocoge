package pl.dpawlak.flocoge.diagram;

import java.util.HashMap;
import java.util.Map;

import pl.dpawlak.flocoge.model.DecissionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

public class InspectionContext {

    public final FlocogeModel model;

    public boolean valid;
    public String error;
    public ModelElement currentElement;
    public ModelConnection previousElementConnection;
    public Map<String, Integer> currentBranches;

    public InspectionContext(FlocogeModel model) {
        this.model = model;
        valid = true;
    }

    public Map<String, Integer> copyAndAddBranch(Map<String, Integer> branches, int branchIndex) {
        currentBranches = new HashMap<>(branches);
        if (currentElement != null) {
            currentBranches.put(currentElement.id, branchIndex);
        }
        return currentBranches;
    }

    public void addDecissionMeta() {
        if (currentElement != null) {
            model.decissions.put(currentElement.id,
                new DecissionMeta(currentElement.id, currentElement.connections.size()));
        }
    }

    public void moveToNextElement(ModelConnection connection) {
        previousElementConnection = connection;
        currentElement = connection.target;
    }

    public void clearElement() {
        previousElementConnection = null;
        currentElement = null;
    }

    public void setError(String error) {
        valid = false;
        this.error = error;
    }
}
