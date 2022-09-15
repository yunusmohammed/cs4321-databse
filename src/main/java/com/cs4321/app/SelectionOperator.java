package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;

import java.util.Map;

public class SelectionOperator extends Operator {
    private final SelectExpressionVisitor visitor;
    private final Map<String, Integer> columnMap;
    private final Expression exp;
    private final ScanOperator child;

    /**
     * Creates an Operator that will represent a particular SELECT statement containing a WHERE clause.
     *
     * @param visitor   The ExpressionVisitor that will be used to determine whether a row passes a condition
     * @param columnMap The mapping from a table's column name to the index that column represents in a row
     * @param exp       The expression that is used to validate a row
     * @param child     The Scan Operator that will provide the rows in a column
     */
    public SelectionOperator(SelectExpressionVisitor visitor, Map<String, Integer> columnMap, Expression exp, ScanOperator child) {
        this.visitor = visitor;
        this.columnMap = columnMap;
        this.exp = exp;
        this.child = child;
    }

    /**
     * If possible, gets the next Tuple in its column
     *
     * @return The next Tuple in the column that passes the statement's expression; null if no such Tuple exists
     */
    @Override
    public Tuple getNextTuple() {
        Tuple row = this.child.getNextTuple();
        while (row != null && !this.visitor.evalExpression(this.exp, row, this.columnMap)) {
            row = this.child.getNextTuple();
        }
        return row;
    }

    /**
     * Resets the Select Operator such that it can start looking from the top of the column again.
     */
    @Override
    public void reset() {
        this.child.reset();
    }

    /**
     * Closes the query such that there cannot be subsequent calls to getNextTuple.
     */
    @Override
    public void finalize() {
        this.child.finalize();
    }

}
