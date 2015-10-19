package pl.dpawlak.flocoge.generator;

import java.io.IOException;
import java.util.HashSet;
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
            externalClass = codeModel._class(config.packageName + "." + baseName + "External", ClassType.INTERFACE);
            facadeClass = codeModel._class(config.packageName + "." + baseName + "Facade");
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
        externalDelegate = createAndAssignFacadeField(constructor, externalClass, delegateName + "External");
    }

    private JFieldVar createAndAssignFacadeField(JMethod constructor, JDefinedClass modelClass, String name) {
        JVar param = constructor.param(modelClass, name);
        constructor.body().assign(JExpr.refthis(name), param);
        return facadeClass.field(JMod.PRIVATE | JMod.FINAL, modelClass, name);
    }

    private void fillEntities() throws CodeGenerationException {
        try {
            for (ModelElement element : model.startElements) {
                JMethod method = null;
                switch (element.shape) {
                    case ON_PAGE_REF:
                        method = facadeClass.method(JMod.PRIVATE, codeModel.VOID, element.label);
                        traverseBranch(method.body(), getNext(element));
                        break;
                    case OFF_PAGE_REF:
                        method = facadeClass.method(JMod.PUBLIC, codeModel.VOID, element.label);
                        traverseBranch(method.body(), getNext(element));
                        break;
                    default:
                        method = facadeClass.method(JMod.PUBLIC, codeModel.VOID, element.label);
                        traverseBranch(method.body(), element);
                        break;
                }
            }
        } catch (JClassAlreadyExistsException exception) {
            throw new CodeGenerationException(exception);
        }
    }

    private void traverseBranch(JBlock body, ModelElement startElement) throws JClassAlreadyExistsException {
        ModelElement element = startElement;
        while (element != null) {
            if (element.shape == Shape.DECISION) {
                if (isBooleanDecision(element)) {
                    generateDelegateBooleanCall(body, element);
                } else {
                    generateDelegateEnumCall(body, element);
                }
                element = null;
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

    private void generateDelegateBooleanCall(JBlock body, ModelElement element) throws JClassAlreadyExistsException {
        if (!delegateMethods.contains(element.label)) {
            interfaceClass.method(JMod.NONE, codeModel.BOOLEAN, element.label);
            delegateMethods.add(element.label);
        }
        JConditional if1 = body._if(JExpr.invoke(delegate, element.label));
        traverseBranch(if1._then(), element.connections.get(0).target);
        traverseBranch(if1._else(), element.connections.get(1).target);
    }

    private void generateDelegateEnumCall(JBlock body, ModelElement element) throws JClassAlreadyExistsException {
        if (!delegateMethods.contains(element.label)) {
            JDefinedClass resultEnum = interfaceClass._enum(ModelNamesUtils.createEnumName(element.label));
            for (ModelConnection connection : element.connections) {
                resultEnum.enumConstant(connection.label);
            }
            interfaceClass.method(JMod.NONE, resultEnum, element.label);
            delegateMethods.add(element.label);
        }
        JSwitch _switch = body._switch(JExpr.invoke(delegate, element.label));
        for (ModelConnection connection : element.connections) {
            JBlock caseBody = _switch._case(JExpr.ref(connection.label)).body();
            traverseBranch(caseBody, connection.target);
            caseBody._break();
        }
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
