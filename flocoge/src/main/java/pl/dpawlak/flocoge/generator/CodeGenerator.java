package pl.dpawlak.flocoge.generator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JSwitch;
import com.sun.codemodel.JVar;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.diagram.ModelNamesUtils;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.DecisionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class CodeGenerator {

    private final Configuration config;
    private final Logger log;
    private final JCodeModel codeModel;
    private final Set<String> delegateMethods;
    private final Set<String> externalDelegateMethods;

    private FlocogeModel model;
    private JDefinedClass interfaceClass;
    private JDefinedClass facadeClass;
    private JDefinedClass externalClass;
    private JFieldVar delegate;
    private JFieldVar externalDelegate;

    public CodeGenerator(Configuration config, Logger log) {
        this.config = config;
        this.log = log;
        codeModel = new JCodeModel();
        delegateMethods = new HashSet<>();
        externalDelegateMethods = new HashSet<>();
    }

    public void generate(FlocogeModel model) throws CodeGenerationException {
        this.model = model;
        log.log("Generating code");
        prepareEmptyEntities();
        fillEntities();
        build();
    }

    private void prepareEmptyEntities() throws CodeGenerationException {
        try {
            String baseName = config.baseName;
            interfaceClass = codeModel._class(config.packageName + "." + baseName, ClassType.INTERFACE);
            facadeClass = codeModel._class(config.packageName + "." + baseName + "Facade");
            if (model.areExternalCallsPresent()) {
                externalClass = codeModel._class(config.packageName + "." + baseName + "External", ClassType.INTERFACE);
            }
            generateFacadeFieldsAndConstructor();
        } catch (JClassAlreadyExistsException exception) {
            throw new CodeGenerationException(exception);
        }
    }

    private void generateFacadeFieldsAndConstructor() {
        String baseName = config.baseName;
        String delegateName = new StringBuilder(baseName.length())
            .append(Character.toLowerCase(baseName.charAt(0)))
            .append(baseName, 1, baseName.length()).toString();
        JMethod constructor = facadeClass.constructor(JMod.PUBLIC);
        delegate = createAndAssignFacadeField(constructor, interfaceClass, delegateName);
        if (model.areExternalCallsPresent()) {
            externalDelegate = createAndAssignFacadeField(constructor, externalClass, delegateName + "External");
        }
    }

    private JFieldVar createAndAssignFacadeField(JMethod constructor, JDefinedClass modelClass, String name) {
        JVar param = constructor.param(modelClass, name);
        constructor.body().assign(JExpr.refthis(name), param);
        return facadeClass.field(JMod.PRIVATE | JMod.FINAL, modelClass, name);
    }

    private void fillEntities() throws CodeGenerationException {
        try {
            for (Map.Entry<String, ModelElement> path : model.startElements.entrySet()) {
                String methodName = path.getKey();
                ModelElement element = path.getValue();
                JMethod method = null;
                switch (element.shape) {
                    case ON_PAGE_REF:
                        method = facadeClass.method(JMod.PRIVATE, codeModel.VOID, methodName);
                        traverseBranch(method.body(), getNext(element), null);
                        break;
                    case OFF_PAGE_REF:
                        method = facadeClass.method(JMod.PUBLIC, codeModel.VOID, methodName);
                        traverseBranch(method.body(), getNext(element), null);
                        break;
                    default:
                        method = facadeClass.method(JMod.PUBLIC, codeModel.VOID, methodName);
                        traverseBranch(method.body(), element, null);
                        break;
                }
            }
        } catch (JClassAlreadyExistsException exception) {
            throw new CodeGenerationException(exception);
        }
    }

    private ModelElement traverseBranch(JBlock body, ModelElement startElement, String mergePoint)
            throws JClassAlreadyExistsException {
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
                        generateDelegateCall(body, element);
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

    private void generateDelegateCall(JBlock body, ModelElement element) {
        if (!delegateMethods.contains(element.label)) {
            interfaceClass.method(JMod.NONE, codeModel.VOID, element.label);
            delegateMethods.add(element.label);
        }
        body.invoke(delegate, element.label);
    }

    private void generateLocalCall(JBlock body, ModelElement element) {
        body.invoke(element.label);
    }

    private void generateExternalDelegateCall(JBlock body, ModelElement element) {
        if (!externalDelegateMethods.contains(element.label)) {
            externalClass.method(JMod.NONE, codeModel.VOID, element.label);
            externalDelegateMethods.add(element.label);
        }
        body.invoke(externalDelegate, element.label);
    }

    private ModelElement generateDelegateBooleanCall(JBlock body, ModelElement element, String currentMergePoint)
            throws JClassAlreadyExistsException {
        if (!delegateMethods.contains(element.label)) {
            interfaceClass.method(JMod.NONE, codeModel.BOOLEAN, element.label);
            delegateMethods.add(element.label);
        }
        DecisionMeta meta = model.decisions.get(element.id);
        JConditional if1 = body._if(JExpr.invoke(delegate, element.label));
        ModelElement finalElement = traverseBooleanBranch(if1._then(), 0, element, meta, currentMergePoint);
        ModelElement branchFinalElement = traverseBooleanBranch(if1._else(), 1, element, meta, currentMergePoint);
        if (branchFinalElement != null && finalElement == null) {
            finalElement = branchFinalElement;
        }
        return finalElement;
    }

    private ModelElement traverseBooleanBranch(JBlock block, int branchIndex, ModelElement element,
            DecisionMeta meta, String currentMergePoint) throws JClassAlreadyExistsException {
        String branchMergePoint = meta.mergePoints[branchIndex];
        String mergePoint = branchMergePoint != null ? branchMergePoint : currentMergePoint;
        ModelElement finalElement = traverseBranch(block, element.connections.get(branchIndex).target, mergePoint);
        if (finalElement == null && mergePoint != null) {
            block._return();
        }
        return finalElement;
    }

    private ModelElement generateDelegateEnumCall(JBlock body, ModelElement element, String currentMergePoint)
            throws JClassAlreadyExistsException {
        if (!delegateMethods.contains(element.label)) {
            JDefinedClass resultEnum = interfaceClass._enum(ModelNamesUtils.createEnumName(element.label));
            for (ModelConnection connection : element.connections) {
                resultEnum.enumConstant(connection.label);
            }
            interfaceClass.method(JMod.NONE, resultEnum, element.label);
            delegateMethods.add(element.label);
        }
        DecisionMeta meta = model.decisions.get(element.id);
        JSwitch _switch = body._switch(JExpr.invoke(delegate, element.label));
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

    private ModelElement traverseEnumBranch(JSwitch _switch, ModelConnection connection, int branchIndex,
            DecisionMeta meta, String currentMergePoint) throws JClassAlreadyExistsException {
        JBlock caseBody = _switch._case(JExpr.ref(connection.label)).body();
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

    private void build() throws CodeGenerationException {
        try {
            codeModel.build(new HeaderCommentFileCodeWriter(config.srcFolder));
        } catch (IOException ioException) {
            throw new CodeGenerationException(ioException);
        }
    }
}
