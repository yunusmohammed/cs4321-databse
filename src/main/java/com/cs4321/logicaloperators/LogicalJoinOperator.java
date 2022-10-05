package com.cs4321.logicaloperators;

import com.cs4321.physicaloperators.JoinExpressionVisitor;

import net.sf.jsqlparser.expression.Expression;

/**
 * A Logical Join Operator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalJoinOperator extends LogicalOperator {

  private LogicalOperator leftChild;
  private LogicalOperator rightChild;
  private Expression joinCondition;
  private JoinExpressionVisitor visitor;

  /**
   * Base constructor for the LogicalJoinOperator
   */
  public LogicalJoinOperator() {

  }

  /**
   * Constructor for LogicalJoinOperator
   * 
   * @param leftChild      the logical left child operator of this logical join
   *                       operator
   * @param rightChild     the logical right child operator of this logical join
   *                       operator
   * @param joinConditionn the condition to join rows on
   */
  public LogicalJoinOperator(LogicalOperator leftChild, LogicalOperator rightChild, Expression joinCondition,
      JoinExpressionVisitor visitor) {
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.joinCondition = joinCondition;
    this.visitor = visitor;
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
}
