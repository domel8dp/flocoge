package pl.dpawlak.flocoge.diagram;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pl.dpawlak.flocoge.config.Configuration;

/**
 * Created by dpawlak on Dec 17, 2014
 */
public class DiagramLoader {
    
    private final Configuration config;
    private final ModelLoader loader;
    private final XMLInputFactory factory;
    private XMLEventReader reader;
    private StartElement startElement;

    public DiagramLoader(Configuration config, ModelLoader loader) {
        this.config = config;
        this.loader = loader;
        factory = XMLInputFactory.newInstance();
    }
    
    public void loadDiagram() throws DiagramLoadingException {
        try (FileInputStream inputStream = new FileInputStream(config.diagramPath)) {
            reader = factory.createXMLEventReader(inputStream);
            if (reader.hasNext()) {
                startElement = reader.nextTag().asStartElement();
                String rootName = startElement.getName().getLocalPart();
                if ("mxfile".equals(rootName)) {
                    Characters encryptedContentElement = findDiagramElement();
                    reader.close();
                    String diagramData = decryptDiagram(encryptedContentElement.getData());
                    prepareXmlReader(diagramData);
                    loader.loadModel(reader, startElement);
                } else if ("mxGraphModel".equals(rootName)) {
                    loader.loadModel(reader, startElement);
                } else {
                    throw new DiagramLoadingException("Diagram loading error (invalid root element)");
                }
            } else {
                throw new DiagramLoadingException("Diagram loading error (empty xml reader)");
            }
        } catch (XMLStreamException | IOException ex) {
            throw new DiagramLoadingException(ex);
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (XMLStreamException ignored) { }
            }
        }
    }

    private Characters findDiagramElement() throws DiagramLoadingException {
        try {
            XMLEventReader filteredReader = factory.createFilteredReader(reader, new DiagramElementFilter());
            if (filteredReader.hasNext() && filteredReader.nextEvent() != null && reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isCharacters()) {
                    return event.asCharacters();
                } else {
                    throw new DiagramLoadingException("Diagram loading error (diagram element content is not string)");
                }
            } else {
                throw new DiagramLoadingException("Diagram loading error (diagram element not found)");
            }
        } catch (XMLStreamException ex) {
            throw new DiagramLoadingException(ex);
        }
    }
    
    private String decryptDiagram(String content) throws DiagramLoadingException {
        byte[] data = decryptBase64(content);
        String inflatedData = inflateData(data);
        return decodeUrlString(inflatedData);
    }
    
    private void prepareXmlReader(String data) throws DiagramLoadingException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        try {
            reader = factory.createXMLEventReader(inputStream);
            if (reader.hasNext()) {
                startElement = reader.nextTag().asStartElement();
                String rootName = startElement.getName().getLocalPart();
                if (!"mxGraphModel".equals(rootName)) {
                    throw new DiagramLoadingException("Diagram loading error (invalid root element)");
                }
            } else {
                throw new DiagramLoadingException("Diagram loading error (empty xml reader)");
            }
        } catch (XMLStreamException ex) {
            throw new DiagramLoadingException(ex);
        }
    }
    
    private byte[] decryptBase64(String content) throws DiagramLoadingException {
        try {
            return DatatypeConverter.parseBase64Binary(content);
        } catch (IllegalArgumentException ex) {
            throw new DiagramLoadingException(ex);
        }
    }
    
    private String inflateData(byte[] data) throws DiagramLoadingException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length * 2);
        try (InflaterOutputStream inflaterStream = new InflaterOutputStream(outputStream, new Inflater(true))) {
            inflaterStream.write(data);
            return outputStream.toString(StandardCharsets.UTF_8.name());
        } catch (IOException ex) {
            throw new DiagramLoadingException(ex);
        }
    }
    
    private String decodeUrlString(String data) throws DiagramLoadingException {
        try {
            return URLDecoder.decode(data, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new DiagramLoadingException(ex);
        }
    }
    
    private static class DiagramElementFilter implements EventFilter {

        @Override
        public boolean accept(XMLEvent event) {
            return event.isStartElement() && "diagram".equals(event.asStartElement().getName().getLocalPart());
        }
    }
}
