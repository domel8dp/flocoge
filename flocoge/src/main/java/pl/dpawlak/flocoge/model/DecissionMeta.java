package pl.dpawlak.flocoge.model;

public class DecissionMeta {

    public final String decisionId;
    public final String[] mergePoints;

    public DecissionMeta(String decisionId, int branchCount) {
        this.decisionId = decisionId;
        mergePoints = new String[branchCount];
    }
}
