package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;

/**
 * Operator for handling distinct clauses
 * 
 * @author Yohanes
 */
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
        this.setColumnMap(child.getColumnMap());
    }

    /**
     * Returns the child of this Physical Duplicate Elimination operator
     */
    public Operator getChild() {
        return this.child;
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
     * Returns a string representation of the duplicate elimination operator and its
     * children.
     *
     * @return - a string representation of the duplicate elimination operator.
     */
    @Override
    public String toString() {
        return "DuplicateEliminationOperator{" + child.toString() + "}";
    }

    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        builder.append("DupElim");
        builder.append("\n");
        builder.append(this.getChild().toString(level + 1));
        return builder.toString();
    }

    /**
     * Closes the query so that there cannot be more calls to getNextTuple.
     */
    @Override
    public void finalize() {
        child.finalize();
    }

}
