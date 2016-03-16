package pl.dpawlak.flocoge.config;

import static org.junit.Assert.*;
import static pl.dpawlak.flocoge.config.ConfigurationUtils.*;

import java.io.File;

import org.junit.Test;

public class ConfigurationUtilsTests {

    private static final File EXISTING_FILE = new File("build.gradle");
    private static final File MISSNG_PATH = new File("missing.path");
    private static final File EXISTING_FOLDER = new File("src");
    private static final String VALID_PACKAGE_NAME = "pl.dpawlak.flocoge2";
    private static final String MINIMAL_VALID_PACKAGE_NAME = "p";
    private static final String PACKAGE_NAME_WITH_RESERVED_WORD = "pl.null.flocoge";
    private static final String PACKAGE_NAME_WITH_INVALID_PART = "pl.2.flocoge";
    private static final String VALID_DIAGRAM_NAME = "Diagram";
    private static final String MINIMAL_VALID_DIAGRAM_NAME = "#@$^&X!>";
    private static final String INVALID_DIAGRAM_NAME = "#@$^&!>";
    private static final File VALID_DIAGRAM_NAME_FROM_PATH = new File("resources/Diagram.xml");
    private static final File INVALID_DIAGRAM_NAME_FROM_PATH = new File("resources/%%.xml");
    private static final String DIAGRAM_NAME = "10 a test diagram name";
    private static final String CONVERTED_DIAGRAM_NAME = "ATestDiagramName_10";

    /*
     * existing diagram path -> ok
     * missing diagram path -> nok
     * existing folder as diagram path -> nok
     * null -> nok
     */
    @Test
    public void testDiagramPath() {
        assertTrue(diagramExists(EXISTING_FILE));
        assertFalse(diagramExists(MISSNG_PATH));
        assertFalse(diagramExists(EXISTING_FOLDER));
        assertFalse(diagramExists(null));
    }

    /*
     * existing src directory -> ok
     * missing src directory -> nok
     * existing file as src directory -> nok
     * null -> nok
     */
    @Test
    public void testSrcFolder() {
        assertTrue(srcFolderExists(EXISTING_FOLDER));
        assertFalse(srcFolderExists(MISSNG_PATH));
        assertFalse(srcFolderExists(EXISTING_FILE));
        assertFalse(srcFolderExists(null));
    }

    /*
     * valid package name (dot separated, every part starts with a letter, _ or $, can contain digits) -> ok
     * reserved word -> nok
     * invalid part (only digits) -> nok
     * null -> nok
     */
    @Test
    public void testPackageName() {
        assertTrue(packageNameValid(VALID_PACKAGE_NAME));
        assertTrue(packageNameValid(MINIMAL_VALID_PACKAGE_NAME));
        assertFalse(packageNameValid(PACKAGE_NAME_WITH_RESERVED_WORD));
        assertFalse(packageNameValid(PACKAGE_NAME_WITH_INVALID_PART));
        assertFalse(packageNameValid(null));
    }

    /*
     * valid diagram name (at least one letter) -> ok
     * invalid name (only symbols) -> nok
     * null -> nok
     */
    @Test
    public void testDiagramName() {
        assertTrue(diagramNameValid(VALID_DIAGRAM_NAME));
        assertTrue(diagramNameValid(MINIMAL_VALID_DIAGRAM_NAME));
        assertFalse(diagramNameValid(INVALID_DIAGRAM_NAME));
        assertFalse(diagramNameValid((String)null));
    }

    /*
     * valid diagram name (at least one letter in file name, excluding extension) -> ok
     * invalid name (only symbols) -> nok
     */
    @Test
    public void testDiagramNameFromFile() {
        assertTrue(diagramNameValid(VALID_DIAGRAM_NAME_FROM_PATH));
        assertFalse(diagramNameValid(INVALID_DIAGRAM_NAME_FROM_PATH));
        assertFalse(diagramNameValid((File)null));
    }

    /*
     * valid configuration -> no exception
     */
    @Test
    public void testCheckingValidConfig() throws InvalidConfigurationException {
        Configuration config = new ConfigurationBuilder().withDiagramPath(EXISTING_FILE).withSrcFolder(EXISTING_FOLDER)
            .withPackageName(VALID_PACKAGE_NAME).build();
        check(config);
    }

    /*
     * configuration with missing file -> config exception
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testCheckingConfigWithInvalidDiagram() throws InvalidConfigurationException {
        Configuration config = new ConfigurationBuilder().withDiagramPath(MISSNG_PATH).withSrcFolder(EXISTING_FOLDER)
            .withPackageName(VALID_PACKAGE_NAME).build();
        check(config);
    }

    /*
     * configuration with file as src folder -> config exception
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testCheckingConfigWithInvalidSrcFolder() throws InvalidConfigurationException {
        Configuration config = new ConfigurationBuilder().withDiagramPath(EXISTING_FILE).withSrcFolder(EXISTING_FILE)
            .withPackageName(VALID_PACKAGE_NAME).build();
        check(config);
    }

    /*
     * configuration with file as src folder, but dry run -> no exception
     */
    @Test
    public void testCheckingConfigWithInvalidSrcFolderAndDryRun() throws InvalidConfigurationException {
        Configuration config = new ConfigurationBuilder().withDiagramPath(EXISTING_FILE).withSrcFolder(EXISTING_FILE)
            .withPackageName(VALID_PACKAGE_NAME).dryRun().build();
        check(config);
    }

    /*
     * configuration with invalid package name -> config exception
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testCheckingConfigWithInvalidPackageName() throws InvalidConfigurationException {
        Configuration config = new ConfigurationBuilder().withDiagramPath(EXISTING_FILE).withSrcFolder(EXISTING_FOLDER)
            .withPackageName(PACKAGE_NAME_WITH_INVALID_PART).build();
        check(config);
    }

    /*
     * configuration with invalid diagram name -> config exception
     */
    @Test(expected = InvalidConfigurationException.class)
    public void testCheckingConfigWithInvalidDiagramName() throws InvalidConfigurationException {
        Configuration config = new ConfigurationBuilder().withDiagramPath(EXISTING_FILE).withSrcFolder(EXISTING_FOLDER)
            .withPackageName(VALID_PACKAGE_NAME).withName(INVALID_DIAGRAM_NAME).build();
        check(config);
    }

    /*
     * diagram name conversion -> camel case, no whitespace, initial digits moved to back
     */
    @Test
    public void testConvertingDiagramName() {
        Configuration config = new ConfigurationBuilder().withName(DIAGRAM_NAME).build();
        assertEquals(CONVERTED_DIAGRAM_NAME, getDiagramName(config));
    }
}
