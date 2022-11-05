package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class SelectionOperator extends Operator {
    private final SelectExpressionVisitor visitor;
    private final AliasMap aliasMap;
    private final Expression exp;
    private final ScanOperator child;

    /**
     * Creates an Operator that will represent a particular SELECT statement
     * containing a WHERE clause.
     *
     * @param visitor  The ExpressionVisitor that will be used to determine whether
     *                 a row passes a condition
     * @param aliasMap The AliasMap representing a mapping from a column to the
     *                 index that column represents in a row
     * @param exp      The expression that is used to validate a row
     * @param child    The Scan Operator that will provide the rows in a column
     */
    public SelectionOperator(SelectExpressionVisitor visitor, AliasMap aliasMap, Expression exp,
                             ScanOperator child) {
        this.visitor = visitor;
        this.aliasMap = aliasMap;
        this.exp = exp;
        this.child = child;
        this.setColumnMap(child.getColumnMap());
    }

    /**
     * If possible, gets the next Tuple in its column
     *
     * @return The next Tuple in the column that passes the statement's expression;
     * null if no such Tuple exists
     */
    @Override
    public Tuple getNextTuple() {
        Tuple row = this.child.getNextTuple();
        while (row != null && !this.visitor.evalExpression(this.exp, row, this.aliasMap)) {
            row = this.child.getNextTuple();
        }
        return row;
    }

    /**
     * Resets the Select Operator such that it can start looking from the top of the
     * column again.
     */
    @Override
    public void reset() {
        this.child.reset();
    }

    /**
     * Returns the string representation of the Selection Operator formatted as
     * SelectionOperator{child, condition}
     *
     * @return The string representation of the Selection Operator
     */
    @Override
    public String toString() {
        return "SelectionOperator{" + this.child.toString() + ", " + this.exp.toString() + "}";
    }

    /**
     * Closes the query such that there cannot be subsequent calls to getNextTuple.
     */
    @Override
    public void finalize() {
        this.child.finalize();
    }

}
