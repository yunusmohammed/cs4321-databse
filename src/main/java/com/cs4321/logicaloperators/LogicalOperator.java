package com.cs4321.logicaloperators;

import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;

/**
 * Base abstract Logical Operator class
 *
 * @author Yunus Mohammed (ymm26@cornell.edu)
 */
public abstract class LogicalOperator {

    public Operator accept(PhysicalPlanBuilder builder) {
        return null;
    }

    /**
     * Returns a formatted string of this logical operator with a new line character
     * at the end
     * 
     * @param level the depth of the logical operator in the logical query plan tree
     * @return a formatted string of this logical operator with a new line character
     *         at the end
     */
    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        builder.append("LogicalOperator");
        builder.append("\n");
        return builder.toString();
    }
}
