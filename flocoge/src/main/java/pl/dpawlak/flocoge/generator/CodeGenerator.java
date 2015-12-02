package pl.dpawlak.flocoge.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.DecisionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class CodeGenerator {

    private final Configuration config;
    private final Logger log;
    private final CodeModel codeModel;
    private final Set<String> delegateMethods;
    private final Set<String> externalDelegateMethods;

    private FlocogeModel model;

    public CodeGenerator(Configuration config, Logger log) {
        this.config = config;
        this.log = log;
        delegateMethods = new HashSet<>();
        externalDelegateMethods = new HashSet<>();
        codeModel = new DefaultCodeModel();
    }

    public void generate(FlocogeModel model) throws CodeGenerationException {
        this.model = model;
        log.log("Generating code");
        codeModel.init(config.packageName, config.baseName, model.areExternalCallsPresent());
        fillEntities();
        codeModel.build(config.srcFolder);
    }

    private void fillEntities() throws CodeGenerationException {
        for (Map.Entry<String, ModelElement> path : model.startElements.entrySet()) {
            String methodName = path.getKey();
            ModelElement element = path.getValue();
            switch (element.shape) {
                case ON_PAGE_REF:
                    traverseBranch(codeModel.privateMethod(methodName), getNext(element), null);
                    break;
                case OFF_PAGE_REF:
                    traverseBranch(codeModel.publicExternalMethod(methodName), getNext(element), null);
                    break;
                default:
                    traverseBranch(codeModel.publicMethod(methodName), element, null);
                    break;
            }
        }
    }

    private ModelElement traverseBranch(CodeBlock body, ModelElement startElement, String mergePoint)
            throws CodeGenerationException {
        ModelElement element = startElement;
        log.trace("Start traversing branch, merge point: {}", mergePoint);
        while (element != null && (mergePoint == null || !element.id.equals(mergePoint))) {
            log.trace("Generating element id: {}, label: {}, shape: {}", element.id, element.label, element.shape);
            if (element.shape == Shape.DECISION) {
                if (isBooleanDecision(element)) {
                    element = generateDelegateBooleanCall(body, element, mergePoint);
                } else {
                    element = generateDelegateEnumCall(body, element, mergePoint);
                }
                if (element != null) {
                    log.trace("After decision element id: {}, label: {}, shape: {}", element.id, element.label,
                        element.shape);
                }
            } else {
                switch (element.shape) {
                    case OPERATION:
                        generateDelegateCall(body, element.label);
                        break;
                    case ON_PAGE_REF:
                        generateLocalCall(body, element);
                        break;
                    case OFF_PAGE_REF:
                        generateExternalDelegateCall(body, element);
                        break;
                    default:
                        break;
                }
                element = getNext(element);
            }
        }
        return element;
    }

    private void generateDelegateCall(CodeBlock body, String name) {
        if (!delegateMethods.contains(name)) {
            codeModel.addMethod(name);
            delegateMethods.add(name);
        }
        body.callDelegate(name);
    }

    private void generateLocalCall(CodeBlock body, ModelElement element) {
        body.call(element.label);
    }

    private void generateExternalDelegateCall(CodeBlock body, ModelElement element) {
        if (!externalDelegateMethods.contains(element.label)) {
            codeModel.addExternalMethod(element.label);
            externalDelegateMethods.add(element.label);
        }
        body.callExternal(element.label);
    }

    private ModelElement generateDelegateBooleanCall(CodeBlock body, ModelElement element, String currentMergePoint)
            throws CodeGenerationException {
        if (!delegateMethods.contains(element.label)) {
            codeModel.addBooleanMethod(element.label);
            delegateMethods.add(element.label);
        }
        DecisionMeta meta = model.decisions.get(element.id);
        CodeIf if1 = body._if(element.label);
        ModelElement finalElement = traverseBooleanBranch(if1._then(), 0, element, meta, currentMergePoint);
        ModelElement branchFinalElement = traverseBooleanBranch(if1._else(), 1, element, meta, currentMergePoint);
        if (branchFinalElement != null && finalElement == null) {
            finalElement = branchFinalElement;
        }
        return finalElement;
    }

    private ModelElement traverseBooleanBranch(CodeBlock block, int branchIndex, ModelElement element,
            DecisionMeta meta, String currentMergePoint) throws CodeGenerationException {
        String branchMergePoint = meta.mergePoints[branchIndex];
        String mergePoint = branchMergePoint != null ? branchMergePoint : currentMergePoint;
        ModelElement finalElement = traverseBranch(block, element.connections.get(branchIndex).target, mergePoint);
        if (finalElement == null && mergePoint != null) {
            block._return();
        }
        return finalElement;
    }

    private ModelElement generateDelegateEnumCall(CodeBlock body, ModelElement element, String currentMergePoint)
            throws CodeGenerationException {
        if (!delegateMethods.contains(element.label)) {
            codeModel.addEnumMethod(element.label, getConnectionLabels(element));
            delegateMethods.add(element.label);
        }
        DecisionMeta meta = model.decisions.get(element.id);
        CodeSwitch _switch = body._switch(element.label);
        int index = 0;
        ModelElement finalElement = null;
        for (ModelConnection connection : element.connections) {
            ModelElement branchFinalElement = traverseEnumBranch(_switch, connection, index++, meta, currentMergePoint);
            if (branchFinalElement != null && finalElement == null) {
                finalElement = branchFinalElement;
            }
        }
        return finalElement;
    }

    private List<String> getConnectionLabels(ModelElement element) {
        List<String> labels = new ArrayList<>(element.connections.size());
        for (ModelConnection connection : element.connections) {
            labels.add(connection.label);
        }
        return labels;
    }

    private ModelElement traverseEnumBranch(CodeSwitch _switch, ModelConnection connection, int branchIndex,
            DecisionMeta meta, String currentMergePoint) throws CodeGenerationException {
        CodeBlock caseBody = _switch._case(connection.label);
        String branchMergePoint = meta.mergePoints[branchIndex];
        String mergePoint = branchMergePoint != null ? branchMergePoint : currentMergePoint;
        ModelElement finalElement = traverseBranch(caseBody, connection.target, mergePoint);
        if (finalElement == null && (mergePoint != null || meta.hasMergePoints())) {
            caseBody._return();
        } else {
            caseBody._break();
        }
        return finalElement;
    }

    private ModelElement getNext(ModelElement element) {
        return element.connections.isEmpty() ? null : element.connections.get(0).target;
    }

    private boolean isBooleanDecision(ModelElement element) {
        return element.connections.size() == 2 && element.connections.get(0).label.equals(ModelConnection.TRUE);
    }

    CodeGenerator(Configuration config, Logger log, CodeModel codeModel) {
        this.config = config;
        this.log = log;
        this.codeModel = codeModel;
        delegateMethods = new HashSet<>();
        externalDelegateMethods = new HashSet<>();
    }
}
