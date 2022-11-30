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
    private String tableName;
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
        String tableName = table.getAlias();
        if (tableName == null) tableName = table.getName();
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
     * Returns the alias or table name if no alias exists for the base table
     * @return the alias or table name
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * Returns the base table name
     * @return the base table name
     */
    public String getBaseTableName() {
        return aliasMap.getBaseTable(tableName);
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

    @Override
    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        builder.append("Leaf[" + this.getTable().getName() + "]");
        builder.append("\n");
        return builder.toString();
    }
}
