package com.cs4321.logicaloperators;

import java.util.List;

import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * A Logical Projection Operator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalProjectionOperator extends LogicalOperator {

  private final List<SelectItem> selectItems;
  private final LogicalOperator child;

  /**
   * Constructor for the logical projection operator
   * 
   * @param selectItems The list of columns that will be chosen from a row
   * @param child       The logical child operator of this logical projection
   *                    operator
   */
  public LogicalProjectionOperator(List<SelectItem> selectItems, LogicalOperator child) {
    this.selectItems = selectItems;
    this.child = child;
  }

  /**
   * Get the logical child operator of this logical projection operator
   * 
   * @return The logical child operator of this logical projection operator
   */
  public LogicalOperator getChild() {
    return this.child;
  }

  /**
   * Get the selectItems of this logical operator
   * 
   * @return The selectItems of this logical operator
   */
  public List<SelectItem> getSelectItems() {
    return this.selectItems;
  }

}
