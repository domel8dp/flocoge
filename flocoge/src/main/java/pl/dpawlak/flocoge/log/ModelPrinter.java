package pl.dpawlak.flocoge.log;

import java.util.Collection;

import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

/**
 * Created by dpawlak on Mar 19, 2015
 */
public class ModelPrinter {

    public void print(Collection<ModelElement> model) {
        System.out.println("Diagram model:");
        for (ModelElement element : model) {
            System.out.println("START PATH");
            traverseBranch(element, 1);
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
