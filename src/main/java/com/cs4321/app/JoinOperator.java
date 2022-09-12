package com.cs4321.app;
import net.sf.jsqlparser.expression.Expression;

public class JoinOperator extends Operator {

    private Operator left;

    private Operator right;

    // captures the join condition
    private Expression expression;

    private Tuple leftTuple;

    @Override
    public Tuple getNextTuple() {
        // TODO Auto-generated method stub
        Tuple rightTuple = this.getNextTuple();
        return null;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        this.left.reset();
        this.right.reset();
    }

    @Override
    public void finalize() {
        this.left.finalize();
        this.right.finalize();
    }

}
