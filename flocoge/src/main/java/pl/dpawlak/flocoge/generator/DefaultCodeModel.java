package pl.dpawlak.flocoge.generator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
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

import pl.dpawlak.flocoge.diagram.ModelNamesUtils;

class DefaultCodeModel implements CodeModel {

    private final JCodeModel codeModel;
    private final Set<String> delegateMethods;
    private final Set<String> externalDelegateMethods;

    private String baseName;
    private JDefinedClass interfaceClass;
    private JDefinedClass facadeClass;
    private JDefinedClass externalClass;
    private JFieldVar delegate;
    private JFieldVar externalDelegate;

    public DefaultCodeModel() {
        codeModel = new JCodeModel();
        delegateMethods = new HashSet<>();
        externalDelegateMethods = new HashSet<>();
    }

    @Override
    public void init(String packageName, String baseName, boolean externalCallsPresent) throws CodeGenerationException {
        this.baseName = baseName;
        prepareEmptyEntities(packageName, externalCallsPresent);
    }

    private void prepareEmptyEntities(String packageName, boolean externalCallsPresent) throws CodeGenerationException {
        try {
            interfaceClass = codeModel._class(packageName + "." + baseName, ClassType.INTERFACE);
            facadeClass = codeModel._class(packageName + "." + baseName + "Facade");
            if (externalCallsPresent) {
                externalClass = codeModel._class(packageName + "." + baseName + "External", ClassType.INTERFACE);
            }
            generateFacadeFieldsAndConstructor(externalCallsPresent);
        } catch (JClassAlreadyExistsException exception) {
            throw new CodeGenerationException(exception);
        }
    }

    private void generateFacadeFieldsAndConstructor(boolean externalCallsPresent) {
        String delegateName = new StringBuilder(baseName.length())
            .append(Character.toLowerCase(baseName.charAt(0)))
            .append(baseName, 1, baseName.length()).toString();
        JMethod constructor = facadeClass.constructor(JMod.PUBLIC);
        delegate = createAndAssignFacadeField(constructor, interfaceClass, delegateName);
        if (externalCallsPresent) {
            externalDelegate = createAndAssignFacadeField(constructor, externalClass, delegateName + "External");
        }
    }

    private JFieldVar createAndAssignFacadeField(JMethod constructor, JDefinedClass modelClass, String name) {
        JVar param = constructor.param(modelClass, name);
        constructor.body().assign(JExpr.refthis(name), param);
        return facadeClass.field(JMod.PRIVATE | JMod.FINAL, modelClass, name);
    }

    @Override
    public CodeBlock publicMethod(String name) {
        JMethod method = facadeClass.method(JMod.PUBLIC, codeModel.VOID, name);
        return new DefaultCodeBlock(method.body());
    }

    @Override
    public CodeBlock privateMethod(String name) {
        JMethod method = facadeClass.method(JMod.PRIVATE, codeModel.VOID, name);
        return new DefaultCodeBlock(method.body());
    }

    @Override
    public CodeBlock publicExternalMethod(String name) {
        JMethod method = facadeClass.method(JMod.PUBLIC, codeModel.VOID, name);
        return new DefaultCodeBlock(method.body());
    }

    @Override
    public void addMethod(String name) {
        if (!delegateMethods.contains(name)) {
            interfaceClass.method(JMod.NONE, codeModel.VOID, name);
            delegateMethods.add(name);
        }
    }

    @Override
    public void addBooleanMethod(String name) {
        if (!delegateMethods.contains(name)) {
            interfaceClass.method(JMod.NONE, codeModel.BOOLEAN, name);
            delegateMethods.add(name);
        }
    }

    @Override
    public void addEnumMethod(String name, List<String> values) throws CodeGenerationException {
        try {
            if (!delegateMethods.contains(name)) {
                JDefinedClass resultEnum = interfaceClass._enum(ModelNamesUtils.createEnumName(name));
                for (String value : values) {
                    resultEnum.enumConstant(value);
                }
                interfaceClass.method(JMod.NONE, resultEnum, name);
                delegateMethods.add(name);
            }
        } catch (JClassAlreadyExistsException exception) {
            throw new CodeGenerationException(exception);
        }
    }

    @Override
    public void addExternalMethod(String name) {
        if (!externalDelegateMethods.contains(name)) {
            externalClass.method(JMod.NONE, codeModel.VOID, name);
            externalDelegateMethods.add(name);
        }
    }

    @Override
    public void build(File srcFolder) throws CodeGenerationException {
        try {
            codeModel.build(new HeaderCommentFileCodeWriter(srcFolder));
        } catch (IOException ioException) {
            throw new CodeGenerationException(ioException);
        }
    }

    private class DefaultCodeBlock implements CodeBlock {

        private final JBlock block;

        public DefaultCodeBlock(JBlock block) {
            this.block = block;
        }

        @Override
        public void call(String name) {
            block.invoke(name);
        }

        @Override
        public void callDelegate(String name) {
            block.invoke(delegate, name);
        }

        @Override
        public void callExternal(String name) {
            block.invoke(externalDelegate, name);
        }

        @Override
        public CodeIf _if(String name) {
            return new DefaultCodeIf(block, name);
        }

        @Override
        public CodeSwitch _switch(String name) {
            return new DefaultCodeSwitch(block, name);
        }

        @Override
        public void _return() {
            block._return();
        }

        @Override
        public void _break() {
            block._break();
        }
    }

    private class DefaultCodeIf implements CodeIf {

        private final JConditional if1;

        public DefaultCodeIf(JBlock block, String name) {
            this.if1 = block._if(JExpr.invoke(delegate, name));
        }

        @Override
        public CodeBlock _then() {
            return new DefaultCodeBlock(if1._then());
        }

        @Override
        public CodeBlock _else() {
            return new DefaultCodeBlock(if1._else());
        }
    }

    private class DefaultCodeSwitch implements CodeSwitch {

        private final JSwitch _switch;

        public DefaultCodeSwitch(JBlock block, String name) {
            this._switch = block._switch(JExpr.invoke(delegate, name));
        }

        @Override
        public CodeBlock _case(String name) {
            return new DefaultCodeBlock(_switch._case(JExpr.ref(name)).body());
        }
    }
}