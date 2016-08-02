package pl.dpawlak.flocoge.model;

import pl.dpawlak.flocoge.model.ModelElement.Shape;
import pl.dpawlak.flocoge.model.util.ModelBuilder;

public class InvalidTestModels {

    private static final String INVALID_LABEL = "?";

    public static FlocogeModel createModelWithLoop() {
        return new ModelBuilder()
            .startPath(Shape.ON_PAGE_REF, "handle error<br>")
            .markBookmark("LOOP")
            .connectElement(Shape.OPERATION, "show error<br>mesage<br>")
            .connectBookmark("LOOP")
            .build();
    }

    public static FlocogeModel createModelWithInvalidBranches(ModelElement.Shape shape) {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "user action<br>")
                .connectElement(shape, "perform<br>defined<br>action<br>")
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process vip<br>request<br>", "vip")
                        .end()
            .build();
    }

    public static FlocogeModel createModelWithInvalidDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "user action<br>")
                .connectElement(Shape.DECISION, "perform<br>defined<br>action<br>")
                .connectElement(Shape.OFF_PAGE_REF, "process normal<br>user request<br>", "normal")
            .build();
    }

    public static FlocogeModel createModelWithoutElementLabel() {
        return new ModelBuilder().startPath(Shape.ON_PAGE_REF, null).build();
    }

    public static FlocogeModel createModelWithEmptyElementLabel() {
        return new ModelBuilder().startPath(Shape.ON_PAGE_REF, "").build();
    }

    public static FlocogeModel createModelWithInvalidElementLabel() {
        return new ModelBuilder().startPath(Shape.ON_PAGE_REF, INVALID_LABEL).build();
    }

    public static FlocogeModel createModelWithInvalidDecisionBranchLabel() {
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

    public static FlocogeModel createModelWithNonUniqueShapes() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "a label")
            .connectElement(Shape.ON_PAGE_REF, "a label")
            .build();
    }

    public static FlocogeModel createModelWithMismatchedDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "a label")
                .connectElement(Shape.DECISION, "a decision")
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process a", "a")
                        .end()
                    .branch()
                        .connectElement(Shape.OFF_PAGE_REF, "process b", "b")
                        .end()
            .startPath(Shape.DECISION, "a decision")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process c", "c")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process d", "d")
                    .end()
            .build();
    }

    public static FlocogeModel createModelWithNonUniqueDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "a decision")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process c", "y")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "process d", "y")
                    .end()
            .build();
    }

    public static FlocogeModel createModelWithoutValidPathLabel() {
        return new ModelBuilder()
            .startPath(Shape.START, "start")
                .connectElement(Shape.SKIP, "element")
                .connectElement(Shape.START, "")
                .connectElement(Shape.START, "start")
            .build();
    }

    public static FlocogeModel createModelWithNonUniquePathNames() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "a label")
                .connectElement(Shape.OFF_PAGE_REF, "process a")
            .startPath(Shape.EVENT, "a label")
                .connectElement(Shape.OFF_PAGE_REF, "process c")
            .build();
    }

    public static FlocogeModel createModelWithMissingInternalCall() {
        return new ModelBuilder()
            .startPath(Shape.EVENT, "event")
                .connectElement(Shape.ON_PAGE_REF, "call")
            .build();
    }

    public static FlocogeModel createModelWithInterleavingBranches() {
        return new ModelBuilder()
            .startPath(Shape.START, "interleaving")
                .connectElement(Shape.DECISION, "decision 1")
                    .branch()
                        .connectElement(Shape.OPERATION, "operation a", "a")
                        .connectElement(Shape.OPERATION, "operation 1")
                        .markBookmark("join 1")
                        .connectElement(Shape.OPERATION, "operation 2")
                        .markBookmark("join 2")
                        .connectElement(Shape.SKIP, "end")
                        .end()
                    .branch()
                        .connectElement(Shape.DECISION, "decision 2", "b")
                            .branch()
                                .connectBookmark("join 1", "c")
                                .end()
                            .branch()
                                .connectElement(Shape.OPERATION, "operation d", "d")
                                .connectBookmark("join 2")
                                .end()
                        .end()
            .build();
    }
}
