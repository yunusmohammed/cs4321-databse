package com.cs4321.logicaloperators;

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
  public LogicalJoinOperator(LogicalOperator leftChild, LogicalOperator rightChild, Expression joinCondition) {
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.joinCondition = joinCondition;
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
}
