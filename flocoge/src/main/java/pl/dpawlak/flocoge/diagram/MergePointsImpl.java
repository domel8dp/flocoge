package pl.dpawlak.flocoge.diagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.dpawlak.flocoge.log.Logger;
import pl.dpawlak.flocoge.model.DecisionMeta;

public class MergePointsImpl implements MergePoints {

    private final Logger log;
    private final InspectionContext context;
    private final String decisionId;
    private final int branchIndex;
    private final DecisionMeta decision;
    private final Map<String, List<Integer>> branches;

    public MergePointsImpl(Logger log, InspectionContext context, String decisionId, int branchIndex) {
        this.log = log;
        this.context = context;
        this.decisionId = decisionId;
        this.branchIndex = branchIndex;
        decision = context.getDecisionMeta(decisionId);
        branches = context.getBranches();
    }

    @Override
    public boolean idInNode() {
        return branches.containsKey(decisionId);
    }

    @Override
    public boolean indexInNode() {
        return branches.get(decisionId).contains(branchIndex);
    }

    @Override
    public boolean allBranchesEqual() {
        Iterator<Map.Entry<String, Integer>> currentBranchesIterator = context.getCurrentBranchesIterator();
        while (currentBranchesIterator.hasNext()) {
            Map.Entry<String, Integer> branch = currentBranchesIterator.next();
            List<Integer> nodeBranchIndices = branches.get(branch.getKey());
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
        log.error("Diagram error (element '{}' is part of a loop)", context.getLabel());
    }

    @Override
    public void markInvalid() {
        context.markInvalid();
    }

    @Override
    public boolean mergePointExistsForThisIndex() {
        return decision.mergePoints[branchIndex] != null;
    }

    @Override
    public void saveIndexInNode() {
        branches.get(decisionId).add(branchIndex);
    }

    @Override
    public void saveDecisionIdInNode() {
        branches.put(decisionId, new ArrayList<Integer>(decision.mergePoints.length));
        saveIndexInNode();
    }

    @Override
    public boolean otherMergePointExists() {
        for (int index = 0; index < decision.mergePoints.length; index++) {
            if (index != branchIndex && decision.mergePoints[index] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean sameMergePoint() {
        String otherMergePoint = null;
        for (int index = 0; index < decision.mergePoints.length; index++) {
            if (index != branchIndex && decision.mergePoints[index] != null) {
                otherMergePoint = decision.mergePoints[index];
            }
        }
        return context.getId().equals(otherMergePoint);
    }

    @Override
    public void saveMergePointForBranch() {
        decision.mergePoints[branchIndex] = context.getId();
    }

    @Override
    public void improperBranchingDetected() {
        log.error("Diagram error (element '{}' is part of an invalid branch)", context.getLabel());
    }

    @Override
    public void saveMergePointOnBothBranches() {
        int otherBranchIndex = branches.get(decisionId).get(0);
        decision.mergePoints[branchIndex] = decision.mergePoints[otherBranchIndex] = context.getId();
    }
}
