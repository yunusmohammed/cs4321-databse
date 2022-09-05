package com.cs4321.app;

/**
 * @author Jessica Tweneboah
 */
public class ScanQueryPlan extends QueryPlan{
    public ScanQueryPlan(Operator root) {
        super(root);
    }

    @Override
    public void evaluate() {
        getRoot().dump();
    }
}
