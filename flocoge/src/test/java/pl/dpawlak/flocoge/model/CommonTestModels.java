package pl.dpawlak.flocoge.model;

import pl.dpawlak.flocoge.model.ModelElement.Shape;
import pl.dpawlak.flocoge.model.util.ModelBuilder;

public class CommonTestModels {

    private static final String INVALID_LABEL = "?";

    public static FlocogeModel createTestFileModel() {
        return new ModelBuilder()
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
            .startPath(Shape.ON_PAGE_REF, "handle error<br>")
                .connectElement(Shape.OPERATION, "show error<br>mesage<br>")
                .connectElement(Shape.SKIP, "end")
            .build();
    }

    public static FlocogeModel createTestFileModelTransformed() {
        return new ModelBuilder()
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
            .startPath(Shape.ON_PAGE_REF, "handleError")
                .connectElement(Shape.OPERATION, "showErrorMesage")
            .build();
    }

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

    public static FlocogeModel createModelWithBooleanDecisionBranches() {
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

    public static FlocogeModel createTransformedModelWithBooleanDecisionBranches() {
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

    public static FlocogeModel createModelWithInvertedBooleanDecisionBranches() {
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

    public static FlocogeModel createTransformedModelWithInvertedBooleanDecisionBranches() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "isOk")
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "processVipRequest", "true")
                    .end()
                .branch()
                    .connectElement(Shape.OFF_PAGE_REF, "processNormalUserRequest", "false")
                    .end()
            .build();
    }

    public static FlocogeModel createModelWithOnPageRefs() {
        return new ModelBuilder()
            .startPath(Shape.ON_PAGE_REF, "private1")
            .startPath(Shape.OPERATION, "operation1")
            .startPath(Shape.ON_PAGE_REF, "private2")
            .startPath(Shape.OPERATION, "operation2")
            .build();
    }

    public static FlocogeModel createTransformedModelWithOnPageRefs() {
        return new ModelBuilder()
            .startPath(Shape.OPERATION, "operation1")
            .startPath(Shape.OPERATION, "operation2")
            .startPath(Shape.ON_PAGE_REF, "private1")
            .startPath(Shape.ON_PAGE_REF, "private2")
            .build();
    }

    public static FlocogeModel createComplexModel() {
        return new ModelBuilder()
            .startPath(Shape.SKIP, "start")
                .connectElement(Shape.DECISION, "are there saved devices ?")
                    .branch()
                        .connectElement(Shape.DECISION, "do you want to discover ?", "N")
                            .branch()
                                .connectElement(Shape.OPERATION, "open discovery tab", "Y")
                                .connectElement(Shape.ON_PAGE_REF, "start discovery")
                                .end()
                            .branch()
                                .connectElement(Shape.OPERATION, "display message", "N")
                                .connectElement(Shape.SKIP, "end")
                                .end()
                        .end()
                    .branch()
                        .connectElement(Shape.OPERATION, "display devices", "Y")
                        .connectElement(Shape.SKIP, "end")
                        .end()
            .startPath(Shape.ON_PAGE_REF, "discovery finished")
                .connectElement(Shape.OPERATION, "hide progress,<br>cancel")
                .connectElement(Shape.DECISION, "found devices?")
                    .branch()
                        .connectElement(Shape.OPERATION, "enable UI elements", "Y")
                        .markBookmark("JOIN 1")
                        .connectElement(Shape.SKIP, "end")
                        .end()
                    .branch()
                        .connectElement(Shape.OPERATION, "show message", "N")
                        .connectBookmark("JOIN 1")
                        .end()
            .startPath(Shape.ON_PAGE_REF, "restore poll")
                .connectElement(Shape.DECISION, "connection request?")
                    .branch()
                        .connectElement(Shape.OPERATION, "restore full poll operation", "N")
                        .connectElement(Shape.OPERATION, "reset request counter")
                        .connectElement(Shape.ON_PAGE_REF, "enqueue request")
                        .markBookmark("JOIN 2")
                        .connectElement(Shape.OPERATION, "enable UI")
                        .connectElement(Shape.SKIP, "end")
                        .end()
                    .branch()
                        .connectBookmark("JOIN 2", "Y")
                        .end()
            .build();
    }

    public static FlocogeModel createTransformedComplexModel() {
        return new ModelBuilder()
            .startPath(Shape.DECISION, "areThereSavedDevices")
                .branch()
                    .connectElement(Shape.OPERATION, "displayDevices", "true")
                    .end()
                .branch()
                    .connectElement(Shape.DECISION, "doYouWantToDiscover", "false")
                        .branch()
                            .connectElement(Shape.OPERATION, "openDiscoveryTab", "true")
                            .connectElement(Shape.ON_PAGE_REF, "startDiscovery")
                            .end()
                        .branch()
                            .connectElement(Shape.OPERATION, "displayMessage", "false")
                            .end()
                    .end()
            .startPath(Shape.ON_PAGE_REF, "discoveryFinished")
                .connectElement(Shape.OPERATION, "hideProgressCancel")
                .connectElement(Shape.DECISION, "foundDevices")
                    .branch()
                        .connectElement(Shape.OPERATION, "enableUIElements", "true")
                        .markBookmark("JOIN")
                        .end()
                    .branch()
                        .connectElement(Shape.OPERATION, "showMessage", "false")
                        .connectBookmark("JOIN")
                        .end()
            .startPath(Shape.ON_PAGE_REF, "restorePoll")
                .connectElement(Shape.DECISION, "connectionRequest")
                    .branch()
                        .connectElement(Shape.ON_PAGE_REF, "enqueueRequest", "true")
                        .markBookmark("JOIN 2")
                        .connectElement(Shape.OPERATION, "enableUI")
                        .end()
                    .branch()
                        .connectElement(Shape.OPERATION, "restoreFullPollOperation", "false")
                        .connectElement(Shape.OPERATION, "resetRequestCounter")
                        .connectBookmark("JOIN 2")
                        .end()
            .build();
    }
}
