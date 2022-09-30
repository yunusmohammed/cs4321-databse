package com.cs4321.logicaloperators;

import java.util.List;

import com.cs4321.app.ColumnMap;

import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * A Logical Projection Operator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalProjectionOperator extends LogicalOperator {

  private final List<SelectItem> selectItems;
  private final LogicalOperator child;
  private final ColumnMap columnMap;

  /**
   * Constructor for the logical projection operator
   * 
   * @param selectItems The list of columns that will be chosen from a row
   * @param child       The logical child operator of this logical projection
   *                    operator
   * @param columnMap   A ColumnMap instance for alias resolution
   */
  public LogicalProjectionOperator(List<SelectItem> selectItems, LogicalOperator child, ColumnMap columnMap) {
    this.selectItems = selectItems;
    this.child = child;
    this.columnMap = columnMap;
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

  /**
   * Get the ColumnMap of this logical operator
   * 
   * @return The ColumnMap of this logical operator
   */
  public ColumnMap getColumnMap() {
    return this.columnMap;
  }

}
