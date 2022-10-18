package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import com.cs4321.physicaloperators.Operator;

public class DuplicateEliminationOperator extends Operator {

    private Operator child;
    private Tuple prevTuple;

    /**
     * Constructs an operator to represent a distinct clause. This operator is
     * always at the root of the query plan.
     * 
     * @param child- the rest of the query plan.
     */
    public DuplicateEliminationOperator(Operator child) {
        this.child = child;
    }

    /**
     * Returns the next unique tuple. Assumes the child returns tuples in sorted
     * order.
     * 
     * @return- the next unique tuple.
     */
    @Override
    public Tuple getNextTuple() {
        Tuple nextTuple = child.getNextTuple();
        while (nextTuple != null && nextTuple.equals(prevTuple)) {
            nextTuple = child.getNextTuple();
        }
        prevTuple = nextTuple;
        return nextTuple;
    }

    /**
     * Resets the operator so that it can read tuples from the beginning.
     */
    @Override
    public void reset() {
        child.reset();
        prevTuple = null;
    }

    /**
     * Returns a string representation of the duplicate elimination operator and its children.
     * @return - a string representation of the duplicate elimination operator.
     */
    @Override
    public String toString() {
        return "DuplicateEliminationOperator{" + child.toString() + "}";
    }

    /**
     * Closes the query so that there cannot be more calls to getNextTuple.
     */
    @Override
    public void finalize() {
        child.finalize();
    }

}
