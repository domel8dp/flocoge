package pl.dpawlak.flocoge;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import pl.dpawlak.flocoge.config.Configuration;
import pl.dpawlak.flocoge.config.ConfigurationBuilder;
import pl.dpawlak.flocoge.diagram.DiagramLoader;
import pl.dpawlak.flocoge.diagram.DiagramLoadingException;
import pl.dpawlak.flocoge.diagram.ModelInspector;
import pl.dpawlak.flocoge.generator.CodeGenerationException;
import pl.dpawlak.flocoge.generator.CodeGenerator;
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;

public class FlocogeTests {

    private static final File DIAGRAM_PATH = new File("build.gradle");
    private static final String PACKAGE_NAME = "pl.dpawlak.flocoge";

    private Logger log;
    private FlocogeModel model;
    private DiagramLoader diagramLoader;
    private ModelInspector inspector;
    private CodeGenerator generator;

    @Before
    public void createMocks() {
        log = mock(Logger.class);
        model = mock(FlocogeModel.class);
        diagramLoader = mock(DiagramLoader.class);
        inspector = mock(ModelInspector.class);
        generator = mock(CodeGenerator.class);
    }

    /*
     * happy path -> diagram loaded, printed, inspected; code generated
     */
    @Test
    public void testGeneration() throws Exception {
        Configuration config = validConfiguration();
        when(inspector.inspect(eq(model))).thenReturn(true);

        Flocoge flocoge = new Flocoge(log, config, model, diagramLoader, inspector, generator);
        flocoge.generate();

        verify(diagramLoader).loadDiagram(eq(model));
        verify(log).printBareModel(eq(model));
        verify(inspector).inspect(eq(model));
        verify(log).printModel(eq(model));
        verify(generator).generate(eq(model));
    }

    /*
     * invalid configuration -> exception
     */
    @Test(expected = FlocogeException.class)
    public void testInvalidConfiguration() throws FlocogeException {
        Configuration config = mock(Configuration.class);
        when(inspector.inspect(eq(model))).thenReturn(true);
        new Flocoge(log, config, model, diagramLoader, inspector, generator).generate();
    }

    /*
     * diagram loading failed -> exception
     */
    @Test(expected = FlocogeException.class)
    public void testDiagramLoadingFailure() throws FlocogeException, DiagramLoadingException {
        Configuration config = validConfiguration();
        doThrow(DiagramLoadingException.class).when(diagramLoader).loadDiagram(eq(model));
        when(inspector.inspect(eq(model))).thenReturn(true);
        new Flocoge(log, config, model, diagramLoader, inspector, generator).generate();
    }

    /*
     * inspection failed -> exception
     */
    @Test(expected = FlocogeException.class)
    public void testInspectionFailure() throws FlocogeException {
        Configuration config = validConfiguration();
        when(inspector.inspect(eq(model))).thenReturn(false);
        new Flocoge(log, config, model, diagramLoader, inspector, generator).generate();
    }

    /*
     * generation failed -> exception
     */
    @Test(expected = FlocogeException.class)
    public void testCodeGenerationFailure() throws FlocogeException, CodeGenerationException {
        Configuration config = validConfiguration();
        when(inspector.inspect(eq(model))).thenReturn(true);
        doThrow(CodeGenerationException.class).when(generator).generate(eq(model));
        new Flocoge(log, config, model, diagramLoader, inspector, generator).generate();
    }

    private Configuration validConfiguration() {
        return new ConfigurationBuilder().withDiagramPath(DIAGRAM_PATH).withPackageName(PACKAGE_NAME).dryRun().build();
    }
}
