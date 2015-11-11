package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.dpawlak.flocoge.log.util.ErrorCollectingLogger;
import pl.dpawlak.flocoge.model.TestModels;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.InvalidTestModels;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelValidationTest {

    private FlocogeModel model;
    private ModelInspector inspector;
    private ErrorCollectingLogger logger;

    @Test
    public void testFileModelValidation() {
        initInspector(TestModels.createTestFileModel());
        assertTrue(inspector.inspect(model));
    }

    @Test
    public void testModelWithLoopsValidation() {
        initInspector(InvalidTestModels.createModelWithLoop());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("is part of a loop"));
    }

    @Test
    public void testInvalidBranchesValidation() {
        initInspector(InvalidTestModels.createModelWithInvalidBranches(Shape.OPERATION));
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidSkipBranchesValidation() {
        initInspector(InvalidTestModels.createModelWithInvalidBranches(Shape.SKIP));
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        initInspector(InvalidTestModels.createModelWithInvalidDecisionBranches());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("does not have enough branches"));
    }

    @Test
    public void testNoElementLabelValidation() {
        initInspector(InvalidTestModels.createModelWithoutElementLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("(element has invalid label"));
    }

    @Test
    public void testEmptyElementLabelValidation() {
        initInspector(InvalidTestModels.createModelWithEmptyElementLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("(element has invalid label"));
    }

    @Test
    public void testNoStartLabelValidation() {
        initInspector(TestModels.createModelWithoutStartLabel());
        assertTrue(inspector.inspect(model));
    }

    @Test
    public void testEmptyStartLabelValidation() {
        initInspector(TestModels.createModelWithEmptyStartLabel());
        assertTrue(inspector.inspect(model));
    }

    @Test
    public void testInvalidElementLabelValidation() {
        initInspector(InvalidTestModels.createModelWithInvalidElementLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("(element has invalid label"));
    }

    @Test
    public void testInvalidDecisionBranchLabelValidation() {
        initInspector(InvalidTestModels.createModelWithInvalidDecisionBranchLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("branch has invalid label"));
    }

    @Test
    public void testNonUniqueShapesValidation() {
        initInspector(InvalidTestModels.createModelWithNonUniqueShapes());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("have multiple shapes"));
    }

    @Test
    public void testNonUniqueBranchesValidation() {
        initInspector(InvalidTestModels.createModelWithNonUniqueDecisionBranches());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("have different branches"));
    }

    @Test
    public void testComplexModelValidation() {
        initInspector(TestModels.createComplexModel());
        assertTrue(inspector.inspect(model));
    }

    @Test
    public void testNoPathLabelValidation() {
        initInspector(InvalidTestModels.createModelWithoutValidPathLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("(path without valid start element)"));
    }

    @Test
    public void testNonUniquePathNames() {
        initInspector(InvalidTestModels.createModelWithNonUniquePathNames());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("(multiple paths have the same name"));
    }

    @Test
    public void testMissingInternalCall() {
        initInspector(InvalidTestModels.createModelWithMissingInternalCall());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("references missing path)"));
    }

    private void initInspector(FlocogeModel model) {
        this.model = model;
        logger = new ErrorCollectingLogger();
        inspector = new ModelInspector(logger);
    }
}
