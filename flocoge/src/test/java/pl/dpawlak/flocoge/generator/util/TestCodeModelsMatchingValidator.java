package pl.dpawlak.flocoge.generator.util;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Map;

public class TestCodeModelsMatchingValidator {

    private final TestCodeModel expected;
    private final TestCodeModel actual;

    public TestCodeModelsMatchingValidator(TestCodeModel expected, TestCodeModel actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public void validate() {
        assertEquals(expected.pathMethods, actual.pathMethods);
        validateEachPathMethod();
        assertEquals(expected.delegateMethods, actual.delegateMethods);
        assertEquals(expected.delegateBooleanMethods, actual.delegateBooleanMethods);
        assertEquals(expected.delegateEnumMethods, actual.delegateEnumMethods);
        assertEquals(expected.externalMethods, actual.externalMethods);
        assertEquals(expected.externalCallsPresent, actual.externalCallsPresent);
    }

    private void validateEachPathMethod() {
        Iterator<Call> expectedPathIterator = expected.pathMethods.iterator();
        Iterator<Call> actualPathIterator = actual.pathMethods.iterator();
        while (expectedPathIterator.hasNext() && actualPathIterator.hasNext()) {
            TestCodeBlock expectedCodeBlock = expected.pathMethodBlocks.get(expectedPathIterator.next().name);
            TestCodeBlock actualCodeBlock = actual.pathMethodBlocks.get(actualPathIterator.next().name);
            validateCodeBlocks(expectedCodeBlock, actualCodeBlock);
        }
    }

    private void validateCodeBlocks(TestCodeBlock expectedCodeBlock, TestCodeBlock actualCodeBlock) {
        assertEquals(expectedCodeBlock != null, actualCodeBlock != null);
        if (expectedCodeBlock != null && actualCodeBlock != null) {
            assertEquals(expectedCodeBlock.calls, actualCodeBlock.calls);
            for (Call call : expectedCodeBlock.calls) {
                if (call.type == Call.Type.IF) {
                    TestCodeIf expectedIf = expectedCodeBlock.ifs.get(call.name);
                    TestCodeIf actualIf = actualCodeBlock.ifs.get(call.name);
                    validateCodeIfs(expectedIf, actualIf);
                } else if (call.type == Call.Type.SWITCH) {
                    TestCodeSwitch expectedSwitch = expectedCodeBlock.switches.get(call.name);
                    TestCodeSwitch actualSwitch = actualCodeBlock.switches.get(call.name);
                    validateCodeSwitches(expectedSwitch, actualSwitch);
                }
            }
        }
    }

    private void validateCodeIfs(TestCodeIf expectedIf, TestCodeIf actualIf) {
        validateCodeBlocks(expectedIf._then, actualIf._then);
        validateCodeBlocks(expectedIf._else, actualIf._else);
    }

    private void validateCodeSwitches(TestCodeSwitch expectedSwitch, TestCodeSwitch actualSwitch) {
        assertEquals(expectedSwitch != null, actualSwitch != null);
        assertEquals(expectedSwitch.cases.size(), actualSwitch.cases.size());
        Iterator<Map.Entry<String, TestCodeBlock>> expectedBranchIterator = expectedSwitch.cases.entrySet().iterator();
        Iterator<Map.Entry<String, TestCodeBlock>> actualBranchIterator = actualSwitch.cases.entrySet().iterator();
        while (expectedBranchIterator.hasNext() && actualBranchIterator.hasNext()) {
            Map.Entry<String, TestCodeBlock> expectedBranch = expectedBranchIterator.next();
            Map.Entry<String, TestCodeBlock> actualBranch = actualBranchIterator.next();
            assertEquals(expectedBranch.getKey(), actualBranch.getKey());
            validateCodeBlocks(expectedBranch.getValue(), actualBranch.getValue());
        }
    }
}
