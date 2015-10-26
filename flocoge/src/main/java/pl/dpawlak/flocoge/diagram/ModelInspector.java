package pl.dpawlak.flocoge.diagram;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelInspector {

    private final Logger log;
    private final LinkedList<ModelElement> transformed;

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
        updateModel();
        return context.valid;
    }

    public String getError() {
        return context.error;
    }

    private void initContext(FlocogeModel model) {
        context = new InspectionContext(model);
        elementInspector = new ElementInspectorFacade(new ElementInspectorImpl(context), null);
    }

    private void traversePaths() {
        Iterator<ModelElement> startElementIterator = context.model.startElements.iterator();
        while (context.valid && startElementIterator.hasNext()) {
            ModelConnection branchStart = preparePathStart(startElementIterator.next());
            inspectBranch();
            finishBranch(branchStart);
        }
    }

    private ModelConnection preparePathStart(ModelElement startElement) {
        ModelConnection branchStart = new ModelConnection();
        branchStart.target = startElement;
        context.moveToNextElement(branchStart);
        context.copyAndAddBranch(new HashMap<String, Integer>(), 0);
        context.addDecissionMeta();
        return branchStart;
    }

    private void inspectBranch() {
        while (context.valid && context.currentElement != null) {
            log.trace("Inspecting {}: {}", context.currentElement.shape.name(), context.currentElement.label);
            elementInspector.inspectElement();
            if (context.valid && context.currentElement != null) {
                if (context.currentElement.shape == Shape.DECISION) {
                    context.addDecissionMeta();
                    inspectDecissionBranches();
                    context.clearElement();
                } else {
                    moveToNextElement();
                }
            }
        }
    }

    private void inspectDecissionBranches() {
        Map<String, Integer> currentBranches = context.currentBranches;
        ModelElement decisionElement = context.currentElement;
        Iterator<ModelConnection> connectionsIterator = context.currentElement.connections.iterator();
        int branchIndex = 0;
        while (context.valid && connectionsIterator.hasNext()) {
            context.currentElement = decisionElement;
            context.copyAndAddBranch(currentBranches, branchIndex++);
            context.moveToNextElement(connectionsIterator.next());
            inspectBranch();
        }
    }

    private void moveToNextElement() {
        if (context.currentElement.connections.size() > 0) {
            context.moveToNextElement(context.currentElement.connections.get(0));
        } else {
            context.clearElement();
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
    
    private void updateModel() {
        context.model.startElements.clear();
        context.model.startElements.addAll(transformed);
    }
}
