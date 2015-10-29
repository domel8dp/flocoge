package pl.dpawlak.flocoge.diagram;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.dpawlak.flocoge.model.DecisionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

public class InspectionContext {

    private final FlocogeModel model;

    private boolean valid;
    private ModelElement element;
    private ModelConnection elementConnection;
    private Map<String, Integer> currentBranches;

    public InspectionContext(FlocogeModel model) {
        this.model = model;
        valid = true;
    }

    public Iterator<ModelElement> getStartElementsIterator() {
        return model.startElements.iterator();
    }

    public ModelConnection preparePathStart(ModelElement startElement) {
        ModelConnection branchStart = new ModelConnection();
        branchStart.target = startElement;
        moveToNextElement(branchStart);
        currentBranches = new HashMap<>();
        currentBranches.put(element.id, 0);
        addDecissionMeta();
        return branchStart;
    }

    public boolean isValidAndElementExists() {
        return valid && element != null;
    }

    public ModelElement.Shape getShape() {
        return element.shape;
    }

    public String getLabel() {
        return element.label;
    }

    public DecisionBackup backupDecision() {
        return new DecisionBackup(element, currentBranches);
    }

    public Iterator<ModelConnection> getConnectionsIterator() {
        return element.connections.iterator();
    }

    public void restoreDecisionAndPrepareBranch(DecisionBackup backup, int branchIndex,
            ModelConnection branchConnection) {
        element = backup.element;
        copyAndAddBranch(backup.currentBranches, branchIndex);
        moveToNextElement(branchConnection);
    }

    private Map<String, Integer> copyAndAddBranch(Map<String, Integer> branches, int branchIndex) {
        currentBranches = new HashMap<>(branches);
        if (element != null) {
            currentBranches.put(element.id, branchIndex);
        }
        return currentBranches;
    }

    public void addDecissionMeta() {
        if (element != null) {
            model.decisions.put(element.id, new DecisionMeta(element.id, element.connections.size()));
        }
    }

    public void moveToNextElement() {
        if (element.connections.size() > 0) {
            moveToNextElement(element.connections.get(0));
        } else {
            clearElement();
        }
    }

    private void moveToNextElement(ModelConnection connection) {
        elementConnection = connection;
        element = connection.target;
    }

    public void clearElement() {
        elementConnection = null;
        element = null;
    }

    public void replaceElement(ModelElement element) {
        elementConnection.target = element;
        this.element = element;
    }

    public void updateModelStartElements(List<ModelElement> startElements) {
        model.startElements.clear();
        model.startElements.addAll(startElements);
    }

    public Iterator<Map.Entry<String, Integer>> getCurrentBranchesIterator() {
        return currentBranches.entrySet().iterator();
    }

    public Map<String, List<Integer>> getBranches() {
        return element.branches;
    }

    public String getId() {
        return element.id;
    }

    public boolean isValid() {
        return valid;
    }

    public void markInvalid() {
        valid = false;
    }

    public DecisionMeta getDecisionMeta(String decisionId) {
        return model.decisions.get(decisionId);
    }

    public ModelElement getElement() {
        return element;
    }

    public static class DecisionBackup {
        private final ModelElement element;
        private final  Map<String, Integer> currentBranches;

        public DecisionBackup(ModelElement element, Map<String, Integer> currentBranches) {
            this.element = element;
            this.currentBranches = currentBranches;
        }
    }

    void setCurrentBranches(Map<String, Integer> currentBranches) {
        this.currentBranches = currentBranches;
    }

    void setElement(ModelElement element) {
        this.element = element;
    }
}
