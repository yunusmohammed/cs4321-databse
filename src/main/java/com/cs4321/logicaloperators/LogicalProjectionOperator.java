package com.cs4321.logicaloperators;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.cs4321.app.ColumnMap;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
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
  private final Map<String, Integer> columnOrder;

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
    this.columnOrder = new HashMap<>();
    for (int i = 0; i < selectItems.size(); i++) {
      SelectItem item = selectItems.get(i);
      Expression exp = (((SelectExpressionItem) item).getExpression());
      Column c = (Column) exp;
      this.columnOrder.put(c.toString(), i);
    }
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

  /**
   * Get the column order of this logical project operator
   * 
   * @return The column order of this logical project operator
   */
  public Map<String, Integer> getColumnOrder() {
    return this.columnOrder;
  }

}
