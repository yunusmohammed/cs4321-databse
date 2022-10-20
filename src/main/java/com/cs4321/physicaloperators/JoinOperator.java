package com.cs4321.physicaloperators;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for join operators
 *
 * @author Yunus (ymm26@cornell.edu)
 */

public abstract class JoinOperator extends Operator {
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
        Map<String, Integer> columnMap = new HashMap<>();
        Map<String, Integer> offset = getTableOffsets();
        addChildrenColumnMap(leftChild, offset, columnMap);
        addChildrenColumnMap(rightChild, offset, columnMap);
        this.setColumnMap(columnMap);
    }

    /**
     * Creates half of the column mapping of this node using the mappings of its child as well as the offset
     * applied to that particular child.
     *
     * @param child  The child whose mapping is being used to reconstruct the new join column mapping.
     * @param offset The mapping from alias/table name to index offset
     */
    private void addChildrenColumnMap(Operator child, Map<String, Integer> offset, Map<String, Integer> columnMap) {
        for (Map.Entry<String, Integer> entry : child.getColumnMap().entrySet()) {
            String column = entry.getKey();
            String tableName = column.split("\\.")[0];
            Integer index = entry.getValue();
            columnMap.put(column, index + offset.get(tableName));
        }
    }

    /**
     * Get the index of a column in the output tuple of this join operator
     *
     * @return the index of a column in the output tuple of this join operator
     */
    public int getColumnIndex(Column column) {
        Map<String, Integer> tableOffset = this.visitor.getTableOffsets();
        String tableName = column.getTable().getAlias();
        tableName = (tableName != null) ? tableName : column.getTable().getName();
        return tableOffset.get(tableName) + this.visitor.getColumnMap().get(column);
    }

    @Override
    public void reset() {
        this.leftChild.reset();
        this.rightChild.reset();
    }

    @Override
    public String toString() {
        String joinConditionString = (this.joinCondition == null) ? "null" : this.joinCondition.toString();
        return "JoinOperator{" + this.leftChild.toString() + ", " + this.rightChild.toString() + ", "
                + joinConditionString + "}";
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
     * Gets the left child of this join operator
     *
     * @return The left child of this join
     */
    public Operator getLeftChild() {
        return this.leftChild;
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
     * Gets the join condition of this join operator
     *
     * @returns joinCondition the join condition of this operator
     */
    public Expression getJoinCondition() {
        return this.joinCondition;
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
     * Gets the expression visitor of this join operator
     *
     * @return visitor the expression visitor of this join operator
     */
    public JoinExpressionVisitor getVisitor() {
        return this.visitor;
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
