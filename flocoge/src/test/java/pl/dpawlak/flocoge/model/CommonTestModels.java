package pl.dpawlak.flocoge.model;

import java.util.Collection;

import pl.dpawlak.flocoge.model.ModelElement.Shape;
import pl.dpawlak.flocoge.model.util.ModelBuilder;

/**
 * Created by dpawlak on Feb 25, 2015
 */
public class CommonTestModels {
    
    private static final String INVALID_LABEL = "?";
    
    public static Collection<ModelElement> createTestFileModel() {
        return new ModelBuilder()
            .startPath(Shape.ON_PAGE_REF, "handle error<br>")
                .connectElement(Shape.OPERATION, "show error<br>mesage<br>")
                .connectElement(Shape.SKIP, "end")
            .startPath(Shape.EVENT, "user action<br>")
                .connectElement(Shape.OPERATION, "perform<br>defined<br>action<br>")
                .connectElement(Shape.DECISION, "which<br>user type?<br>")
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process admin<br>request<br>", "admin")
                        .end()
            .startPath(Shape.SKIP, "start")
                .connectElement(Shape.EVENT, "input<br>available<br>")
                .connectElement(Shape.OPERATION, "perform action<br>")
                .connectElement(Shape.DECISION, "is data valid?<br>")
                    .branch()
                        .connectElement(Shape.OPERATION, "prepare data<br>for storage<br>", "Y")
                        .connectElement(Shape.OPERATION, "save in<br>storage<br>")
                        .connectElement(Shape.SKIP, "end")
                        .end()
                    .branch()
                        .connectElement(Shape.ON_PAGE_REF, "handle error<br>", "N")
                        .end()
            .build();
    }
    
    public static Collection<ModelElement> createTestFileModelTransformed() {
        return new ModelBuilder()
            .startPath(Shape.ON_PAGE_REF, "handleError")
                .connectElement(Shape.OPERATION, "showErrorMesage")
            .startPath(Shape.EVENT, "userAction")
                .connectElement(Shape.OPERATION, "performDefinedAction")
                .connectElement(Shape.DECISION, "whichUserType")
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "processNormalUserRequest", "NORMAL")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "processVipRequest", "VIP")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "processAdminRequest", "ADMIN")
                        .end()
            .startPath(Shape.EVENT, "inputAvailable")
                .connectElement(Shape.OPERATION, "performAction")
                .connectElement(Shape.DECISION, "isDataValid")
                    .branch()
                        .connectElement(Shape.OPERATION, "prepareDataForStorage", "true")
                        .connectElement(Shape.OPERATION, "saveInStorage")
                        .end()
                    .branch()
                        .connectElement(Shape.ON_PAGE_REF, "handleError", "false")
                        .end()
            .build();
    }
    
    public static Collection<ModelElement> createModelWithLoop() {
        ModelBuilder builder = new ModelBuilder()
            .startPath(Shape.ON_PAGE_REF, "handle error<br>");
        ModelElement head = builder.getLastElement();
        return builder
            .connectElement(Shape.OPERATION, "show error<br>mesage<br>")
            .connectElement(head)
            .build();
    }
    
    public static Collection<ModelElement> createModelWithInvalidBranches() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "user action<br>")
                .connectElement(Shape.OPERATION, "perform<br>defined<br>action<br>")
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                        .end()
            .build();
    }
    
    public static Collection<ModelElement> createModelWithInvalidDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "user action<br>")
                .connectElement(Shape.DECISION, "perform<br>defined<br>action<br>")
                .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
            .build();
    }
    
    public static Collection<ModelElement> createModelWithInvalidElementLabel() {
        return new ModelBuilder().startPath(Shape.ON_PAGE_REF, INVALID_LABEL).build();
    }

    public static Collection<ModelElement> createModelWithInvalidDecisionLabel() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, INVALID_LABEL)
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                    .end()
            .build();
    }

    public static Collection<ModelElement> createModelWithInvalidDecisionBranchLabel() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "isOk")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", INVALID_LABEL)
                    .end()
            .build();
    }

    public static Collection<ModelElement> createModelWithBooleanDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "isOk")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "y")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", "no")
                    .end()
            .build();
    }

    public static Collection<ModelElement> createTransformedModelWithBooleanDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "isOk")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "processNormalUserRequest", "true")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "processVipRequest", "false")
                    .end()
            .build();
    }

    public static Collection<ModelElement> createModelWithInvertedBooleanDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "isOk")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "false")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", "true")
                    .end()
            .build();
    }

    public static Collection<ModelElement> createTransformedModelWithInvertedBooleanDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "isOk")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "processNormalUserRequest", "false")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "processVipRequest", "true")
                    .end()
            .build();
    }

}
