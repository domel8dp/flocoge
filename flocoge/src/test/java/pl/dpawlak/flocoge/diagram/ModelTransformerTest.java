package pl.dpawlak.flocoge.diagram;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import pl.dpawlak.flocoge.diagram.ModelTransformer;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.CommonTestModels;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

public class ModelTransformerTest {

    @Test
    public void testFileModelTransforming() {
        FlocogeModel model = CommonTestModels.createTestFileModel();
        FlocogeModel expectedModel = CommonTestModels.createTestFileModelTransformed();
        transformAndValidate(model, expectedModel);
    }

    @Test
    public void testBooleanDecisionBranchesTransforming() {
        FlocogeModel model = CommonTestModels.createModelWithBooleanDecisionBranches();
        FlocogeModel expectedModel = CommonTestModels.createTransformedModelWithBooleanDecisionBranches();
        transformAndValidate(model, expectedModel);
    }

    @Test
    public void testInvertedBooleanDecisionBranchesTransforming() {
        FlocogeModel model = CommonTestModels.createModelWithInvertedBooleanDecisionBranches();
        FlocogeModel expectedModel = CommonTestModels.createTransformedModelWithInvertedBooleanDecisionBranches();
        transformAndValidate(model, expectedModel);
    }

    @Test
    public void testOnPageRefsMovingToBack() {
        FlocogeModel model = CommonTestModels.createModelWithOnPageRefs();
        FlocogeModel expectedModel = CommonTestModels.createTransformedModelWithOnPageRefs();
        transformAndValidate(model, expectedModel);
    }

    @Test
    public void testComplexModelTransforming() {
        FlocogeModel model = CommonTestModels.createComplexModel();
        FlocogeModel expectedModel = CommonTestModels.createTransformedComplexModel();
        transformAndValidate(model, expectedModel);
    }

    private void transformAndValidate(FlocogeModel model, FlocogeModel expectedModel) {
        new ModelTransformer(mock(Logger.class)).transform(model);
        new ModelsMatchingValidator(model, expectedModel).validate();
    }
}
