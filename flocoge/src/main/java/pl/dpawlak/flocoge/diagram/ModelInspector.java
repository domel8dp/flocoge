package pl.dpawlak.flocoge.diagram;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.dpawlak.flocoge.diagram.InspectionContext.DecisionBackup;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelInspector {

    private final Logger log;
    private final List<ModelConnection> transformed;

    private InspectionContext context;
    private ElementInspectorFacade elementInspector;
    private int onPageRefFirstIndex;

    public ModelInspector(Logger log) {
        this.log = log;
        transformed = new LinkedList<>();
        onPageRefFirstIndex = 0;
    }

    public boolean inspect(FlocogeModel model) {
        initContext(model);
        traversePaths();
        if (context.isValid() && verifyElementUniqueness(model)) {
            return updateModelStartElements(model);
        } else {
            return false;
        }
    }

    private boolean verifyElementUniqueness(FlocogeModel model) {
        Map<String, ModelElement.Shape> elementShapes = new HashMap<>();
        Map<String, String[]> decisionBranches = new HashMap<>();
        for (ModelElement element : model.elements.values()) {
            if (!verifyElementShapes(element, elementShapes) || !verifyDecisionBranches(element, decisionBranches)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyElementShapes(ModelElement element, Map<String, ModelElement.Shape> elementShapes) {
        if (elementShapes.containsKey(element.label)) {
            if (element.shape != elementShapes.get(element.label)) {
                log.error("Diagram error (elements '{}' have multiple shapes)", element.label);
                return false;
            }
        } else {
            elementShapes.put(element.label, element.shape);
        }
        return true;
    }

    private boolean verifyDecisionBranches(ModelElement element, Map<String, String[]> decisionBranches) {
        if (element.shape == Shape.DECISION) {
            if (decisionBranches.containsKey(element.label)) {
                if (!Arrays.equals(getBranches(element), decisionBranches.get(element.label))) {
                    log.error("Diagram error (decisions '{}' have different branches)", element.label);
                    return false;
                }
            } else {
                decisionBranches.put(element.label, getBranches(element));
            }
        }
        return true;
    }

    private String[] getBranches(ModelElement element) {
        String[] branches = new String[element.connections.size()];
        int i = 0;
        for (ModelConnection connection : element.connections) {
            branches[i++] = connection.label;
        }
        Arrays.sort(branches);
        return branches;
    }

    private void initContext(FlocogeModel model) {
        context = new InspectionContext(model);
        elementInspector = new ElementInspectorFacade(new ElementInspectorImpl(log, context));
    }

    private void traversePaths() {
        Iterator<ModelElement> startElementIterator = context.getStartElementsIterator();
        while (context.isValid() && startElementIterator.hasNext()) {
            ModelConnection branchStart = context.preparePathStart(startElementIterator.next());
            inspectBranch();
            if (context.isValid() && extractPathName(branchStart)) {
                finishBranch(branchStart);
            }
        }
    }

    private void inspectBranch() {
        while (context.isValidAndElementExists()) {
            log.trace("Inspecting {}: {}", context.getShape().name(), context.getLabel());
            elementInspector.inspectElement();
            if (context.isValidAndElementExists()) {
                if (context.getShape() == Shape.DECISION) {
                    context.addDecissionMeta();
                    inspectDecissionBranches();
                    context.clearElement();
                } else {
                    context.moveToNextElement();
                }
            }
        }
    }

    private void inspectDecissionBranches() {
        DecisionBackup backup = context.backupDecision();
        Iterator<ModelConnection> connectionsIterator = context.getConnectionsIterator();
        int branchIndex = 0;
        while (context.isValid() && connectionsIterator.hasNext()) {
            context.restoreDecisionAndPrepareBranch(backup, branchIndex++, connectionsIterator.next());
            inspectBranch();
        }
    }

    private boolean extractPathName(ModelConnection branchStart) {
        if (branchStart.target != null) {
            PathNameExtractorImpl extractor = new PathNameExtractorImpl(log, branchStart.target);
            new PathNameExtractorFacade(extractor).extract();
            if (extractor.isValid()) {
                branchStart.label = extractor.getLabel();
                return true;
            } else {
                context.markInvalid();
                return false;
            }
        } else {
            return false;
        }
    }

    private void finishBranch(ModelConnection branchStart) {
        if (branchStart.target.shape != Shape.ON_PAGE_REF) {
            transformed.add(onPageRefFirstIndex++, branchStart);
        } else {
            transformed.add(branchStart);
        }
    }

    private boolean updateModelStartElements(FlocogeModel model) {
        model.startElements.clear();
        for (ModelConnection branchStart : transformed) {
            if (!model.startElements.containsKey(branchStart.label)) {
                model.startElements.put(branchStart.label, branchStart.target);
            } else {
                log.error("Diagram error (multiple paths have the same name: '{}')", branchStart.label);
                return false;
            }
        }
        return true;
    }
}
