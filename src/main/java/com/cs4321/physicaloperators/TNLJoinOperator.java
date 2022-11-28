package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for handling Joins using Tuple Nested Loop Join
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class TNLJoinOperator extends JoinOperator {

    /**
     * The current tuple of the left child the Operator is on
     */
    private Tuple leftTuple;

    /**
     * Base constructor of the TNLJoinOperator
     */
    public TNLJoinOperator() {
        super();
    }

    /**
     * Constructor for TNLJoinOperator
     *
     * @param leftChild     the left child operator of this join operator
     * @param rightChild    the right child operator of this join operator
     * @param joinCondition the condition to join rows on
     * @param visitor       the expression visitor of this join operator
     */
    public TNLJoinOperator(Operator leftChild, Operator rightChild, Expression joinCondition,
            JoinExpressionVisitor visitor) {
        super(leftChild, rightChild, joinCondition, visitor);
    }

    @Override
    public Tuple getNextTuple() {
        if (this.leftTuple == null)
            this.leftTuple = this.getLeftChild().getNextTuple();
        while (this.leftTuple != null) {
            Tuple rightTuple = this.getRightChild().getNextTuple();
            if (rightTuple == null) {
                this.leftTuple = this.getLeftChild().getNextTuple();
                this.getRightChild().reset();
            } else if (this.getJoinCondition() == null
                    || this.getVisitor().evalExpression(this.getJoinCondition(), leftTuple, rightTuple)) {
                return this.leftTuple.concat(rightTuple);
            }
        }
        System.out.println();
        return null;
    }

    @Override
    public void reset() {
        super.reset();
        this.leftTuple = null;
    }

    @Override
    public String toString() {
        String joinConditionString = (this.getJoinCondition() == null) ? "null" : this.getJoinCondition().toString();
        return "TNLJoinOperator{" + this.getLeftChild().toString() + ", " + this.getRightChild().toString() + ", "
                + joinConditionString + "}";
    }

    @Override
    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("-");
        }
        builder.append("TNLJ[" + this.getJoinCondition().toString() + "]");
        builder.append("\n");
        builder.append(this.getLeftChild().toString(level + 1));
        builder.append(this.getRightChild().toString(level + 1));
        return builder.toString();
    }
}
