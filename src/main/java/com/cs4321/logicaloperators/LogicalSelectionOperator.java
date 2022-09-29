package com.cs4321.logicaloperators;

import net.sf.jsqlparser.expression.Expression;

/**
 * A Logical Select Operator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSelectionOperator extends LogicalOperator {
  private final Expression selectCondition;
  private final LogicalScanOperator child;

  /**
   * Creates a logical select operator
   * 
   * @param selectCondition The selection condition of the operator
   * @param child           The child logical scan operator of the logical select
   *                        operator
   */
  public LogicalSelectionOperator(Expression selectCondition, LogicalScanOperator child) {
    this.selectCondition = selectCondition;
    this.child = child;
  }

  /**
   * Get the child logical scan operator of the logical select
   * 
   * @return The child logical scan operator of the logical select
   */
  public LogicalScanOperator getChildOperator() {
    return this.child;
  }

  /**
   * Get the selection condition of the operator
   * 
   * @return The selection condition of the operator
   */
  public Expression getSelectCondition() {
    return this.selectCondition;
  }
}
