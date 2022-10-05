package com.cs4321.logicaloperators;

import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;

/**
 * A Logical Scan Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalScanOperator extends LogicalOperator {
    private String baseTable;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    public LogicalScanOperator(String baseTable) {
        this.baseTable = baseTable;
    }

    /**
     * Get the table in the database the ScanOperator is scanning
     *
     * @return The table in the database the ScanOperator is scanning
     */
    public String getBaseTable() {
        return this.baseTable;
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
}
