package pl.dpawlak.flocoge.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.model.ModelElement;

/**
 * Created by dpawlak on Mar 1, 2015
 */
public class CodeGenerator {

    private final Collection<ModelElement> model;
    private final Configuration config;
    private final JCodeModel codeModel;
    
    private JDefinedClass interfaceClass;
    private JDefinedClass facadeClass;
    private JDefinedClass externalClass;
    private JFieldVar delegate;
    private JFieldVar externalDelegate;
    
    public CodeGenerator(Collection<ModelElement> model, Configuration config) {
        this.model = model;
        this.config = config;
        codeModel = new JCodeModel();
    }
    
    public void generate() throws CodeGenerationException {
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

    private void fillEntities() {
    }

    private void build() throws CodeGenerationException {
        try {
            codeModel.build(config.srcFolder, new PrintStream(new NullOutputStream()));
        } catch (IOException ioException) {
            throw new CodeGenerationException(ioException);
        }
    }
    
    private static class NullOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException { }
        
        @Override
        public void write(byte[] a) { }
        
        @Override
        public void write(byte[] a, int b, int c) { }
    }
}
