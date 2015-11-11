package pl.dpawlak.flocoge.model;

import pl.dpawlak.flocoge.model.ModelElement.Shape;
import pl.dpawlak.flocoge.model.util.ModelBuilder;

public class TestModels {

    public static FlocogeModel createTestFileModel() {
        return new ModelBuilder()
            .startPath(Shape.START, "start", "5b0bd9cd9438474a-2")
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
            .startPath(Shape.EVENT, "user action<br>", "5b0bd9cd9438474a-3")
                .connectElement(Shape.ON_PAGE_REF, "perform<br>defined<br>action<br>")
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
            .startPath(Shape.ON_PAGE_REF, "handle error<br>", "5b0bd9cd9438474a-35")
                .connectElement(Shape.OPERATION, "show error<br>mesage<br>")
                .connectElement(Shape.SKIP, "end")
            .startPath(Shape.ON_PAGE_REF, "perform<br>defined<br>action<br>", "0")
                .connectElement(Shape.OPERATION, "run defined<br>action<br>")
                .connectElement(Shape.SKIP, "end")
            .build();
    }

    public static FlocogeModel createTestFileModelTransformed() {
        return new ModelBuilder()
            .startPath(Shape.START, "start", "inputAvailable")
                .connectElement(Shape.EVENT, "inputAvailable")
                .connectElement(Shape.OPERATION, "performAction")
                .connectElement(Shape.DECISION, "isDataValid")
                    .branch()
                        .connectElement(Shape.OPERATION, "prepareDataForStorage", "true")
                        .connectElement(Shape.OPERATION, "saveInStorage")
                        .end()
                    .branch()
                        .connectElement(Shape.ON_PAGE_REF, "handleError", "false")
                        .end()
            .startPath(Shape.EVENT, "userAction", "userAction")
                .connectElement(Shape.ON_PAGE_REF, "performDefinedAction")
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
            .startPath(Shape.ON_PAGE_REF, "handleError", "handleError")
                .connectElement(Shape.OPERATION, "showErrorMesage")
            .startPath(Shape.ON_PAGE_REF, "performDefinedAction", "performDefinedAction")
                .connectElement(Shape.OPERATION, "runDefinedAction")
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
            .startPath(Shape.DECISION, "isOk", "isOk")
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
            .startPath(Shape.DECISION, "isOk", "isOk")
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
            .startPath(Shape.OPERATION, "operation1", "operation1")
            .startPath(Shape.OPERATION, "operation2", "operation2")
            .startPath(Shape.ON_PAGE_REF, "private1", "private1")
            .startPath(Shape.ON_PAGE_REF, "private2", "private2")
            .build();
    }

    public static FlocogeModel createComplexModel() {
        return new ModelBuilder()
            .startPath(Shape.START, "start")
                .connectElement(Shape.DECISION, "are there saved devices ?")
                    .branch()
                        .connectElement(Shape.DECISION, "do you want to discover ?", "N")
                            .branch()
                                .connectElement(Shape.OPERATION, "open discovery tab", "Y")
                                .connectElement(Shape.ON_PAGE_REF, "discovery finished")
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
            .startPath(Shape.EVENT, "restore poll")
                .connectElement(Shape.DECISION, "connection request?")
                    .branch()
                        .connectElement(Shape.OPERATION, "restore full poll operation", "N")
                        .connectElement(Shape.OPERATION, "reset request counter")
                        .connectElement(Shape.ON_PAGE_REF, "discovery finished")
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
            .startPath(Shape.START, "start", "areThereSavedDevices")
                .connectElement(Shape.DECISION, "areThereSavedDevices")
                    .branch()
                        .connectElement(Shape.OPERATION, "displayDevices", "true")
                        .end()
                    .branch()
                        .connectElement(Shape.DECISION, "doYouWantToDiscover", "false")
                            .branch()
                                .connectElement(Shape.OPERATION, "openDiscoveryTab", "true")
                                .connectElement(Shape.ON_PAGE_REF, "discoveryFinished")
                                .end()
                            .branch()
                                .connectElement(Shape.OPERATION, "displayMessage", "false")
                                .end()
                        .end()
            .startPath(Shape.EVENT, "restorePoll", "restorePoll")
                .connectElement(Shape.DECISION, "connectionRequest")
                    .branch()
                        .connectElement(Shape.ON_PAGE_REF, "discoveryFinished", "true")
                        .markBookmark("JOIN 2")
                        .connectElement(Shape.OPERATION, "enableUI")
                        .end()
                    .branch()
                        .connectElement(Shape.OPERATION, "restoreFullPollOperation", "false")
                        .connectElement(Shape.OPERATION, "resetRequestCounter")
                        .connectBookmark("JOIN 2")
                        .end()
            .startPath(Shape.ON_PAGE_REF, "discoveryFinished", "discoveryFinished")
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
            .build();
    }

    public static FlocogeModel createModelWithoutStartLabel() {
        return new ModelBuilder()
            .startPath(Shape.START, null)
            .connectElement(Shape.OPERATION, "operation")
            .build();
    }

    public static FlocogeModel createModelWithEmptyStartLabel() {
        return new ModelBuilder()
            .startPath(Shape.START, "")
            .connectElement(Shape.EVENT, "event")
            .build();
    }

    public static FlocogeModel createModelWithPathLabel() {
        return new ModelBuilder()
            .startPath(Shape.START, "start")
                .connectElement(Shape.SKIP, "element")
                .connectElement(Shape.START, "")
                .connectElement(Shape.START, null)
                .connectElement(Shape.START, "start")
                .connectElement(Shape.OPERATION, "enable UI")
            .build();
    }

    public static FlocogeModel createTransformedModelWithPathLabel() {
        return new ModelBuilder()
            .startPath(Shape.START, "start", "enableUI")
                .connectElement(Shape.START, "")
                .connectElement(Shape.START, null)
                .connectElement(Shape.START, "start")
                .connectElement(Shape.OPERATION, "enableUI")
            .build();
    }
}
