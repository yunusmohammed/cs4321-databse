package com.cs4321.logicaloperators;

import com.cs4321.app.ColumnMap;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;
import com.cs4321.physicaloperators.SelectExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;

/**
 * A Logical Selection Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSelectionOperator extends LogicalOperator {
    private final Expression selectCondition;
    private final LogicalScanOperator child;
    private final SelectExpressionVisitor visitor;
    private final ColumnMap columnMap;

    /**
     * Creates a logical select operator
     *
     * @param selectCondition The selection condition of the operator
     * @param child           The child logical scan operator of the logical select
     *                        operator
     * @param visitor         The ExpressionVisitor that will be used to determine
     *                        whether a row passes a condition
     * @param columnMap       The ColumnMap representing a mapping from a column to
     *                        the index that column represents in a row
     */
    public LogicalSelectionOperator(Expression selectCondition, LogicalScanOperator child,
                                    SelectExpressionVisitor visitor, ColumnMap columnMap) {
        this.selectCondition = selectCondition;
        this.child = child;
        this.visitor = visitor;
        this.columnMap = columnMap;
    }

    /**
     * Get the child logical scan operator of the logical select
     *
     * @return The child logical scan operator of the logical select
     */
    public LogicalScanOperator getChild() {
        return this.child;
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
     * Get the ColumnMap of the logical select operator
     *
     * @return The ColumnMap of the logical select operator
     */
    public ColumnMap getColumnMap() {
        return this.columnMap;
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
