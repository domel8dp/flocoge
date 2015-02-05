package pl.dpawlak.flocoge.model;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import pl.dpawlak.flocoge.model.ModelElement.Shape;
import pl.dpawlak.flocoge.model.util.ModelBuilder;
import pl.dpawlak.flocoge.model.util.ModelsMatchingValidator;

/**
 * Created by dpawlak on Jan 21, 2015
 */
public class ModelValidatorTest {
    
    private static final String ID = "0";
    private static final String INVALID_LABEL = "?";
    
    @Test
    public void testComplexModelValidation() {
        Map<String, ModelElement> startElements = createComplexModel();
        ModelValidator validator = new ModelValidator(startElements);
        assertTrue(validator.validate());
        
        Map<String, ModelElement> expectedElements = createComplexModelValidated();
        new ModelsMatchingValidator(startElements.values(), expectedElements.values()).validate();
    }

    @Test
    public void testModelWithLoopsValidation() {
        Map<String, ModelElement> startElements = createModelWithLoop();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("is part of a loop"));
    }

    @Test
    public void testInvalidBranchesValidation() {
        Map<String, ModelElement> startElements = createModelWithInvalidBranches();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("has more than one branch"));
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        Map<String, ModelElement> startElements = createModelWithInvalidDecisionBranches();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("does not have enough branches"));
    }

    @Test
    public void testInvalidElementLabelValidation() {
        Map<String, ModelElement> startElements = new ModelBuilder().startPath(ID, Shape.ON_PAGE_REF, INVALID_LABEL).build();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("(element has invalid label"));
    }

    @Test
    public void testInvalidDecisionLabelValidation() {
        Map<String, ModelElement> startElements = createModelWithInvalidDecisionLabel();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("(decision element has invalid label"));
    }

    @Test
    public void testInvalidDecisionBranchLabelValidation() {
        Map<String, ModelElement> startElements = createModelWithInvalidDecisionBranchLabel();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
        assertTrue(validator.getError().contains("branch has invalid label"));
    }
    
    @Test
    public void testBooleanDecisionBranches() {
        Map<String, ModelElement> startElements = createModelWithBooleanDecisionBranches();
        ModelValidator validator = new ModelValidator(startElements);
        assertTrue(validator.validate());
        
        Map<String, ModelElement> expectedElements = createValidatedModelWithBooleanDecisionBranches();
        new ModelsMatchingValidator(startElements.values(), expectedElements.values()).validate();
    }
    
    @Test
    public void testInvertedBooleanDecisionBranches() {
        Map<String, ModelElement> startElements = createModelWithInvertedBooleanDecisionBranches();
        ModelValidator validator = new ModelValidator(startElements);
        assertTrue(validator.validate());
        
        Map<String, ModelElement> expectedElements = createValidatedModelWithInvertedBooleanDecisionBranches();
        new ModelsMatchingValidator(startElements.values(), expectedElements.values()).validate();
    }

    private Map<String, ModelElement> createComplexModel() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.ON_PAGE_REF, "handle error<br>")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "show error<br>mesage<br>")
            .startPath(String.valueOf(++i), Shape.SKIP, "start")
                .connectElement(String.valueOf(++i), Shape.EVENT, "user action<br>")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "perform<br>defined<br>action<br>")
                .connectElement(String.valueOf(++i), Shape.DECISION, "which<br>user type?<br>")
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process admin<br>request<br>", "admin")
                        .end()
            .startPath(String.valueOf(++i), Shape.EVENT, "input<br>available<br>")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "perform action<br>")
                .connectElement(String.valueOf(++i), Shape.DECISION, "is data valid?<br>")
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OPERATION, "prepare data<br>for storage<br>", "Y")
                        .connectElement(String.valueOf(++i), Shape.OPERATION, "save in<br>storage<br>")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.ON_PAGE_REF, "handle error<br>", "N")
                        .end()
            .build();
    }
    
    private Map<String, ModelElement> createComplexModelValidated() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.ON_PAGE_REF, "handleError")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "showErrorMesage")
            .startPath(String.valueOf(++i), Shape.SKIP, "start")
                .connectElement(String.valueOf(++i), Shape.EVENT, "userAction")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "performDefinedAction")
                .connectElement(String.valueOf(++i), Shape.DECISION, "whichUserType")
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processNormalUserRequest", "NORMAL")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processVipRequest", "VIP")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processAdminRequest", "ADMIN")
                        .end()
            .startPath(String.valueOf(++i), Shape.EVENT, "inputAvailable")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "performAction")
                .connectElement(String.valueOf(++i), Shape.DECISION, "isDataValid")
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OPERATION, "prepareDataForStorage", "true")
                        .connectElement(String.valueOf(++i), Shape.OPERATION, "saveInStorage")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.ON_PAGE_REF, "handleError", "false")
                        .end()
            .build();
    }
    
    private Map<String, ModelElement> createModelWithLoop() {
        int i = 0;
        ModelBuilder builder = new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.ON_PAGE_REF, "handle error<br>");
        ModelElement head = builder.getLastElement();
        return builder
            .connectElement(String.valueOf(++i), Shape.OPERATION, "show error<br>mesage<br>")
            .connectElement(head)
            .build();
    }
    
    private Map<String, ModelElement> createModelWithInvalidBranches() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.EVENT, "user action<br>")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "perform<br>defined<br>action<br>")
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                        .end()
                    .branch()
                        .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                        .end()
            .build();
    }
    
    private Map<String, ModelElement> createModelWithInvalidDecisionBranches() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.EVENT, "user action<br>")
                .connectElement(String.valueOf(++i), Shape.DECISION, "perform<br>defined<br>action<br>")
                .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
            .build();
    }

    private Map<String, ModelElement> createModelWithInvalidDecisionLabel() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.DECISION, INVALID_LABEL)
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                    .end()
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                    .end()
            .build();
    }

    private Map<String, ModelElement> createModelWithInvalidDecisionBranchLabel() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.DECISION, "isOk")
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                    .end()
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process vip<br>request<br>", INVALID_LABEL)
                    .end()
            .build();
    }

    private Map<String, ModelElement> createModelWithBooleanDecisionBranches() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.DECISION, "isOk")
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "y")
                    .end()
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process vip<br>request<br>", "no")
                    .end()
            .build();
    }

    private Map<String, ModelElement> createValidatedModelWithBooleanDecisionBranches() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.DECISION, "isOk")
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processNormalUserRequest", "true")
                    .end()
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processVipRequest", "false")
                    .end()
            .build();
    }

    private Map<String, ModelElement> createModelWithInvertedBooleanDecisionBranches() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.DECISION, "isOk")
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "false")
                    .end()
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "process vip<br>request<br>", "true")
                    .end()
            .build();
    }

    private Map<String, ModelElement> createValidatedModelWithInvertedBooleanDecisionBranches() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.DECISION, "isOk")
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processNormalUserRequest", "false")
                    .end()
                .branch()
                    .connectElement(String.valueOf(++i), Shape.OFF_PAGE_REF, "processVipRequest", "true")
                    .end()
            .build();
    }
}
