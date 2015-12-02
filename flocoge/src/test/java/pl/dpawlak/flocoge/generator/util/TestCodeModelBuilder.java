package pl.dpawlak.flocoge.generator.util;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import pl.dpawlak.flocoge.generator.CodeBlock;
import pl.dpawlak.flocoge.generator.CodeGenerationException;

public class TestCodeModelBuilder {

    private final TestCodeModel model;
    private final Deque<CodeBlock> branchStack;

    public TestCodeModelBuilder() {
        model = new TestCodeModel();
        branchStack = new LinkedList<>();
    }

    public TestCodeModel build() {
        return model;
    }

    public TestCodeModelBuilder startPath(String name) {
        branchStack.clear();
        branchStack.addLast(model.publicMethod(name));
        return this;
    }

    public TestCodeModelBuilder startPrivatePath(String name) {
        branchStack.clear();
        branchStack.addLast(model.privateMethod(name));
        return this;
    }

    public TestCodeModelBuilder startExternalPath(String name) {
        branchStack.clear();
        branchStack.addLast(model.publicExternalMethod(name));
        return this;
    }

    public TestCodeModelBuilder callDelegate(String name) {
        model.addMethod(name);
        branchStack.getLast().callDelegate(name);
        return this;
    }

    public TestCodeModelBuilder callLocal(String name) {
        branchStack.getLast().call(name);
        return this;
    }

    public TestCodeModelBuilder callExternal(String name) {
        model.externalCallsPresent = true;
        model.addExternalMethod(name);
        branchStack.getLast().callExternal(name);
        return this;
    }

    public TestCodeModelBuilder callIf(String name) {
        model.addBooleanMethod(name);
        branchStack.getLast()._if(name);
        return this;
    }

    public TestCodeModelBuilder callReturn() {
        branchStack.getLast()._return();
        return this;
    }

    public TestCodeModelBuilder callBreak() {
        branchStack.getLast()._break();
        return this;
    }

    public TestCodeModelBuilder beginThen() {
        TestCodeBlock cb = (TestCodeBlock)branchStack.getLast();
        String ifName = cb.calls.get(cb.calls.size() - 1).name;
        branchStack.addLast(cb.ifs.get(ifName)._then());
        return this;
    }

    public TestCodeModelBuilder beginElse() {
        TestCodeBlock cb = (TestCodeBlock)branchStack.getLast();
        String ifName = cb.calls.get(cb.calls.size() - 1).name;
        branchStack.addLast(cb.ifs.get(ifName)._else());
        return this;
    }

    public TestCodeModelBuilder callSwitch(String name, String... branchNames) throws CodeGenerationException {
        model.addEnumMethod(name, Arrays.asList(branchNames));
        branchStack.getLast()._switch(name);
        return this;
    }

    public TestCodeModelBuilder beginCase(String name) {
        TestCodeBlock cb = (TestCodeBlock)branchStack.getLast();
        String switchName = cb.calls.get(cb.calls.size() - 1).name;
        branchStack.addLast(cb.switches.get(switchName)._case(name));
        return this;
    }

    public TestCodeModelBuilder end() {
        branchStack.removeLast();
        return this;
    }
}
