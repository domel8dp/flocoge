package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;

import org.junit.Test;

import pl.dpawlak.flocoge.diagram.ModelNamesUtils;

public class ModelNamesUtilsTest {

    private static final String[] VALID_LABELS = {"a", "a<br>valid label<br>", " does tHis Work!? ", " 5@Times", "#3C", "16 64bit words", "a camelCase call", "isOk"};
    private static final String[] VALID_NAMES = {"a", "aValidLabel", "doesTHisWork", "times_5", "c_3", "bitWords_1664", "aCamelCaseCall", "isOk"};
    private static final String[] VALID_BRANCH_NAMES = {"A", "A_VALID_LABEL", "DOES_THIS_WORK", "TIMES_5", "C_3", "BIT_WORDS_16_64", "A_CAMELCASE_CALL", "ISOK"};
    private static final String[] INVALID_LABELS = {" <br> ", "#!", " ", "", "@3"};
    private static final String[] ENUM_NAMES = {"AResult", "AValidLabelResult", "DoesTHisWorkResult", "Times_5Result", "C_3Result", "BitWords_1664Result", "ACamelCaseCallResult", "IsOkResult"};

    @Test
    public void testValidLabels() {
        for (int i = 0; i < VALID_LABELS.length; i++) {
            assertTrue(ModelNamesUtils.validateElementLabel(VALID_LABELS[i]));
            assertEquals(VALID_NAMES[i], ModelNamesUtils.convertElementLabel(VALID_LABELS[i]));
            assertEquals(VALID_BRANCH_NAMES[i], ModelNamesUtils.convertConnectionLabel(VALID_LABELS[i]));
        }
    }

    @Test
    public void testInvalidLabels() {
        for (int i = 0; i < INVALID_LABELS.length; i++) {
            assertFalse(ModelNamesUtils.validateElementLabel(INVALID_LABELS[i]));
        }
    }

    @Test
    public void testEnumNames() {
        for (int i = 0; i < ENUM_NAMES.length; i++) {
            assertEquals(ENUM_NAMES[i], ModelNamesUtils.createEnumName(VALID_NAMES[i]));
        }
    }
}
