package com.cs4321.logicaloperators;

import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.Operator;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.List;
import java.util.Map;

/**
 * A Logical Sort Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSortOperator extends LogicalOperator {

    private final LogicalOperator child;
    private final List<OrderByElement> orderByElementList;
    private final Map<String, Integer> sortColumnMap;

    /**
     * Constructor of the logical sort operator
     *
     * @param child           The logical child operator
     * @param sortColumnMap   A map from column names in the table to their
     *                        associated indexes.
     * @param orderByElements the list of elements for which our order by clause
     *                        will sort
     */
    public LogicalSortOperator(LogicalOperator child, Map<String, Integer> sortColumnMap,
            List<OrderByElement> orderByElements) {
        this.child = child;
        this.orderByElementList = orderByElements;
        this.sortColumnMap = sortColumnMap;
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
     * Get the list of elements for which our order by clause
     * will sort
     *
     * @return The list of elements for which our order by clause
     *         will sort
     */
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElementList;
    }

    /**
     * Get the sort columb map
     *
     * @return The sort columb map
     */
    public Map<String, Integer> getSortColumnMap() {
        return this.sortColumnMap;
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
        String sortOrder = "[]";
        if(this.orderByElementList != null) sortOrder = this.orderByElementList.toString();
        builder.append("Sort" + sortOrder);
        builder.append("\n");
        builder.append(this.getChild().toString(level + 1));
        return builder.toString();
    }
}
