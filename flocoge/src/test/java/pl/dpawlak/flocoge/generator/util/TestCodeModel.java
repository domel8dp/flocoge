package pl.dpawlak.flocoge.generator.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.dpawlak.flocoge.generator.CodeBlock;
import pl.dpawlak.flocoge.generator.CodeGenerationException;
import pl.dpawlak.flocoge.generator.CodeModel;

public class TestCodeModel implements CodeModel {

    public boolean externalCallsPresent;
    public final List<Call> pathMethods = new LinkedList<>();
    public final Map<String, TestCodeBlock> pathMethodBlocks = new HashMap<>();
    public final Set<String> delegateMethods = new HashSet<>();
    public final Set<String> delegateBooleanMethods = new HashSet<>();
    public final Map<String, List<String>> delegateEnumMethods = new LinkedHashMap<>();
    public final Set<String> externalMethods = new HashSet<>();

    @Override
    public void init(String packageName, String baseName, boolean externalCallsPresent) throws CodeGenerationException {
        this.externalCallsPresent = externalCallsPresent;
    }

    @Override
    public CodeBlock publicMethod(String name) {
        TestCodeBlock block = new TestCodeBlock();
        pathMethods.add(new Call(name, Call.Type.DELEGATE));
        pathMethodBlocks.put(name, block);
        return block;
    }

    @Override
    public CodeBlock privateMethod(String name) {
        TestCodeBlock block = new TestCodeBlock();
        pathMethods.add(new Call(name, Call.Type.LOCAL));
        pathMethodBlocks.put(name, block);
        return block;
    }

    @Override
    public CodeBlock publicExternalMethod(String name) {
        TestCodeBlock block = new TestCodeBlock();
        pathMethods.add(new Call(name, Call.Type.EXTERNAL));
        pathMethodBlocks.put(name, block);
        return block;
    }

    @Override
    public void addMethod(String name) {
        delegateMethods.add(name);
    }

    @Override
    public void addBooleanMethod(String name) {
        delegateBooleanMethods.add(name);
    }

    @Override
    public void addEnumMethod(String name, List<String> values) throws CodeGenerationException {
        delegateEnumMethods.put(name, values);
    }

    @Override
    public void addExternalMethod(String name) {
        externalMethods.add(name);
    }

    @Override
    public void build(File srcFolder) throws CodeGenerationException { }
}
