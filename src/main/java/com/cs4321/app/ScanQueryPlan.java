package com.cs4321.app;

/**
 * The Scan Query Plan is a tree with the Scan operator as its root.
 * It has no children.
 *
 * @author Jessica Tweneboah
 */
public class ScanQueryPlan extends QueryPlan {

    /**
     * Constructor that initialises a ScanQueryPlan
     *
     * @param root The root scan operator of the ScanQueryPlan
     */
    public ScanQueryPlan(Operator root) {
        super(root);
    }

    /**
     * Evaluates the result of the Scan Query Plan.
     * Dumps the scan to the appropriate Print Stream or file
     */
    @Override
    public void evaluate() {
        getRoot().dump();
    }
}
