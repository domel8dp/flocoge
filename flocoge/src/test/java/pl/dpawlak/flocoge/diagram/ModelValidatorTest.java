package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import pl.dpawlak.flocoge.diagram.ModelValidator;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.CommonTestModels;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class ModelValidatorTest {

    @Test
    public void testFileModelValidation() {
        FlocogeModel model = CommonTestModels.createTestFileModel();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertTrue(validator.validate(model));
    }

    @Test
    public void testModelWithLoopsValidation() {
        FlocogeModel model = CommonTestModels.createModelWithLoop();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertFalse(validator.validate(model));
        assertTrue(validator.getError().contains("is part of a loop"));
    }

    @Test
    public void testInvalidBranchesValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidBranches();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertFalse(validator.validate(model));
        assertTrue(validator.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidDecisionBranches();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertFalse(validator.validate(model));
        assertTrue(validator.getError().contains("does not have enough branches"));
    }

    @Test
    public void testInvalidElementLabelValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidElementLabel();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertFalse(validator.validate(model));
        assertTrue(validator.getError().contains("(element has invalid label"));
    }

    @Test
    public void testInvalidDecisionLabelValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidDecisionLabel();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertFalse(validator.validate(model));
        assertTrue(validator.getError().contains("(decision element has invalid label"));
    }

    @Test
    public void testInvalidDecisionBranchLabelValidation() {
        FlocogeModel model = CommonTestModels.createModelWithInvalidDecisionBranchLabel();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertFalse(validator.validate(model));
        assertTrue(validator.getError().contains("branch has invalid label"));
    }

    @Test
    public void testComplexModelValidation() {
        FlocogeModel model = CommonTestModels.createComplexModel();
        ModelValidator validator = new ModelValidator(mock(Logger.class));
        assertTrue(validator.validate(model));
    }
}
