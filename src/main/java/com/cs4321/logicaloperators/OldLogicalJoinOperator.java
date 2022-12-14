package com.cs4321.logicaloperators;

import java.util.List;

import com.cs4321.app.AliasMap;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.physicaloperators.JoinExpressionVisitor;
import com.cs4321.physicaloperators.Operator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

/**
 * A Logical Join Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class OldLogicalJoinOperator extends LogicalOperator {

    private LogicalOperator leftChild;
    private LogicalOperator rightChild;
    private Expression joinCondition;
    private JoinExpressionVisitor visitor;
    private List<Table> originalJoinOrder;
    private AliasMap aliasMap;

    /**
     * Base constructor for the LogicalJoinOperator
     */
    public OldLogicalJoinOperator() {

    }

    /**
     * Constructor for LogicalJoinOperator
     *
     * @param leftChild     the logical left child operator of this logical join
     *                      operator
     * @param rightChild    the logical right child operator of this logical join
     *                      operator
     * @param joinCondition the condition to join rows on
     */
    public OldLogicalJoinOperator(LogicalOperator leftChild, LogicalOperator rightChild, Expression joinCondition,
            JoinExpressionVisitor visitor, List<Table> originalJoinOrder, AliasMap aliasMap) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.joinCondition = joinCondition;
        this.visitor = visitor;
        this.originalJoinOrder = originalJoinOrder;
        this.aliasMap = aliasMap;
    }

    /**
     * Get the left child of this logical join operator
     *
     * @return The left child of this logical join operator
     */
    public LogicalOperator getLeftChild() {
        return this.leftChild;
    }

    /**
     * Get the right child of this logical join operator
     *
     * @return The right child of this logical join operator
     */
    public LogicalOperator getRightChild() {
        return this.rightChild;
    }

    /**
     * Get the join condition of this logical join operator
     *
     * @return The join condition of this logical join operator
     */
    public Expression getJoinCondition() {
        return this.joinCondition;
    }

    /**
     * Get the join exression visitor of this logical join operator
     *
     * @return The join condition of this logical join operator
     */
    public JoinExpressionVisitor getJoinExpressionVisitor() {
        return this.visitor;
    }

    public List<Table> getOriginalJoinOrder() {
        return this.originalJoinOrder;
    }

    /**
     * Get the alias map of this logical join operator
     * 
     * @return the alias map of this logical join operator
     */
    public AliasMap getAliasMap() {
        return this.aliasMap;
    }

    /**
     * Set the left child of this logical join operator
     */
    public void setLeftChild(LogicalOperator leftChild) {
        this.leftChild = leftChild;
    }

    /**
     * Set the right child of this logical join operator
     */
    public void setRightChild(LogicalOperator rightChild) {
        this.rightChild = rightChild;
    }

    /**
     * Set the join condition of this logical join operator
     */
    public void setJoinCondition(Expression joinCondition) {
        this.joinCondition = joinCondition;
    }

    /**
     * Set the join exression visitor of this logical join operator
     */
    public void setJoinExpressionVisitor(JoinExpressionVisitor visitor) {
        this.visitor = visitor;
    }

    /**
     * Set the original join order of this logical join operator
     */
    public void setOriginalJoinOrder(List<Table> originalJoinOrder) {
        this.originalJoinOrder = originalJoinOrder;
    }

    /**
     * Set the alias map of this logical join operator
     */
    public void setAliasMap(AliasMap aliasMap) {
        this.aliasMap = aliasMap;
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
}
