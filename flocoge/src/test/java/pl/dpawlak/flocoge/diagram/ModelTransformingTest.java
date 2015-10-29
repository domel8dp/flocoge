package pl.dpawlak.flocoge.diagram;

import static org.mockito.Mockito.mock;

import static pl.dpawlak.flocoge.model.CommonTestModels.*;

import org.junit.Test;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

public class ModelTransformingTest {

    @Test
    public void testFileModelTransforming() {
        transformAndValidate(createTestFileModel(), createTestFileModelTransformed());
    }

    @Test
    public void testBooleanDecisionBranchesTransforming() {
        transformAndValidate(createModelWithBooleanDecisionBranches(),
            createTransformedModelWithBooleanDecisionBranches());
    }

    @Test
    public void testInvertedBooleanDecisionBranchesTransforming() {
        transformAndValidate(createModelWithInvertedBooleanDecisionBranches(),
            createTransformedModelWithInvertedBooleanDecisionBranches());
    }

    @Test
    public void testOnPageRefsMovingToBack() {
        transformAndValidate(createModelWithOnPageRefs(), createTransformedModelWithOnPageRefs());
    }

    @Test
    public void testComplexModelTransforming() {
        transformAndValidate(createComplexModel(), createTransformedComplexModel());
    }

    private void transformAndValidate(FlocogeModel model, FlocogeModel expectedModel) {
        new ModelInspector(mock(Logger.class)).inspect(model);
        new ModelsMatchingValidator(model, expectedModel).validate();
    }
}
