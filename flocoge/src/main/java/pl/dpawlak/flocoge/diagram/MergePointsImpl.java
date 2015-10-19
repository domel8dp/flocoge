package pl.dpawlak.flocoge.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.dpawlak.flocoge.model.DecissionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelElement;

public class MergePointsImpl implements MergePoints {

    private final ModelElement node;
    private final String decisionId;
    private final int branchIndex;
    private final DecissionMeta decission;
    private final Map<String, Integer> currentBranches;

    private boolean valid;
    private String error;

    public MergePointsImpl(FlocogeModel model, ModelElement node, String decisionId, int branchIndex,
            Map<String, Integer> currentBranches) {
        this.node = node;
        this.decisionId = decisionId;
        this.branchIndex = branchIndex;
        this.currentBranches = currentBranches;
        decission = model.decissions.get(decisionId);
        valid = true;
    }

    @Override
    public boolean idInNode() {
        return node.branches.containsKey(decisionId);
    }

    @Override
    public boolean indexInNode() {
        return node.branches.get(decisionId).contains(branchIndex);
    }

    @Override
    public boolean allBranchesEqual() {
        Iterator<Map.Entry<String, Integer>> currentBranchesIterator = currentBranches.entrySet().iterator();
        while (currentBranchesIterator.hasNext()) {
            Map.Entry<String, Integer> branch = currentBranchesIterator.next();
            List<Integer> nodeBranchIndices = node.branches.get(branch.getKey());
            if (differentBranchIndices(branch, nodeBranchIndices)) {
                return false;
            }
        }
        return true;
    }

    private boolean differentBranchIndices(Map.Entry<String, Integer> branch, List<Integer> nodeBranchIndices) {
        return nodeBranchIndices != null &&
            (nodeBranchIndices.size() != 1 || !branch.getValue().equals(nodeBranchIndices.get(0)));
    }

    @Override
    public void loopDetected() {
        error = "Diagram error (element '" + node.label + "' is part of a loop)";
    }

    @Override
    public void markInvalid() {
        valid = false;
    }

    @Override
    public boolean mergePointExistsForThisIndex() {
        return decission.mergePoints[branchIndex] != null;
    }

    @Override
    public void saveIndexInNode() {
        node.branches.get(decisionId).add(branchIndex);
    }

    @Override
    public void saveDecisionIdInNode() {
        node.branches.put(decisionId, new ArrayList<Integer>(decission.mergePoints.length));
        saveIndexInNode();
    }

    @Override
    public boolean otherMergePointExists() {
        for (int index = 0; index < decission.mergePoints.length; index++) {
            if (index != branchIndex && decission.mergePoints[index] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sameMergePoint() {
        String otherMergePoint = null;
        for (int index = 0; index < decission.mergePoints.length; index++) {
            if (index != branchIndex && decission.mergePoints[index] != null) {
                otherMergePoint = decission.mergePoints[index];
            }
        }
        return node.id.equals(otherMergePoint);
    }

    @Override
    public void saveMergePointForBranch() {
        decission.mergePoints[branchIndex] = node.id;
    }

    @Override
    public void improperBranchingDetected() {
        error = "Diagram error (element '" + node.label + "' is part of a invalid branch)";
    }

    @Override
    public void saveMergePointOnBothBranches() {
        int otherBranchIndex = node.branches.get(decisionId).get(0);
        decission.mergePoints[branchIndex] = decission.mergePoints[otherBranchIndex] = node.id;
    }

    public boolean isValid() {
        return valid;
    }

    public String getError() {
        return error;
    }
}
