package pl.dpawlak.flocoge.log;

import java.util.Map;

import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelPrinter {

    public void print(FlocogeModel model) {
        System.out.println("Diagram model:");
        for (Map.Entry<String, ModelElement> path : model.startElements.entrySet()) {
            System.out.println("START PATH: " + path.getKey());
            traverseBranch(path.getValue(), 1);
        }
    }

    private void traverseBranch(ModelElement startElement, int level) {
        ModelElement element = startElement;
        while (element != null) {
            indent(level);
            printElement(element);
            if (element.shape == Shape.DECISION) {
                for (ModelConnection connection : element.connections) {
                    indent(level);
                    System.out.println("  BRANCH: " + connection.label);
                    traverseBranch(connection.target, level + 1);
                }
                element = null;
            } else {
                element = getNextElement(element);
            }
        }
    }

    private void indent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }
    }

    private void printElement(ModelElement element) {
        System.out.print(element.shape.name());
        System.out.print(", label: ");
        System.out.print(element.label);
        System.out.print(", id: ");
        System.out.println(element.id);
    }

    private ModelElement getNextElement(ModelElement element) {
        int connectionsCount = element.connections.size();
        return connectionsCount > 0 ? element.connections.get(0).target : null;
    }
}
