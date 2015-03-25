package pl.dpawlak.flocoge.diagram;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import pl.dpawlak.flocoge.diagram.ModelElementParser.Connection;
import pl.dpawlak.flocoge.diagram.ModelElementParser.Label;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

/**
 * Created by dpawlak on Dec 20, 2014
 */
public class ModelLoader {
    
    private final XMLInputFactory factory;
    private final ModelElementParser parser;
    private final List<Label> labels;
    private final Map<String, Connection> connections;
    private final Map<String, ModelElement> elements;
    
    public ModelLoader(XMLInputFactory factory) {
        this.factory = factory;
        parser = new ModelElementParser();
        labels = new LinkedList<>();
        connections = new LinkedHashMap<>();
        elements = new HashMap<>();
    }
    
    public Collection<ModelElement> loadModel(XMLEventReader reader, StartElement rootElement) throws DiagramLoadingException {
        parseElements(reader, rootElement);
        assignLabelsToConnections();
        return connectElements();
    }

    private void assignLabelsToConnections() {
        for (Label label : labels) {
            Connection connection = connections.get(label.parentId);
            if (connection != null) {
                connection.label = label.value;
            }
        }
        labels.clear();
    }
    
    private Collection<ModelElement> connectElements() {
        Map<String, ModelElement> startElements = new LinkedHashMap<>(elements);
        for (Connection connection : connections.values()) {
            ModelElement sourceElement = elements.get(connection.sourceId);
            if (sourceElement != null) {
                ModelElement targetElement = startElements.remove(connection.targetId);
                if (targetElement == null) {
                    targetElement = elements.get(connection.targetId);
                }
                if (targetElement != null) {
                    ModelConnection modelConnection = new ModelConnection();
                    modelConnection.label = connection.label;
                    modelConnection.target = targetElement;
                    sourceElement.connections.add(modelConnection);
                }
            }
        }
        connections.clear();
        elements.clear();
        return startElements.values();
    }

    private void parseElements(XMLEventReader reader, StartElement rootElement) throws DiagramLoadingException {
        try {
            XMLEventReader filteredReader = factory.createFilteredReader(reader, new MxCellElementFilter());
            while (filteredReader.hasNext()) {
                parseElement(filteredReader.nextEvent().asStartElement());
            }
        } catch (XMLStreamException ex) {
            throw new DiagramLoadingException(ex);
        }
    }
    
    private void parseElement(StartElement element) throws DiagramLoadingException {
        switch (parser.parseNextElement(element)) {
            case ELEMENT:
                ModelElement modelElement = parser.getModelElement();
                elements.put(modelElement.id, modelElement);
                break;
            case CONNECTION:
                Connection connection = parser.getConnection();
                connections.put(connection.id, connection);
                break;
            case LABEL:
                labels.add(parser.getLabel());
                break;
            default:
                break;
        }
    }

    public XMLInputFactory getFactory() {
        return factory;
    }
    
    private static class MxCellElementFilter implements EventFilter {

        @Override
        public boolean accept(XMLEvent event) {
            if (event.isStartElement()) {
                StartElement element = event.asStartElement();
                return "mxCell".equals(element.getName().toString()) &&
                    element.getAttributeByName(QName.valueOf("style")) != null;
            } else {
                return false;
            }
        }
    }

}
