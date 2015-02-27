package pl.dpawlak.flocoge.model;

import java.util.Collection;

import org.junit.Test;

import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

/**
 * Created by dpawlak on Feb 25, 2015
 */
public class ModelTransformerTest {
    
    @Test
    public void testFileModelTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createTestFileModel();
        Collection<ModelElement> expectedElements = CommonTestModels.createTestFileModelTransformed();
        ModelTransformer transformer = new ModelTransformer(startElements);
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testBooleanDecisionBranchesTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithBooleanDecisionBranches();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedModelWithBooleanDecisionBranches();
        ModelTransformer transformer = new ModelTransformer(startElements);
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testInvertedBooleanDecisionBranchesTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvertedBooleanDecisionBranches();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedModelWithInvertedBooleanDecisionBranches();
        ModelTransformer transformer = new ModelTransformer(startElements);
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testComplexModelTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createComplexModel();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedComplexModel();
        ModelTransformer transformer = new ModelTransformer(startElements);
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
}
