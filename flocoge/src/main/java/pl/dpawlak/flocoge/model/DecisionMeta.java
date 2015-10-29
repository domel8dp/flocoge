package pl.dpawlak.flocoge.model;

public class DecisionMeta {

    public final String decisionId;
    public final String[] mergePoints;

    public DecisionMeta(String decisionId, int branchCount) {
        this.decisionId = decisionId;
        mergePoints = new String[branchCount];
    }
}
