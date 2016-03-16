package pl.dpawlak.flocoge.generator;

import java.io.File;
import java.util.List;

import pl.dpawlak.flocoge.config.Configuration;

public interface CodeModel {

    void init(Configuration configuration, boolean externalCallsPresent) throws CodeGenerationException;
    CodeBlock publicMethod(String name);
    CodeBlock privateMethod(String name);
    CodeBlock publicExternalMethod(String name);
    void addMethod(String name);
    void addBooleanMethod(String name);
    void addEnumMethod(String name, List<String> values) throws CodeGenerationException;
    void addExternalMethod(String name);
    void build(File srcFolder) throws CodeGenerationException;
}