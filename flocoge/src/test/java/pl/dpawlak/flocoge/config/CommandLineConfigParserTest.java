package pl.dpawlak.flocoge.config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import pl.dpawlak.flocoge.log.Logger;

public class CommandLineConfigParserTest {

    private static final String DIAGRAM_PATH = "build.gradle";
    private static final String SRC_FOLDER = "src/main/java";
    private static final String PACKAGE_NAME = "pl.dpawlak.flocoge";
    private static final String NAME = "DiagramName";
    private static final String[] SHORT_ARGS = {"-b", "-d", "-n", NAME, "-p", "-s", "-t", "-v", DIAGRAM_PATH,
        SRC_FOLDER, PACKAGE_NAME};
    private static final String[] LONG_ARGS = {"--dry", "--name", NAME, "--print-bare", "--print-model", "--stacktrace",
        "--trace", "--verbose", DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME};
    private static final String INVALID_DIAGRAM_PATH = "missing.file";
    private static final String INVALID_SRC_FOLDER = "build.gradle";
    private static final String INVALID_PACKAGE_NAME = "pl.dpawlak.2.invalid";
    private static final String MINIMAL_VALID_PACKAGE_NAME = "p";
    private static final String RESERVED_WORD_PACKAGE_NAME = "pl.true.flocoge";
    
    private CommandLineConfigParser parser;

    @Before
    public void init() {
        parser = new CommandLineConfigParser(mock(Logger.class));
    }

    /*
     * when args count less than 3 -> fail
     */
    @Test
    public void testInsuficientParameters() {
        assertFalse(parser.parse(new String[] { }));
        assertFalse(parser.parse(new String[] {PACKAGE_NAME}));
        assertFalse(parser.parse(new String[] {SRC_FOLDER, PACKAGE_NAME}));
    }

    /*
     * min params count is 3: <diagram path> <src folder> <package name>
     */
    @Test
    public void testMinimalParametersSet() throws IOException {
        assertTrue(parser.parse(new String[] {DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME}));
        Configuration config = parser.getConfiguration();
        assertNotNull(config);
        assertEquals(new File(DIAGRAM_PATH).getCanonicalPath(), config.diagramPath.getCanonicalPath());
        assertEquals(new File(SRC_FOLDER).getCanonicalPath(), config.srcFolder.getCanonicalPath());
        assertEquals(PACKAGE_NAME, config.packageName);
        assertNull(config.name);
        assertFalse(config.stacktrace);
        assertFalse(config.verbose);
        assertFalse(config.printBareModel);
        assertFalse(config.printModel);
        assertFalse(config.trace);
        assertFalse(config.dry);
    }

    /*
     * test all flags (short names)
     */
    @Test
    public void testShortArgs() {
        testArgs(SHORT_ARGS);
    }

    /*
     * test all flags (long names)
     */
    @Test
    public void testLongArgs() {
        testArgs(LONG_ARGS);
    }

    private void testArgs(String[] args) {
        assertTrue(parser.parse(args));
        Configuration config = parser.getConfiguration();
        assertNotNull(config);
        assertEquals(NAME, config.name);
        assertTrue(config.stacktrace);
        assertTrue(config.verbose);
        assertTrue(config.printBareModel);
        assertTrue(config.printModel);
        assertTrue(config.trace);
        assertTrue(config.dry);
    }

    /*
     * -h -> only prints help
     */
    @Test
    public void testShortHelpFlag() {
        assertFalse(parser.parse(new String[] {"-h", DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME}));
    }

    /*
     * --help -> only prints help
     */
    @Test
    public void testLongHelpFlag() {
        assertFalse(parser.parse(new String[] {"--help", DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME}));
    }

    /*
     * trace flag implies stacktrace
     */
    @Test
    public void testTraceFlag() {
        assertTrue(parser.parse(new String[] {"-t", DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME}));
        Configuration config = parser.getConfiguration();
        assertNotNull(config);
        assertTrue(config.stacktrace);
        assertTrue(config.trace);
    }

    /*
     * missing diagram file -> fail
     */
    @Test
    public void testInvalidDiagramPath() {
        assertFalse(parser.parse(new String[] {INVALID_DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME}));
    }

    /*
     * file given as src folder -> fail
     */
    @Test
    public void testInvalidSrcFolder() {
        assertFalse(parser.parse(new String[] {DIAGRAM_PATH, INVALID_SRC_FOLDER, PACKAGE_NAME}));
    }

    /*
     * invalid package name -> fail
     */
    @Test
    public void testInvalidPackageName() {
        assertFalse(parser.parse(new String[] {DIAGRAM_PATH, SRC_FOLDER, INVALID_PACKAGE_NAME}));
    }

    /*
     * single letter package name is allowed
     */
    @Test
    public void testMinimalPackageNameParameters() {
        assertTrue(parser.parse(new String[] {DIAGRAM_PATH, SRC_FOLDER, MINIMAL_VALID_PACKAGE_NAME}));
    }

    /*
     * package name with reserved word -> fail
     */
    @Test
    public void testPackageNameWithReservedWord() {
        assertFalse(parser.parse(new String[] {DIAGRAM_PATH, SRC_FOLDER, RESERVED_WORD_PACKAGE_NAME}));
    }

    /*
     * no name argument after --name
     */
    @Test
    public void testMissingName() {
        assertFalse(parser.parse(new String[] {"--name", DIAGRAM_PATH, SRC_FOLDER, PACKAGE_NAME}));
    }
}
