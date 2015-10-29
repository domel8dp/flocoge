package pl.dpawlak.flocoge.diagram;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pl.dpawlak.flocoge.diagram.InspectionContext.DecisionBackup;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelInspector {

    private final Logger log;
    private final List<ModelElement> transformed;

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
        context.updateModelStartElements(transformed);
        return context.isValid();
    }

    private void initContext(FlocogeModel model) {
        context = new InspectionContext(model);
        elementInspector = new ElementInspectorFacade(new ElementInspectorImpl(log, context), null);
    }

    private void traversePaths() {
        Iterator<ModelElement> startElementIterator = context.getStartElementsIterator();
        while (context.isValid() && startElementIterator.hasNext()) {
            ModelConnection branchStart = context.preparePathStart(startElementIterator.next());
            inspectBranch();
            finishBranch(branchStart);
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

    private void finishBranch(ModelConnection branchStart) {
        ModelElement startElement = branchStart.target;
        if (startElement != null) {
            if (startElement.shape != Shape.ON_PAGE_REF) {
                transformed.add(onPageRefFirstIndex++, startElement);
            } else {
                transformed.add(startElement);
            }
        }
    }
}
