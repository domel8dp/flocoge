package pl.dpawlak.flocoge.log;

import java.util.HashSet;
import java.util.Set;

import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;

public class BareModelPrinter implements ModelPrinter {

    private final Set<String> printedIds;

    public BareModelPrinter() {
        printedIds = new HashSet<>();
    }

    @Override
    public void print(FlocogeModel model) {
        System.out.println("Bare model:");
        System.out.println("==================");
        for (ModelElement startElement : model.startElements.values()) {
            System.out.println("* start path");
            traverseBranch(startElement, 1);
            System.out.println();
        }
        System.out.println("==================");
    }

    private void traverseBranch(ModelElement startElement, int level) {
        ModelElement element = startElement;
        while (element != null) {
            printElement(element, level);
            printedIds.add(element.id);
            if (hasMultipleConnections(element)) {
                for (ModelConnection connection : element.connections) {
                    element = startBranch(connection, level + 1);
                    traverseBranch(element, level + 2);
                }
                element = null;
            } else {
                element = moveToNext(element, level);
            }
        }
    }

    private void printElement(ModelElement element, int level) {
        indent(level);
        System.out.print(element.shape.name().toLowerCase());
        System.out.print("(label: '");
        System.out.print(element.label);
        System.out.print("', id: ");
        System.out.print(element.id);
        System.out.println(")");
    }

    private void indent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }
    }

    private boolean hasMultipleConnections(ModelElement element) {
        return element.connections.size() > 1;
    }

    private ModelElement startBranch(ModelConnection connection, int level) {
        if (connection.label != null && connection.label.trim().length() > 0) {
            printConnection(connection, level);
        } else {
            indent(level);
            System.out.println("connection(label: '')");
        }
        if (connection.target == null) {
            return printAndReturnPathEnd(level + 1);
        } else if (printedIds.contains(connection.target.id)) {
            return printAndReturnMergePoint(connection, level + 1);
        } else {
            return connection.target;
        }
    }

    private void printConnection(ModelConnection connection, int level) {
        indent(level);
        System.out.print("connection(label: '");
        System.out.print(connection.label);
        System.out.println("')");
    }

    private ModelElement printAndReturnPathEnd(int level) {
        indent(level);
        System.out.println("* end path");
        return null;
    }

    private ModelElement printAndReturnMergePoint(ModelConnection connection, int level) {
        indent(level);
        System.out.print("* connection to element(id: ");
        System.out.print(connection.target.id);
        System.out.println("')");
        return null;
    }

    private ModelElement moveToNext(ModelElement element, int level) {
        if (!element.connections.isEmpty()) {
            ModelConnection connection = element.connections.get(0);
            if (connection.label != null && connection.label.trim().length() > 0) {
                printConnection(connection, level);
            }
            if (connection.target == null) {
                return printAndReturnPathEnd(level);
            } else if (printedIds.contains(connection.target.id)) {
                return printAndReturnMergePoint(connection, level);
            } else {
                return connection.target;
            }
        } else {
            return printAndReturnPathEnd(level);
        }
    }
}
