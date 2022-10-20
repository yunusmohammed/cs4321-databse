package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Logical Projection Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalProjectionOperator extends LogicalOperator {

    private final List<SelectItem> selectItems;
    private final LogicalOperator child;
    private final Map<String, Integer> columnOrder;
    private final AliasMap aliasMap;

    /**
     * Constructor for the logical projection operator
     *
     * @param selectItems The list of columns that will be chosen from a row
     * @param child       The logical child operator of this logical projection
     *                    operator
     * @param aliasMap    A AliasMap instance for alias resolution
     */
    public LogicalProjectionOperator(List<SelectItem> selectItems, LogicalOperator child, AliasMap aliasMap) {
        this.selectItems = selectItems;
        this.child = child;
        this.columnOrder = new HashMap<>();
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem item = selectItems.get(i);
            Expression exp = (((SelectExpressionItem) item).getExpression());
            Column c = (Column) exp;
            String tableName = c.getTable().getAlias();
            if (tableName == null) tableName = c.getTable().getName();
            this.columnOrder.put(tableName + "." + c.getColumnName(), i);
        }
        this.aliasMap = aliasMap;
    }

    /**
     * Get the logical child operator of this logical projection operator
     *
     * @return The logical child operator of this logical projection operator
     */
    public LogicalOperator getChild() {
        return this.child;
    }

    /**
     * Get the selectItems of this logical operator
     *
     * @return The selectItems of this logical operator
     */
    public List<SelectItem> getSelectItems() {
        return this.selectItems;
    }

    /**
     * Get the column map of this logical operator
     *
     * @return The column map of this logical operator
     */
    public Map<String, Integer> getColumnOrder() {
        return this.columnOrder;
    }

    /**
     * Get the AliasMap of this logical operator
     *
     * @return The AliasMap of this logical operator
     */
    public AliasMap getAliasMap() {
        return this.aliasMap;
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
