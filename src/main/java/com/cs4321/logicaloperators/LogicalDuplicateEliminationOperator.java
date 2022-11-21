package com.cs4321.logicaloperators;

import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;

/**
 * A Logical Duplicate Elimination Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalDuplicateEliminationOperator extends LogicalOperator {

    private LogicalOperator child;

    /**
     * Construct a logical duplicate elimination operator
     *
     * @param child the logical child operator of this logical duplicate elimination
     *              operator
     */
    public LogicalDuplicateEliminationOperator(LogicalOperator child) {
        this.child = child;
    }

    /**
     * Get the logical child operator
     *
     * @return The logical child operator
     */
    public LogicalOperator getChild() {
        return this.child;
    }

    /**
     * Accepts the builder to traverse this operator.
     *
     * @param builder The builder that will traverse this operator.
     * @return The phyiscal tree that this logical operator represents.
     */
    @Override
    public Operator accept(PhysicalPlanBuilder builder) {
        return builder.visit(this);
    }

    /**
     * Returns the string representation of this logical duplicate elimation
     * operator
     */
    @Override
    public String toString() {
        return "DupElim";
    }
}
