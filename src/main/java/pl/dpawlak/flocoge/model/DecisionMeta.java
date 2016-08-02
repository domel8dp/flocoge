package pl.dpawlak.flocoge.model;

import java.util.HashSet;
import java.util.Set;

public class DecisionMeta {

    public final String decisionId;
    public final String[] mergePoints;
    public final Set<String> openDecissions;

    public DecisionMeta(String decisionId, int branchCount, Set<String> openDecissions) {
        this.decisionId = decisionId;
        this.openDecissions = new HashSet<>(openDecissions);
        mergePoints = new String[branchCount];
    }

    public boolean hasMergePoints() {
        for (String mergePoint : mergePoints) {
            if (mergePoint != null) {
                return true;
            }
        }
        return false;
    }
}
