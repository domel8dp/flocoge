package pl.dpawlak.flocoge.diagram;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelElementParser {

    public enum ElementType {ELEMENT, CONNECTION, LABEL, UNKNOWN}

    private ModelElement modelElement;
    private Connection connection;
    private Label label;

    public ModelElementParser() { }

    public ElementType parseNextElement(StartElement element) throws DiagramLoadingException {
        clearInternalState();
        String style = element.getAttributeByName(QName.valueOf("style")).getValue();
        ElementType type = checkType(element, style);
        switch (type) {
            case ELEMENT:
                parseElement(element, style);
                break;
            case CONNECTION:
                parseConnection(element);
                break;
            case LABEL:
                parseLabel(element);
                break;
            default:
                break;
        }
        return type;
    }

    public ModelElement getModelElement() {
        return modelElement;
    }

    public Connection getConnection() {
        return connection;
    }

    public Label getLabel() {
        return label;
    }

    private void clearInternalState() {
        modelElement = null;
        connection = null;
        label = null;
    }

    private ElementType checkType(StartElement element, String style) {
        if (style.contains("shape=")) {
            return ElementType.ELEMENT;
        } else if (style.contains("edgeStyle=")) {
            return ElementType.CONNECTION;
        } else if (style.contains("text;")) {
            return ElementType.LABEL;
        } else {
            return ElementType.UNKNOWN;
        }
    }

    private void parseElement(StartElement element, String style) throws DiagramLoadingException {
        modelElement = new ModelElement();
        setAttribute(modelElement, "id", element, "id");
        setAttribute(modelElement, "label", element, "value");
        setElementType(modelElement, style);
    }

    private void parseConnection(StartElement element) throws DiagramLoadingException {
        connection = new Connection();
        setAttribute(connection, "id", element, "id");
        setAttribute(connection, "sourceId", element, "source");
        setAttribute(connection, "targetId", element, "target");
        setAttribute(connection, "label", element, "value");
    }

    private void parseLabel(StartElement element) throws DiagramLoadingException {
        label = new Label();
        setAttribute(label, "parentId", element, "parent");
        setAttribute(label, "value", element, "value");
    }

    private void setAttribute(Object object, String fieldName, StartElement element, String attributeName)
            throws DiagramLoadingException {
        Attribute attribute = element.getAttributeByName(QName.valueOf(attributeName));
        if (attribute != null) {
            try {
                object.getClass().getField(fieldName).set(object, attribute.getValue());
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                throw new DiagramLoadingException(ex);
            }
        }
    }

    private void setElementType(ModelElement modelElement, String style) {
        if (style.contains("shape=mxgraph.flowchart.manual_operation;") ||
                style.contains("shape=mxgraph.flowchart.data;")) {
            modelElement.shape = Shape.EVENT;
        } else if (style.contains("shape=mxgraph.flowchart.process;") ||
                style.contains("shape=mxgraph.flowchart.predefined_process;") ||
                style.contains("shape=mxgraph.flowchart.preparation;") ||
                style.contains("shape=mxgraph.flowchart.database;")) {
            modelElement.shape = Shape.OPERATION;
        } else if (style.contains("shape=mxgraph.flowchart.decision;")) {
            modelElement.shape = Shape.DECISION;
        } else if (style.contains("shape=mxgraph.flowchart.on-page_reference;")) {
            modelElement.shape = Shape.ON_PAGE_REF;
        } else if (style.contains("shape=mxgraph.flowchart.off-page_reference;")) {
            modelElement.shape = Shape.OFF_PAGE_REF;
        } else {
            modelElement.shape = Shape.SKIP;
        }
    }

    public static class Connection {
        public String id;
        public String sourceId;
        public String targetId;
        public String label;
    }

    public static class Label {
        public String parentId;
        public String value;
    }
}
