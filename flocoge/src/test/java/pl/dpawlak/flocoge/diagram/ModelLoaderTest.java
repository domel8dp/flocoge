package pl.dpawlak.flocoge.diagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import pl.dpawlak.flocoge.diagram.ModelElement.Shape;
import pl.dpawlak.flocoge.diagram.util.ModelBuilder;
import pl.dpawlak.flocoge.diagram.util.ModelTraversingValidator;

/**
 * Created by dpawlak on Dec 30, 2014
 */
public class ModelLoaderTest {
    
    private static final File PLAIN_DIAGRAM = new File("src/test/resources/TestDiagramPlain");
    private static final Map<String, ModelElement> EXPECTED_PATHS = new ModelBuilder()
        .startPath("A", Shape.ON_PAGE_REF, "handle error<br>")
            .connectElement(Shape.OPERATION, "show error<br>mesage<br>")
        .startPath("B", Shape.EVENT, "user action<br>")
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
        .startPath("C", Shape.EVENT, "input<br>available<br>")
            .connectElement(Shape.OPERATION, "perform action<br>")
            .connectElement(Shape.DECISION, "is data valid?<br>")
                .branch()
                    .connectElement(Shape.OPERATION, "prepare data<br>for storage<br>", "Y")
                    .connectElement(Shape.OPERATION, "save in<br>storage<br>")
                    .end()
                .branch()
                    .connectElement(Shape.ON_PAGE_REF, "handle error<br>", "N")
                    .end()
        .build();
    
    @Test
    public void testLoadingModel() throws XMLStreamException, DiagramLoadingException, FileNotFoundException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(PLAIN_DIAGRAM));
        
        ModelLoader loader = new ModelLoader(factory);
        Map<String, ModelElement> startElements = loader.loadModel(reader, reader.nextTag().asStartElement());
        
        new ModelTraversingValidator(startElements.values(), EXPECTED_PATHS.values()).validate();
    }

}
