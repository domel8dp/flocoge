package pl.dpawlak.flocoge.model;

import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.junit.Test;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

/**
 * Created by dpawlak on Feb 25, 2015
 */
public class ModelTransformerTest {
    
    @Test
    public void testFileModelTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createTestFileModel();
        Collection<ModelElement> expectedElements = CommonTestModels.createTestFileModelTransformed();
        ModelTransformer transformer = new ModelTransformer(startElements, mock(Logger.class));
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testBooleanDecisionBranchesTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithBooleanDecisionBranches();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedModelWithBooleanDecisionBranches();
        ModelTransformer transformer = new ModelTransformer(startElements, mock(Logger.class));
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testInvertedBooleanDecisionBranchesTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithInvertedBooleanDecisionBranches();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedModelWithInvertedBooleanDecisionBranches();
        ModelTransformer transformer = new ModelTransformer(startElements, mock(Logger.class));
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testOnPageRefsMovingToBack() {
        Collection<ModelElement> startElements = CommonTestModels.createModelWithOnPageRefs();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedModelWithOnPageRefs();
        ModelTransformer transformer = new ModelTransformer(startElements, mock(Logger.class));
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
    
    @Test
    public void testComplexModelTransforming() {
        Collection<ModelElement> startElements = CommonTestModels.createComplexModel();
        Collection<ModelElement> expectedElements = CommonTestModels.createTransformedComplexModel();
        ModelTransformer transformer = new ModelTransformer(startElements, mock(Logger.class));
        new ModelsMatchingValidator(transformer.transform(), expectedElements).validate();
    }
}
