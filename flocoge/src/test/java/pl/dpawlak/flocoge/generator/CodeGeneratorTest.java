package pl.dpawlak.flocoge.generator;

import static org.mockito.Mockito.mock;
import static pl.dpawlak.flocoge.model.TestModels.*;
import static pl.dpawlak.flocoge.generator.TestCodeModels.*;

import org.junit.Test;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.generator.util.TestCodeModel;
import pl.dpawlak.flocoge.generator.util.TestCodeModelsMatchingValidator;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class CodeGeneratorTest {

    @Test
    public void testFileCodeModelGeneration() throws CodeGenerationException {
        generateAndValidate(createTestFileModelTransformed(), createTestFileCodeModel());
    }

    @Test
    public void testComplexCodeModelGeneration() throws CodeGenerationException {
        generateAndValidate(createTransformedComplexModel(), createComplexCodeModel());
    }

    @Test
    public void testAdditionalCodeModelGeneration() throws CodeGenerationException {
        generateAndValidate(createTransformedAdditionalModel(), createAdditionalCodeModel());
    }

    @Test
    public void testGenerationWithEmptyIf() throws CodeGenerationException {
        generateAndValidate(createTransformedModelWithEmptyIf(), createCodeModelWithEmptyIf());
    }

    @Test
    public void testGenerationWithEmptyElse() throws CodeGenerationException {
        generateAndValidate(createTransformedModelWithEmptyElse(), createCodeModelWithEmptyElse());
    }

    @Test
    public void testGenerationWithEmptyIfAndElse() throws CodeGenerationException {
        generateAndValidate(createTransformedModelWithEmptyIfAndElse(), createCodeModelWithEmptyIfAndElse());
    }

    private void generateAndValidate(FlocogeModel model, TestCodeModel expectedCodeModel)
            throws CodeGenerationException {
        TestCodeModel codeModel = new TestCodeModel();
        new CodeGenerator(mock(Configuration.class), mock(Logger.class), codeModel).generate(model);
        new TestCodeModelsMatchingValidator(expectedCodeModel, codeModel).validate();
    }
}
