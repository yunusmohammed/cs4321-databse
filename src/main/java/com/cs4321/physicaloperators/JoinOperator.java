package com.cs4321.physicaloperators;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.List;
import java.util.Map;

import com.cs4321.app.AliasMap;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Tuple;

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

    private List<Table> originalJoinOrder;

    private Map<String, Integer> oldTableOffsets;

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
            JoinExpressionVisitor visitor, List<Table> originalJoinOrder, Map<String, Integer> oldTableOffsets) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.joinCondition = joinCondition;
        this.visitor = visitor;
        this.originalJoinOrder = originalJoinOrder;
        Map<String, Integer> columnMap = leftChild.getColumnMap();
        Map<String, Integer> offset = getTableOffsets();
        addChildrenColumnMap(rightChild, offset, columnMap);
        this.setColumnMap(columnMap);
        this.oldTableOffsets = oldTableOffsets;
    }

    /**
     * Creates half of the column mapping of this node using the mappings of its
     * child as well as the offset
     * applied to that particular child.
     *
     * @param child  The child whose mapping is being used to reconstruct the new
     *               join column mapping.
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
        return tableOffset.get(tableName) + this.visitor.getAliasMap().get(column);
    }

    /**
     * Get the original order of relations of this join operator
     *
     * @return the original order of relations of this join operator
     */
    public List<Table> getOriginalJoinOrder() {
        return this.originalJoinOrder;
    }

    public Tuple getTupleInOriginalOrder(Tuple tuple) {
        if (this.getOriginalJoinOrder() == null) {
            return tuple;
        }
        Tuple originalOrderedTuple = new Tuple("");
        List<Table> originalOrder = this.getOriginalJoinOrder();
        Map<String, Integer> tableOffSets = this.getVisitor().getTableOffsets();
        for (Table table : originalOrder) {
            String curTable = table.getAlias();
            curTable = (curTable != null) ? curTable : table.getName();
            originalOrderedTuple = originalOrderedTuple
                    .concat(tuple.get(tableOffSets.get(curTable), tableOffSets.get(curTable)
                            + DatabaseCatalog.getInstance().columnMap(this.getAliasMap().getBaseTable(curTable))
                                    .size()));
        }
        return originalOrderedTuple;
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

    public Map<String, Integer> getOldTableOffsets() {
        return this.oldTableOffsets;
    }

    /**
     * Return the alias map of this join operator
     * 
     * @return the alias map of this join operator
     */
    public AliasMap getAliasMap() {
        return getVisitor().getAliasMap();
    }

    /**
     * Returns the string version of the join condition
     * 
     * @return the string version of the join condition
     */
    public String getJoinConditionString() {
        String joinConditionString = "";
        if (this.getJoinCondition() != null)
            joinConditionString = this.getJoinCondition().toString();
        return joinConditionString;
    }

}
