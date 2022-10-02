package com.cs4321.logicaloperators;

/**
 * A Logical Duplicate Elimination Operator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalDuplicateEliminationOperator extends LogicalOperator {

  private LogicalOperator child;

  /**
   * Construct a logical duplicate elimination operator
   * 
   * @param child the logical child operator of this logical duplicate elimination
   *              operator
   */
  public LogicalDuplicateEliminationOperator(LogicalOperator child) {
    this.child = child;
  }

  /**
   * Get the logical child operator
   * 
   * @return The logical child operator
   */
  public LogicalOperator getChild() {
    return this.child;
  }
}
