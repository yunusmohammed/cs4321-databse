package com.cs4321.logicaloperators;

import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * A Logical Sort Operator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSortOperator extends LogicalOperator {

  private final LogicalOperator child;
  private final List<OrderByElement> orderByElementList;

  /**
   * Constructor of the logical sort operator
   * 
   * @param child           The logical child operator
   * @param orderByElements the list of elements for which our order by clause
   *                        will sort
   */
  public LogicalSortOperator(LogicalOperator child, List<OrderByElement> orderByElements) {
    this.child = child;
    this.orderByElementList = orderByElements;
  }

  /**
   * Get the logical child operator
   * 
   * @return The logical child operator
   */
  public LogicalOperator getChild() {
    return this.child;
  }

  /**
   * Get the list of elements for which our order by clause
   * will sort
   * 
   * @return The list of elements for which our order by clause
   *         will sort
   */
  public List<OrderByElement> getOrderByElements() {
    return this.orderByElementList;
  }
}
