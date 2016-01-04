package pl.dpawlak.flocoge.diagram;

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
import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

public class ModelLoader {

    private final XMLInputFactory factory;
    private final Logger log;
    private final ModelElementParser parser;
    private final List<Label> labels;
    private final Map<String, Connection> connections;

    private FlocogeModel model;

    public ModelLoader(XMLInputFactory factory, Logger log) {
        this.factory = factory;
        this.log = log;
        parser = new ModelElementParser();
        labels = new LinkedList<>();
        connections = new LinkedHashMap<>();
    }

    public void loadModel(FlocogeModel model, XMLEventReader reader, StartElement rootElement)
            throws DiagramLoadingException {
        log.log("Loading model from diagram...");
        log.trace("==================");
        this.model = model;
        parseElements(reader, rootElement);
        log.trace("==================");
        assignLabelsToConnections();
        connectElements();
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
        log.trace("Parsing: {} ...", element);
        switch (parser.parseNextElement(element)) {
            case ELEMENT:
                ModelElement modelElement = parser.getModelElement();
                log.trace("Parsed {}(label: '{}', id: {})", modelElement.shape.name(), modelElement.label,
                    modelElement.id);
                model.elements.put(modelElement.id, modelElement);
                break;
            case CONNECTION:
                Connection connection = parser.getConnection();
                log.trace("Parsed connection(label: '{}', source id: {}, target id: {})", connection.label,
                    connection.sourceId, connection.targetId);
                connections.put(connection.id, connection);
                break;
            case LABEL:
                Label label = parser.getLabel();
                log.trace("Parsed label(value: '{}', parent id: {})", label.value, label.parentId);
                labels.add(label);
                break;
            default:
                log.trace("Element type unknown");
                break;
        }
    }

    private void assignLabelsToConnections() {
        log.trace("Assigning labels to connections...");
        for (Label label : labels) {
            Connection connection = connections.get(label.parentId);
            if (connection != null) {
                connection.label = label.value;
            }
        }
        labels.clear();
    }

    private void connectElements() {
        log.trace("Connecting elements...");
        Map<String, ModelElement> startElements = new LinkedHashMap<>(model.elements);
        for (Connection connection : connections.values()) {
            ModelElement sourceElement = model.elements.get(connection.sourceId);
            if (sourceElement != null) {
                ModelElement targetElement = startElements.remove(connection.targetId);
                if (targetElement == null) {
                    targetElement = model.elements.get(connection.targetId);
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
        model.startElements.putAll(startElements);
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
