package pl.dpawlak.flocoge.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Test;

import pl.dpawlak.flocoge.log.Logger;

/**
 * Created by dpawlak on Jan 21, 2015
 */
public class ModelValidatorTest {
    
    @Test
    public void testFileModelValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createTestFileModel();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertTrue(validator.validate());
    }

    @Test
    public void testModelWithLoopsValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithLoop();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("is part of a loop"));
    }

    @Test
    public void testInvalidBranchesValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvalidBranches();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvalidDecisionBranches();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("does not have enough branches"));
    }

    @Test
    public void testInvalidElementLabelValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvalidElementLabel();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("(element has invalid label"));
    }

    @Test
    public void testInvalidDecisionLabelValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvalidDecisionLabel();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("(decision element has invalid label"));
    }

    @Test
    public void testInvalidDecisionBranchLabelValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvalidDecisionBranchLabel();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("branch has invalid label"));
    }
    
    @Test
    public void testComplexModelValidation() {
        Collection<ModelElement> startElements = CommonTestModels.createComplexModel();
        ModelValidator validator = new ModelValidator(startElements, mock(Logger.class));
        assertTrue(validator.validate());
    }
}
