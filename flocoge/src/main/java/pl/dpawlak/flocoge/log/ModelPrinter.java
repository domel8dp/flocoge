package pl.dpawlak.flocoge.log;

import java.util.Map;

import pl.dpawlak.flocoge.model.DecisionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelConnection;
import pl.dpawlak.flocoge.model.ModelElement;
import pl.dpawlak.flocoge.model.ModelElement.Shape;

public class ModelPrinter {

    private FlocogeModel model;

    public void print(FlocogeModel model) {
        this.model = model;
        System.out.println("==============");
        System.out.println("Transformed model:");
        for (Map.Entry<String, ModelElement> path : model.startElements.entrySet()) {
            printPath(path);
            traverseBranch(path.getValue(), 1, null);
        }
        System.out.println("==============");
    }

    private void printPath(Map.Entry<String, ModelElement> path) {
        switch (path.getValue().shape) {
            case ON_PAGE_REF:
                System.out.print("private: ");
                break;
            case OFF_PAGE_REF:
                System.out.print("public external: ");
                break;
            default:
                System.out.print("public: ");
                break;
        }
        System.out.println(path.getKey());
    }

    private ModelElement traverseBranch(ModelElement startElement, int level, String mergePoint) {
        ModelElement element = startElement;
        while (element != null && (mergePoint == null || !element.id.equals(mergePoint))) {
            printElement(element, level);
            if (element.shape == Shape.DECISION) {
                element = traverseDecisionBranches(element, level + 1, mergePoint);
            } else {
                element = getNextElement(element);
            }
        }
        return element;
    }

    private void printElement(ModelElement element, int level) {
        indent(level);
        System.out.print(element.shape.name().toLowerCase());
        System.out.print(": ");
        System.out.println(element.label);
    }

    private void indent(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }
    }

    private ModelElement traverseDecisionBranches(ModelElement element, int level, String currentMergePoint) {
        DecisionMeta meta = model.decisions.get(element.id);
        int index = 0;
        ModelElement finalElement = null;
        for (ModelConnection connection : element.connections) {
            String branchMergePoint = meta.mergePoints[index++];
            printBranch(connection, branchMergePoint, level);
            String mergePoint = branchMergePoint != null ? branchMergePoint : currentMergePoint;
            ModelElement branchFinalElement = traverseBranch(connection.target, level + 1, mergePoint);
            if (finalElement == null && (mergePoint != null || meta.hasMergePoints())) {
                printReturn(level + 1);
            }
            if (branchFinalElement != null && finalElement == null) {
                finalElement = branchFinalElement;
            }
        }
        return finalElement;
    }

    private void printBranch(ModelConnection connection, String mergePoint, int level) {
        indent(level);
        System.out.print("branch: ");
        System.out.println(connection.label);
    }

    private void printReturn(int level) {
        indent(level);
        System.out.println("*exit*");
    }

    private ModelElement getNextElement(ModelElement element) {
        return element.connections.isEmpty() ? null : element.connections.get(0).target;
    }
}
