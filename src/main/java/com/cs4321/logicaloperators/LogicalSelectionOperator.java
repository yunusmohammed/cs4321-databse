package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.IndexSelectionVisitor;
import com.cs4321.physicaloperators.Operator;
import com.cs4321.physicaloperators.SelectExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

/**
 * A Logical Selection Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSelectionOperator extends LogicalOperator implements LogicalJoinChild {
    private final Expression selectCondition;
    private final LogicalOperator child;
    private final SelectExpressionVisitor visitor;
    private final IndexSelectionVisitor indexVisitor;
    private final AliasMap aliasMap;

    /**
     * Creates a logical select operator
     *
     * @param selectCondition The selection condition of the operator
     * @param child           The child logical scan operator of the logical select
     *                        operator
     * @param visitor         The ExpressionVisitor that will be used to determine
     *                        whether a row passes a condition
     * @param indexVisitor    The ExpressionVisitor used to split an expression into
     *                        those
     *                        that can and cannot be indexed
     * @param aliasMap        A AliasMap instance for alias resolution
     */
    public LogicalSelectionOperator(Expression selectCondition, LogicalOperator child,
                                    SelectExpressionVisitor visitor, IndexSelectionVisitor indexVisitor,
                                    AliasMap aliasMap) {
        this.selectCondition = selectCondition;
        this.child = child;
        this.visitor = visitor;
        this.indexVisitor = indexVisitor;
        this.aliasMap = aliasMap;
    }

    /**
     * Get the child logical scan operator of the logical select
     *
     * @return The child logical scan operator of the logical select
     */
    public LogicalOperator getChild() {
        return this.child;
    }

    /**
     * Get the table in the database the child of this SelectOperator is scanning
     * 
     * @return the table in the database the child of this SelectOperator is
     *         scanning
     */
    public Table getTable() {
        return ((LogicalScanOperator) this.getChild()).getTable();
    }

    /**
     * Get the selection condition of the operator
     *
     * @return The selection condition of the operator
     */
    public Expression getSelectCondition() {
        return this.selectCondition;
    }

    /**
     * Get expression visitor of the logical select operator
     *
     * @return The select expression visitor of the logical select operator
     */
    public SelectExpressionVisitor getSelectExpressionVisitor() {
        return this.visitor;
    }

    /**
     * Get the IndexSelectionVisitor of the logical select operator
     *
     * @return The IndexSelectionVisitor of the logical select operator
     */
    public IndexSelectionVisitor getIndexVisitor() {
        return this.indexVisitor;
    }

    /**
     * Gets the base table name of the logical selection operator.
     *
     * @return The base table name of the logical selection operator.
     */
    public String getBaseTableName() {
        Table t = ((LogicalScanOperator) this.child).getTable();
        return aliasMap.getBaseTable(t.getName());
    }

    /**
     * Get the AliasMap of the logical select operator
     *
     * @return The AliasMap of the logical select operator
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

    @Override
    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        String selectItems = "";
        if(this.getSelectCondition() != null) selectItems = this.getSelectCondition().toString();
        builder.append("Select[" + this.getSelectCondition().toString() + "]");
        builder.append("\n");
        builder.append(this.getChild().toString(level + 1));
        return builder.toString();
    }
}
