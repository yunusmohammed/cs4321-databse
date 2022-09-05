package com.cs4321.app;

public class SelectorQueryPlan extends QueryPlan{
    public SelectorQueryPlan(Operator root, ScanOperator child) {
        super(root, child);
    }

    public SelectorQueryPlan(Operator root, SelectOperator child) {
        super(root, child);
    }

    @Override
    public void evaluate() {
        // TODO @Lenhard
    }
}
