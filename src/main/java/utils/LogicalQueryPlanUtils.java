package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.cs4321.app.ColumnMap;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.JoinExpressions;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * Contains utility functions for the logical query plan
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalQueryPlanUtils {

  /**
   * Generates a map of offsets for column indices of tables in the results of
   * joins
   *
   * @param selectBody a select body containing the order in which tables are
   *                   joined
   * @return a map of column index offsets for tables after a join operation
   */
  public static Map<String, Integer> generateJoinTableOffsets(PlainSelect selectBody, ColumnMap columnMap) {
    Map<String, Integer> tableOffset = new HashMap<>();
    List<Join> joins = selectBody.getJoins();
    int prevOffset = 0;
    String prevTable = ((Table) selectBody.getFromItem()).getAlias();
    prevTable = (prevTable != null) ? prevTable : ((Table) selectBody.getFromItem()).getName();
    tableOffset.put(prevTable, prevOffset);
    for (Join join : joins) {
      // default to use alias when an alias exists
      String curTable = ((Table) join.getRightItem()).getAlias();
      curTable = (curTable != null) ? curTable : ((Table) join.getRightItem()).getName();
      int newOffset = prevOffset
          + DatabaseCatalog.getInstance().columnMap(columnMap.getBaseTable(prevTable)).size();
      tableOffset.put(curTable, newOffset);
      prevOffset = newOffset;
      prevTable = curTable;
    }
    return tableOffset;
  }

  /**
   * Decouple expression into all component binary expressions that are not AND
   * expressions
   *
   * @param expression the expression to decouple
   * @return a stack of the decoupled expressions
   */
  public static Stack<BinaryExpression> getExpressions(Expression expression) {
    Stack<BinaryExpression> expressions = new Stack<>();
    if (expression == null)
      return expressions;
    Stack<Expression> stack = new Stack<>();
    stack.add(expression);
    while (stack.size() > 0) {
      Expression exp = stack.pop();
      if (!(exp instanceof AndExpression))
        expressions.add((BinaryExpression) exp);
      else {
        stack.add(((AndExpression) exp).getLeftExpression());
        stack.add(((AndExpression) exp).getRightExpression());
      }
    }
    return expressions;
  }

  /**
   * Distributes expressions among a join operator and its children
   *
   * @param expressions         expressions to be distribuited among the join
   *                            operator
   *                            and its children
   * @param rightChildTableName table corresponding to the right child of the Join
   *                            Operator
   * @return a JoinExpressions intance representing the result of the distribution
   */
  public static JoinExpressions getJoinExpressions(Stack<BinaryExpression> expressions, String rightChildTableName) {
    Stack<Expression> rightChildExpressions = new Stack<>();
    Stack<Expression> parentExpressions = new Stack<>();
    Stack<BinaryExpression> leftChildExpressions = new Stack<>();

    // Separate expression meant for left child, right child, and parent
    for (BinaryExpression exp : expressions) {
      String leftTable = null;
      String rightTable = null;
      if (exp.getLeftExpression() instanceof Column)
        leftTable = ((Column) exp.getLeftExpression()).getTable().getName();
      if (exp.getRightExpression() instanceof Column)
        rightTable = ((Column) exp.getRightExpression()).getTable().getName();

      if (((leftTable != null && leftTable.equals(rightChildTableName))
          && (rightTable == null || rightTable.equals(rightChildTableName))) ||
          ((rightTable != null && rightTable.equals(rightChildTableName))
              && (leftTable == null || leftTable.equals(rightChildTableName)))) {
        // expression references only the columns from the right child's table
        rightChildExpressions.add(exp);

      } else if ((leftTable == null && rightTable == null)
          || (leftTable != null && leftTable.equals(rightChildTableName)
              && rightTable != null && !rightTable.equals(rightChildTableName))
          || (leftTable != null && !leftTable.equals(rightChildTableName)
              && rightTable != null && rightTable.equals(rightChildTableName))) {
        // expression references no tables at all OR references columns from the rigth
        // child's table and some other
        // tables in the left child
        parentExpressions.add(exp);
      } else
        leftChildExpressions.add(exp);
    }

    return new JoinExpressions(parentExpressions, rightChildExpressions, leftChildExpressions);
  }

  /**
   * Conjoins a stack of expressions to build an expression
   * tree
   *
   * @param expressions a stack of expressions to conjoin
   * @return root of the expression tree from conjoining expressions
   */
  public static Expression generateExpressionTree(Stack<Expression> expressions) {
    if (expressions == null || expressions.size() == 0)
      return null;
    while (expressions.size() >= 2) {
      Expression leftExpression = expressions.pop();
      Expression rightExpression = expressions.pop();
      AndExpression exp = new AndExpression(leftExpression, rightExpression);
      expressions.add(exp);
    }
    return expressions.pop();
  }

  /**
   * Creates a mapping from columns names in the select clause to indexes in a
   * corresponding tuple.
   * 
   * @param selectBody- The body of the select statement.
   * @return- A Map from column names to indexes in a tuple.
   */
  public static Map<String, Integer> getColumnIndex(PlainSelect selectBody, ColumnMap columnMap) {
    int curIndex = 0;
    HashMap<String, Integer> columnIndex = new HashMap<>();
    for (Object selectItem : selectBody.getSelectItems()) {
      if (selectItem instanceof AllColumns) {
        // * with potential join
        String fromItem = selectBody.getFromItem().toString();
        if (selectBody.getFromItem().getAlias() != null)
          fromItem = selectBody.getFromItem().getAlias();
        List<Join> joins = selectBody.getJoins();
        List<String> tableNames = new ArrayList<>();
        tableNames.add(fromItem);
        if (joins != null && joins.size() > 0) {
          for (Join join : joins) {
            if (join.getRightItem().getAlias() != null)
              tableNames.add(join.getRightItem().getAlias());
            else
              tableNames.add(join.getRightItem().toString());
          }
        }
        for (String table : tableNames) {
          Map<String, Integer> mapping = DatabaseCatalog.getInstance()
              .columnMap(columnMap.getBaseTable(table));
          for (String column : mapping.keySet()) {
            columnIndex.put(table + "." + column, mapping.get(column) + curIndex);
          }
          curIndex += mapping.size();
        }
      } else {
        columnIndex.put(selectItem.toString(), curIndex++);
      }
    }
    return columnIndex;
  }
}
