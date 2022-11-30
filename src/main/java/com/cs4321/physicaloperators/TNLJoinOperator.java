package com.cs4321.physicaloperators;

import java.util.List;

import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

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
            JoinExpressionVisitor visitor, List<Table> originalJoinOrder) {
        super(leftChild, rightChild, joinCondition, visitor, originalJoinOrder);
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
                return this.getTupleInOriginalOrder(this.leftTuple.concat(rightTuple));
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
}
