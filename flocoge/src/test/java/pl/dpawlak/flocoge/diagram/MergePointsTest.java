package pl.dpawlak.flocoge.diagram;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import pl.dpawlak.flocoge.log.util.ErrorCollectingLogger;
import pl.dpawlak.flocoge.model.DecisionMeta;
import pl.dpawlak.flocoge.model.FlocogeModel;
import pl.dpawlak.flocoge.model.ModelElement;

public class MergePointsTest {

    private static final String DECISION_ID = "a decision";
    private static final String OTHER_DECISION_ID = "other decision";
    private static final String ELEMENT_ID = "an element";
    private static final String OTHER_ELEMENT_ID = "other element";

    private InspectionContext context;
    private ErrorCollectingLogger logger;
    private MergePointsImpl mergePoints;

    /*
     * inspect node without decision id -> save decision id and branch index in node
     */
    @Test
    public void inspectNewNode() {
        FlocogeModel model = modelWithDecisionMeta(1);
        ModelElement element = nodeWithVisitedBranches();
        initMergePoints(model, element, 0);

        new MergePointsFacade(mergePoints).inspectNode();

        assertEquals(Collections.singletonMap(DECISION_ID, Collections.singletonList(0)), element.branches);
    }

    /*
     * inspect node with identical branches -> mark node invalid (loop)
     */
    @Test
    public void inspectAlreadyVisitedNode() {
        FlocogeModel model = modelWithDecisionMeta(2);
        ModelElement element = nodeWithVisitedBranches(0);
        initMergePoints(model, element, 0);

        new MergePointsFacade(mergePoints).inspectNode();

        assertFalse(context.isValid());
        assertTrue(logger.getError().contains("is part of a loop"));
    }

    /*
     * inspect node with different branches -> do nothing
     */
    @Test
    public void inspectAlreadyVisitedNodeOnDifferentBranch() {
        FlocogeModel model = modelWithDecisionMeta(1);
        context = new InspectionContext(model);
        model.decisions.put(OTHER_DECISION_ID, new DecisionMeta(OTHER_DECISION_ID, 2));
        context.setElement(nodeWithVisitedBranches(0));
        context.getBranches().put(OTHER_DECISION_ID, Arrays.asList(0));
        Map<String, Integer> currentBranches = new HashMap<>();
        currentBranches.put(DECISION_ID, 0);
        currentBranches.put(OTHER_DECISION_ID, 1);
        context.setCurrentBranches(currentBranches);
        logger = new ErrorCollectingLogger();
        mergePoints = new MergePointsImpl(logger, context, DECISION_ID, 0);

        new MergePointsFacade(mergePoints).inspectNode();

        assertTrue(context.isValid());
    }

    /*
     * inspect node with decision id and other branch index, without merge point in meta -> save merge point for both branches
     */
    @Test
    public void inspectNewMergePoint() {
        FlocogeModel model = modelWithDecisionMeta(3);
        ModelElement element = nodeWithVisitedBranches(0);
        initMergePoints(model, element, 2);

        new MergePointsFacade(mergePoints).inspectNode();
        DecisionMeta decisionMeta = model.decisions.get(DECISION_ID);

        assertEquals(ELEMENT_ID, decisionMeta.mergePoints[0]);
        assertNull(decisionMeta.mergePoints[1]);
        assertEquals(ELEMENT_ID, decisionMeta.mergePoints[2]);
    }

    /*
     * inspect node with decision id and other branch index, with same merge point in meta -> save merge point for this branch
     */
    @Test
    public void inspectExistingMergePoint() {
        FlocogeModel model = modelWithDecisionMeta(3);
        model.decisions.get(DECISION_ID).mergePoints[0] = ELEMENT_ID;
        model.decisions.get(DECISION_ID).mergePoints[2] = ELEMENT_ID;
        ModelElement element = nodeWithVisitedBranches(0, 2);
        initMergePoints(model, element, 1);

        new MergePointsFacade(mergePoints).inspectNode();
        DecisionMeta decisionMeta = model.decisions.get(DECISION_ID);

        assertEquals(ELEMENT_ID, decisionMeta.mergePoints[1]);
    }

    /*
     * inspect node with decision id and other branch index, with other merge point in meta -> mark node invalid (multiple merge points)
     */
    @Test
    public void inspectInvalidMergePoint() {
        FlocogeModel model = modelWithDecisionMeta(3);
        model.decisions.get(DECISION_ID).mergePoints[0] = OTHER_ELEMENT_ID;
        model.decisions.get(DECISION_ID).mergePoints[2] = OTHER_ELEMENT_ID;
        ModelElement element = nodeWithVisitedBranches(0, 2);
        initMergePoints(model, element, 1);

        new MergePointsFacade(mergePoints).inspectNode();

        assertFalse(context.isValid());
        assertTrue(logger.getError().contains("is part of an invalid branch"));
    }

    /*
     * inspect node with decision id and other branch index, branches are already merged -> save branch index in node
     */
    @Test
    public void inspectNodeOnAlreadyMergedBranch() {
        FlocogeModel model = modelWithDecisionMeta(2);
        model.decisions.get(DECISION_ID).mergePoints[0] = OTHER_ELEMENT_ID;
        model.decisions.get(DECISION_ID).mergePoints[1] = OTHER_ELEMENT_ID;
        ModelElement element = nodeWithVisitedBranches(0);
        initMergePoints(model, element, 1);

        new MergePointsFacade(mergePoints).inspectNode();

        assertEquals(Collections.singletonMap(DECISION_ID, Arrays.asList(0, 1)), element.branches);
    }

    private FlocogeModel modelWithDecisionMeta(int branchCount) {
        FlocogeModel model = new FlocogeModel();
        DecisionMeta decisionMeta = new DecisionMeta(DECISION_ID, branchCount);
        model.decisions.put(DECISION_ID, decisionMeta);
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

    private void initMergePoints(FlocogeModel model, ModelElement element, int branchIndex) {
        context = new InspectionContext(model);
        context.setElement(element);
        context.setCurrentBranches(currentBranches(branchIndex));
        logger = new ErrorCollectingLogger();
        mergePoints = new MergePointsImpl(logger, context, DECISION_ID, branchIndex);
    }

    private Map<String, Integer> currentBranches(int branchIndex) {
        return Collections.singletonMap(DECISION_ID, branchIndex);
    }
}
