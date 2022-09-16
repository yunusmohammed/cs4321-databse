package com.cs4321.app;

import java.util.HashMap;

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
    private Operator leftChild;

    /**
     * The right child of the Operator
     */
    private Operator rightChild;

    /**
     * The join condition
     */
    private Expression joinCondition;

    /**
     * ExpressionVisitor for the JoinOperator
     */
    private JoinExpressionVisitor visitor;

    /**
     * The current tuple of the left child the Operator is on
     */
    private Tuple leftTuple;

    public JoinOperator(JoinExpressionVisitor visitor, Operator leftChild, Operator rightChild,
            Expression joinCondition) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.joinCondition = joinCondition;
        this.visitor = visitor;
    }

    public JoinOperator() {

    }

    @Override
    public Tuple getNextTuple() {
        if (this.leftTuple == null)
            this.leftChild.getNextTuple();
        while (this.leftTuple != null) {
            Tuple rightTuple = this.rightChild.getNextTuple();
            if (rightTuple == null)
                this.leftTuple = this.leftChild.getNextTuple();
            else if (this.visitor.evalExpression(this.joinCondition, leftTuple, rightTuple))
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

    /**
     * Sets the left child of this join operator
     * 
     * @param leftChild the left child of this operator
     */
    public void setLeftChild(Operator leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * Sets the right child of this join operator
     * 
     * @param rightChild the right child of this operator
     */
    public void setRightChild(Operator rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * Sets the join condition of this join operator
     * 
     * @param rightChild the join condition of this operator
     */
    public void setJoinCondition(Expression joinCondition) {
        this.joinCondition = joinCondition;
    }

    /**
     * Sets the expression visitor of this join operator
     * 
     * @param rightChild the expression of this operator
     */
    public void setVisitor(JoinExpressionVisitor visitor) {
        this.visitor = visitor;
    }

    public HashMap<String, Integer> getTableOffsets() {
        return this.visitor.getTableOffsets();
    }

}
