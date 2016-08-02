package pl.dpawlak.flocoge.generator;

import pl.dpawlak.flocoge.generator.util.TestCodeModel;
import pl.dpawlak.flocoge.generator.util.TestCodeModelBuilder;

public class TestCodeModels {

    public static TestCodeModel createTestFileCodeModel() throws CodeGenerationException {
        return new TestCodeModelBuilder()
            .startPath("inputAvailable")
                .callDelegate("performAction")
                .callIf("isDataValid")
                    .beginThen()
                        .callDelegate("prepareDataForStorage")
                        .callDelegate("saveInStorage")
                        .end()
                    .beginElse()
                        .callLocal("handleError")
                        .end()
            .startPath("userAction")
                .callLocal("performDefinedAction")
                .callSwitch("whichUserType", "NORMAL", "VIP", "ADMIN")
                    .beginCase("NORMAL")
                        .callExternal("processNormalUserRequest")
                        .callBreak()
                        .end()
                    .beginCase("VIP")
                        .callExternal("processVipRequest")
                        .callBreak()
                        .end()
                    .beginCase("ADMIN")
                        .callExternal("processAdminRequest")
                        .callBreak()
                        .end()
            .startPrivatePath("handleError")
                .callDelegate("showErrorMesage")
            .startPrivatePath("performDefinedAction")
                .callDelegate("runDefinedAction")
            .build();
    }

    public static TestCodeModel createComplexCodeModel() {
        return new TestCodeModelBuilder()
            .startPath("areThereSavedDevices")
                .callIf("areThereSavedDevices")
                    .beginThen()
                    .callDelegate("displayDevices")
                        .end()
                    .beginElse()
                        .callIf("doYouWantToDiscover")
                            .beginThen()
                                .callDelegate("openDiscoveryTab")
                                .callLocal("discoveryFinished")
                                .end()
                            .beginElse()
                                .callDelegate("displayMessage")
                                .end()
                        .end()
            .startPath("restorePoll")
                .callIfNot("connectionRequest")
                    .beginThen()
                        .callDelegate("restoreFullPollOperation")
                        .callDelegate("resetRequestCounter")
                        .end()
                .callLocal("discoveryFinished")
                .callDelegate("enableUI")
            .startPrivatePath("discoveryFinished")
                .callDelegate("hideProgressCancel")
                .callIfNot("foundDevices")
                    .beginThen()
                        .callDelegate("showMessage")
                        .end()
                .callDelegate("enableUIElements")
            .build();
    }

    public static TestCodeModel createAdditionalCodeModel() throws CodeGenerationException {
        return new TestCodeModelBuilder()
            .startPath("publicPath")
                .callLocal("localPath")
                .callIf("mainIf")
                    .beginThen()
                        .callIf("hangingIf")
                            .beginThen()
                                .callDelegate("hangingOperation")
                                .callReturn()
                                .end()
                        .end()
                .callExternal("finalOperation")
            .startExternalPath("publicExternalPath")
                .callLocal("localPath")
                .callSwitch("mainSwitch", "A", "B")
                    .beginCase("A")
                        .callSwitch("hangingSwitch", "A1", "A2")
                            .beginCase("A1")
                                .callDelegate("hangingOperation")
                                .callReturn()
                                .end()
                            .beginCase("A2")
                                .callBreak()
                                .end()
                        .callBreak()
                        .end()
                    .beginCase("B")
                        .callBreak()
                        .end()
                .callExternal("finalOperation")
            .startPrivatePath("localPath")
                .callExternal("finalOperation")
            .build();
    }

    public static TestCodeModel createCodeModelWithEmptyIf() throws CodeGenerationException {
        return new TestCodeModelBuilder()
            .startPath("enableUI")
                .callIfNot("enableUI")
                    .beginThen()
                        .callDelegate("operation")
                        .end()
                .callDelegate("finalOperation")
            .build();
    }

    public static TestCodeModel createCodeModelWithEmptyElse() throws CodeGenerationException {
        return new TestCodeModelBuilder()
            .startPath("enableUI")
                .callIf("enableUI")
                    .beginThen()
                        .callDelegate("operation")
                        .end()
                .callDelegate("finalOperation")
            .build();
    }

    public static TestCodeModel createCodeModelWithEmptyIfAndElse() throws CodeGenerationException {
        return new TestCodeModelBuilder()
            .startPath("enableUI")
                .callBooleanDelegate("enableUI")
                .callDelegate("finalOperation")
            .build();
    }
}
