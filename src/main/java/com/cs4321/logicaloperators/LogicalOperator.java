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
}
