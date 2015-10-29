package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.dpawlak.flocoge.log.util.ErrorCollectingLogger;
import pl.dpawlak.flocoge.model.CommonTestModels;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelValidationTest {

    private FlocogeModel model;
    private ModelInspector inspector;
    private ErrorCollectingLogger logger;

    @Test
    public void testFileModelValidation() {
        initInspector(CommonTestModels.createTestFileModel());
        assertTrue(inspector.inspect(model));
    }

    @Test
    public void testModelWithLoopsValidation() {
        initInspector(CommonTestModels.createModelWithLoop());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("is part of a loop"));
    }

    @Test
    public void testInvalidBranchesValidation() {
        initInspector(CommonTestModels.createModelWithInvalidBranches(Shape.OPERATION));
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidSkipBranchesValidation() {
        initInspector(CommonTestModels.createModelWithInvalidBranches(Shape.SKIP));
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        initInspector(CommonTestModels.createModelWithInvalidDecisionBranches());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("does not have enough branches"));
    }

    @Test
    public void testInvalidElementLabelValidation() {
        initInspector(CommonTestModels.createModelWithInvalidElementLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("(element has invalid label"));
    }

    @Test
    public void testInvalidDecisionBranchLabelValidation() {
        initInspector(CommonTestModels.createModelWithInvalidDecisionBranchLabel());
        assertFalse(inspector.inspect(model));
        assertTrue(logger.getError().contains("branch has invalid label"));
    }

    @Test
    public void testComplexModelValidation() {
        initInspector(CommonTestModels.createComplexModel());
        assertTrue(inspector.inspect(model));
    }

    private void initInspector(FlocogeModel model) {
        this.model = model;
        logger = new ErrorCollectingLogger();
        inspector = new ModelInspector(logger);
    }
}
