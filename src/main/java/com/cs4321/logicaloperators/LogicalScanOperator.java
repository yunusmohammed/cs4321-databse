package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;
import net.sf.jsqlparser.schema.Table;

/**
 * A Logical Scan Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalScanOperator extends LogicalOperator {
    private Table table;
    private AliasMap aliasMap;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param table    The table in the database the ScanOperator is scanning
     * @param aliasMap The mapping from table names to base table names
     */
    public LogicalScanOperator(Table table, AliasMap aliasMap) {
        this.table = table;
        this.aliasMap = aliasMap;
    }

    /**
     * Get the table in the database the ScanOperator is scanning
     *
     * @return The table in the database the ScanOperator is scanning
     */
    public Table getTable() {
        return this.table;
    }

    /**
     * Gets the alias map that the scan operator uses to detect aliases.
     *
     * @return The alias map
     */
    public AliasMap getAliasMap() {
        return aliasMap;
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
     * Returns the string representation of this logical scan operator
     */
    @Override
    public String toString() {
        return "Leaf[" + this.getTable().getName() + "]";
    }
}
