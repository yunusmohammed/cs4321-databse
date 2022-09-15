package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for handling Joins
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class JoinOperator extends Operator {

    /**
     * The left child of the Operator
     */
    private final Operator leftChild;

    /**
     * The right child of the Operator
     */
    private final Operator rightChild;

    /**
     * The join condition
     */
    private final Expression expression;

    /**
     * ExpressionVisitor for the JoinOperator
     */
    private final JoinExpressionVisitor visitor;

    /**
     * The current tuple of the left child the Operator is on
     */
    private Tuple leftTuple;

    public JoinOperator(JoinExpressionVisitor visitor, Operator leftChild, Operator rightChild, Expression expression) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.expression = expression;
        this.visitor = visitor;
    }

    @Override
    public Tuple getNextTuple() {
        if (this.leftTuple == null)
            this.leftChild.getNextTuple();
        while (this.leftTuple != null) {
            Tuple rightTuple = this.rightChild.getNextTuple();
            if (rightTuple == null)
                this.leftTuple = this.leftChild.getNextTuple();
            else if (this.visitor.evalExpression(this.expression, leftTuple, rightTuple))
                return this.leftTuple.concat(rightTuple);
        }
        return null;
    }

    @Override
    public void reset() {
        this.leftChild.reset();
        this.rightChild.reset();
        this.leftTuple = null;
    }

    @Override
    public void finalize() {
        this.leftChild.finalize();
        this.rightChild.finalize();
    }

}
