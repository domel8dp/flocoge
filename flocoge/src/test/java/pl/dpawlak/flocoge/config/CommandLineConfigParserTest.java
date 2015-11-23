package pl.dpawlak.flocoge.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import pl.dpawlak.flocoge.log.Logger;

public class CommandLineConfigParserTest {

    private static final int MIN_PARAMS = 3;
    private static final String VALID_DIAGRAM_PATH = "build.gradle";
    private static final String VALID_SRC_FOLDER = "src/main/java";
    private static final String VALID_PACKAGE_NAME = "pl.dpawlak.flocoge";
    private static final String[] VALID_PARAMS = {VALID_DIAGRAM_PATH, VALID_SRC_FOLDER, VALID_PACKAGE_NAME};
    private static final String HELP_FLAG = "--help";
    private static final String INVALID_DIAGRAM_PATH = "build.xml";
    private static final String INVALID_SRC_FOLDER = "build.gradle";
    private static final String INVALID_PACKAGE_NAME = "pl.dpawlak.2.invalid";
    private static final String MINIMAL_VALID_PACKAGE_NAME = "p";
    private static final String RESERVED_WORD_PACKAGE_NAME = "pl.true.flocoge";

    @Test
    public void testInsuficientParameters() {
        for (int i = 0; i < MIN_PARAMS; i++) {
            CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
            assertFalse(parser.parse(Arrays.copyOf(VALID_PARAMS, i)));
        }
    }

    @Test
    public void testValidParameters() throws IOException {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertTrue(parser.parse(VALID_PARAMS));
        Configuration config = parser.getConfiguration();
        assertNotNull(config);
        assertEquals(new File(VALID_DIAGRAM_PATH).getCanonicalPath(), config.diagramPath.getCanonicalPath());
        assertEquals(new File(VALID_SRC_FOLDER).getCanonicalPath(), config.srcFolder.getCanonicalPath());
        assertEquals(VALID_PACKAGE_NAME, config.packageName);
    }

    @Test
    public void testHelpFlag() {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertFalse(parser.parse(new String[] {HELP_FLAG, VALID_DIAGRAM_PATH, VALID_SRC_FOLDER, VALID_PACKAGE_NAME}));
    }

    @Test
    public void testInvalidDiagramPath() {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertFalse(parser.parse(new String[] {INVALID_DIAGRAM_PATH, VALID_SRC_FOLDER, VALID_PACKAGE_NAME}));
    }

    @Test
    public void testInvalidSrcFolder() {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertFalse(parser.parse(new String[] {VALID_DIAGRAM_PATH, INVALID_SRC_FOLDER, VALID_PACKAGE_NAME}));
    }

    @Test
    public void testInvalidPackageName() {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertFalse(parser.parse(new String[] {VALID_DIAGRAM_PATH, VALID_SRC_FOLDER, INVALID_PACKAGE_NAME}));
    }

    @Test
    public void testMinimalPackageNameParameters() throws IOException {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertTrue(parser.parse(new String[] {VALID_DIAGRAM_PATH, VALID_SRC_FOLDER, MINIMAL_VALID_PACKAGE_NAME}));
    }

    @Test
    public void testPackageNameWithReservedWord() {
        CommandLineConfigParser parser = new CommandLineConfigParser(mock(Logger.class));
        assertFalse(parser.parse(new String[] {VALID_DIAGRAM_PATH, VALID_SRC_FOLDER, RESERVED_WORD_PACKAGE_NAME}));
    }
}
