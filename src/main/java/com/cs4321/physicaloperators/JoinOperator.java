package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.Map;

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

    /**
     * Base constructor of the JoinOperator
     */
    public JoinOperator() {

    }

    /**
     * Constructor for JoinOperator
     *
     * @param leftChild     the left child operator of this join operator
     * @param rightChild    the right child operator of this join operator
     * @param joinCondition the condition to join rows on
     * @param visitor       the expression visitor of this join operator
     */
    public JoinOperator(Operator leftChild, Operator rightChild, Expression joinCondition,
            JoinExpressionVisitor visitor) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.joinCondition = joinCondition;
        this.visitor = visitor;
    }

    public int getColumnIndex(Column column) {
        Map<String, Integer> tableOffset = this.visitor.getTableOffsets();
        String tableName = column.getTable().getAlias();
        tableName = (tableName != null) ? tableName : column.getTable().getName();
        return tableOffset.get(tableName) + this.visitor.getColumnMap().get(column);
    }

    @Override
    public Tuple getNextTuple() {
        if (this.leftTuple == null)
            this.leftTuple = this.leftChild.getNextTuple();
        while (this.leftTuple != null) {
            Tuple rightTuple = this.rightChild.getNextTuple();
            if (rightTuple == null) {
                this.leftTuple = this.leftChild.getNextTuple();
                this.rightChild.reset();
            } else if (this.joinCondition == null
                    || this.visitor.evalExpression(this.joinCondition, leftTuple, rightTuple)) {
                return this.leftTuple.concat(rightTuple);
            }
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
    public String toString() {
        return "JoinOperator{" + this.leftChild.toString() + ", " + this.rightChild.toString() + "}";
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
     * Gets the left child of this join operator
     *
     * @return The left child of this join
     */
    public Operator getLeftChild() {
        return this.leftChild;
    }

    /**
     * Gets the right child of this join operator
     *
     * @return The right child of this join
     */
    public Operator getRightChild() {
        return this.rightChild;
    }

    /**
     * Sets the join condition of this join operator
     *
     * @param joinCondition the join condition of this operator
     */
    public void setJoinCondition(Expression joinCondition) {
        this.joinCondition = joinCondition;
    }

    /**
     * Sets the expression visitor of this join operator
     *
     * @param visitor the expression visitor of this join operator
     */
    public void setVisitor(JoinExpressionVisitor visitor) {
        this.visitor = visitor;
    }

    /**
     * Gets a map of offsets to be applied to column indices of table columns in
     * order to correctly index columns in joined rows
     *
     * @return the table column index offsets
     */
    public Map<String, Integer> getTableOffsets() {
        return this.visitor.getTableOffsets();
    }

}
