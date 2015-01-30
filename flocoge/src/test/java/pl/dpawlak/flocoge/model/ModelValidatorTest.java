package pl.dpawlak.flocoge.model;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import pl.dpawlak.flocoge.model.ModelElement.Shape;
import pl.dpawlak.flocoge.model.util.ModelBuilder;

/**
 * Created by dpawlak on Jan 21, 2015
 */
public class ModelValidatorTest {
    
    @Test
    public void testComplexModelValidation() {
        Map<String, ModelElement> startElements = createComplexModel();
        ModelValidator validator = new ModelValidator(startElements);
        assertTrue(validator.validate());
    }

    @Test
    public void testModelWithLoopsValidation() {
        Map<String, ModelElement> startElements = createModelWithLoop();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
    }

    @Test
    public void testInvalidBranchesValidation() {
        Map<String, ModelElement> startElements = createModelWithInvalidBranches();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
    }

    @Test
    public void testInvalidDecisionBranchesValidation() {
        Map<String, ModelElement> startElements = createModelWithInvalidDecisionBranches();
        ModelValidator validator = new ModelValidator(startElements);
        assertFalse(validator.validate());
    }

    private Map<String, ModelElement> createComplexModel() {
        int i = 0;
        return new ModelBuilder()
            .startPath(String.valueOf(++i), Shape.ON_PAGE_REF, "handle error<br>")
                .connectElement(String.valueOf(++i), Shape.OPERATION, "show error<br>mesage<br>")
            .startPath(String.valueOf(++i), Shape.EVENT, "user action<br>")
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

}
