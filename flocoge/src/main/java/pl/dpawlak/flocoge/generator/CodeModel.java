package pl.dpawlak.flocoge.generator;

import java.io.File;
import java.util.List;

public interface CodeModel {

    void init(String packageName, String baseName, boolean externalCallsPresent) throws CodeGenerationException;
    CodeBlock publicMethod(String name);
    CodeBlock privateMethod(String name);
    CodeBlock publicExternalMethod(String name);
    void addMethod(String name);
    void addBooleanMethod(String name);
    void addEnumMethod(String name, List<String> values) throws CodeGenerationException;
    void addExternalMethod(String name);
    void build(File srcFolder) throws CodeGenerationException;
}