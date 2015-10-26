package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.dpawlak.flocoge.log.TraceLogger;
import pl.dpawlak.flocoge.model.CommonTestModels;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class ModelValidatorTest {

    @Test
    public void testFileModelValidation() {
        FlocogeModel model = CommonTestModels.createTestFileModel();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertTrue(inspector.inspect(model));
    }

    @Test
    public void testModelWithLoopsValidation() {
        FlocogeModel model = CommonTestModels.createModelWithLoop();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertFalse(inspector.inspect(model));
        assertTrue(inspector.getError().contains("is part of a loop"));
    }

    @Test
    public void testInvalidBranchesValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidBranches();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertFalse(inspector.inspect(model));
        assertTrue(inspector.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidDecisionBranches();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertFalse(inspector.inspect(model));
        assertTrue(inspector.getError().contains("does not have enough branches"));
    }

    @Test
    public void testInvalidElementLabelValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidElementLabel();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertFalse(inspector.inspect(model));
        assertTrue(inspector.getError().contains("(element has invalid label"));
    }

    @Test
    public void testInvalidDecisionBranchLabelValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidDecisionBranchLabel();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertFalse(inspector.inspect(model));
        assertTrue(inspector.getError().contains("branch has invalid label"));
    }

    @Test
    public void testComplexModelValidation() {
        FlocogeModel model = CommonTestModels.createComplexModel();
        ModelInspector inspector = new ModelInspector(new TraceLogger(null));
        assertTrue(inspector.inspect(model));
    }
}
