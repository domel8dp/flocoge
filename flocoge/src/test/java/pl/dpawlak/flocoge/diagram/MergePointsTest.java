package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import pl.dpawlak.flocoge.model.DecissionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelElement;

public class MergePointsTest {

    private static final String DECISION_ID = "a decission";
    private static final String OTHER_DECISION_ID = "other decission";
    private static final String ELEMENT_ID = "an element";
    private static final String OTHER_ELEMENT_ID = "other element";

    /*
     * inspect node without decision id -> save decision id and branch index in node
     */
    @Test
    public void inspectNewNode() {
        FlocogeModel model = modelWithDecissionMeta(1);
        ModelElement element = nodeWithVisitedBranches();
        MergePoints mergePoints = mergePoints(model, element, 0);

        new MergePointsFacade(mergePoints, null).inspectNode();

        assertEquals(Collections.singletonMap(DECISION_ID, Collections.singletonList(0)), element.branches);
    }

    /*
     * inspect node with identical branches -> mark node invalid (loop)
     */
    @Test
    public void inspectAlreadyVisitedNode() {
        FlocogeModel model = modelWithDecissionMeta(2);
        ModelElement element = nodeWithVisitedBranches(0);
        MergePointsImpl mergePoints = mergePoints(model, element, 0);

        new MergePointsFacade(mergePoints, null).inspectNode();

        assertFalse(mergePoints.isValid());
        assertTrue(mergePoints.getError().contains("is part of a loop"));
    }

    /*
     * inspect node with different branches -> do nothing
     */
    @Test
    public void inspectAlreadyVisitedNodeOnDifferentBranch() {
        FlocogeModel model = modelWithDecissionMeta(1);
        model.decissions.put(OTHER_DECISION_ID, new DecissionMeta(OTHER_DECISION_ID, 2));
        ModelElement element = nodeWithVisitedBranches(0);
        element.branches.put(OTHER_DECISION_ID, Arrays.asList(0));
        Map<String, Integer> currentBranches = new HashMap<>();
        currentBranches.put(DECISION_ID, 0);
        currentBranches.put(OTHER_DECISION_ID, 1);
        MergePointsImpl mergePoints = new MergePointsImpl(model, element, DECISION_ID, 0, currentBranches);

        new MergePointsFacade(mergePoints, null).inspectNode();

        assertTrue(mergePoints.isValid());
    }

    /*
     * inspect node with decision id and other branch index, without merge point in meta -> save merge point for both branches
     */
    @Test
    public void inspectNewMergePoint() {
        FlocogeModel model = modelWithDecissionMeta(3);
        ModelElement element = nodeWithVisitedBranches(0);
        MergePoints mergePoints = mergePoints(model, element, 2);

        new MergePointsFacade(mergePoints, null).inspectNode();
        DecissionMeta decissionMeta = model.decissions.get(DECISION_ID);

        assertEquals(ELEMENT_ID, decissionMeta.mergePoints[0]);
        assertNull(decissionMeta.mergePoints[1]);
        assertEquals(ELEMENT_ID, decissionMeta.mergePoints[2]);
    }

    /*
     * inspect node with decision id and other branch index, with same merge point in meta -> save merge point for this branch
     */
    @Test
    public void inspectExistingMergePoint() {
        FlocogeModel model = modelWithDecissionMeta(3);
        model.decissions.get(DECISION_ID).mergePoints[0] = ELEMENT_ID;
        model.decissions.get(DECISION_ID).mergePoints[2] = ELEMENT_ID;
        ModelElement element = nodeWithVisitedBranches(0, 2);
        MergePoints mergePoints = mergePoints(model, element, 1);

        new MergePointsFacade(mergePoints, null).inspectNode();
        DecissionMeta decissionMeta = model.decissions.get(DECISION_ID);

        assertEquals(ELEMENT_ID, decissionMeta.mergePoints[1]);
    }

    /*
     * inspect node with decision id and other branch index, with other merge point in meta -> mark node invalid (multiple merge points)
     */
    @Test
    public void inspectInvalidMergePoint() {
        FlocogeModel model = modelWithDecissionMeta(3);
        model.decissions.get(DECISION_ID).mergePoints[0] = OTHER_ELEMENT_ID;
        model.decissions.get(DECISION_ID).mergePoints[2] = OTHER_ELEMENT_ID;
        ModelElement element = nodeWithVisitedBranches(0, 2);
        MergePointsImpl mergePoints = mergePoints(model, element, 1);

        new MergePointsFacade(mergePoints, null).inspectNode();

        assertFalse(mergePoints.isValid());
        assertTrue(mergePoints.getError().contains("is part of an invalid branch"));
    }

    /*
     * inspect node with decision id and other branch index, branches are already merged -> save branch index in node
     */
    @Test
    public void inspectNodeOnAlreadyMergedBranch() {
        FlocogeModel model = modelWithDecissionMeta(2);
        model.decissions.get(DECISION_ID).mergePoints[0] = OTHER_ELEMENT_ID;
        model.decissions.get(DECISION_ID).mergePoints[1] = OTHER_ELEMENT_ID;
        ModelElement element = nodeWithVisitedBranches(0);
        MergePointsImpl mergePoints = mergePoints(model, element, 1);

        new MergePointsFacade(mergePoints, null).inspectNode();

        assertEquals(Collections.singletonMap(DECISION_ID, Arrays.asList(0, 1)), element.branches);
    }

    private FlocogeModel modelWithDecissionMeta(int branchCount) {
        FlocogeModel model = new FlocogeModel();
        DecissionMeta decissionMeta = new DecissionMeta(DECISION_ID, branchCount);
        model.decissions.put(DECISION_ID, decissionMeta);
        return model;
    }

    private ModelElement nodeWithVisitedBranches(Integer... branchIndices) {
        ModelElement element = new ModelElement();
        element.id = ELEMENT_ID;
        if (branchIndices.length > 0) {
            element.branches.put(DECISION_ID, new LinkedList<>(Arrays.asList(branchIndices)));
        }
        return element;
    }

    private MergePointsImpl mergePoints(FlocogeModel model, ModelElement node, int branchIndex) {
        return new MergePointsImpl(model, node, DECISION_ID, branchIndex, currentBranches(branchIndex));
    }

    private Map<String, Integer> currentBranches(int branchIndex) {
        return Collections.singletonMap(DECISION_ID, branchIndex);
    }
}
