package utils;

import com.cs4321.app.AliasMap;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.logicaloperators.LogicalJoinChild;
import com.cs4321.logicaloperators.LogicalJoinOperator;
import com.cs4321.logicaloperators.LogicalOperator;
import com.cs4321.logicaloperators.LogicalScanOperator;
import com.cs4321.logicaloperators.LogicalSelectionOperator;
import com.cs4321.logicaloperators.OldLogicalJoinOperator;
import com.cs4321.physicaloperators.IndexSelectionVisitor;
import com.cs4321.physicaloperators.JoinExpressionVisitor;
import com.cs4321.physicaloperators.JoinExpressions;
import com.cs4321.physicaloperators.SelectExpressionVisitor;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.*;
import java.util.stream.Collectors;

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
    public static Map<String, Integer> generateOldJoinTableOffsets(PlainSelect selectBody, AliasMap aliasMap) {
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
                    + DatabaseCatalog.getInstance().columnMap(aliasMap.getBaseTable(prevTable)).size();
            tableOffset.put(curTable, newOffset);
            prevOffset = newOffset;
            prevTable = curTable;
        }
        return tableOffset;
    }

    /**
     * Generates a map of offsets for column indices of tables in the results of
     * joins
     *
     * @param selectBody a select body containing the order in which tables are
     *                   joined
     * @return a map of column index offsets for tables after a join operation
     */
    private static Map<String, Integer> generateJoinTableOffsets(List<LogicalOperator> orderedJoinRelations,
            AliasMap aliasMap) {
        Map<String, Integer> tableOffset = new HashMap<>();
        int nextOffset = 0;

        for (LogicalOperator joinRelation : orderedJoinRelations) {
            LogicalJoinChild selectOrScan = (LogicalJoinChild) joinRelation;
            // default to use alias when an alias exists
            String curTable = selectOrScan.getTable().getAlias();
            curTable = (curTable != null) ? curTable : selectOrScan.getTable().getName();
            tableOffset.put(curTable, nextOffset);
            nextOffset += DatabaseCatalog.getInstance().columnMap(aliasMap.getBaseTable(curTable)).size();
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
    public static Map<String, Integer> getColumnIndex(PlainSelect selectBody, AliasMap aliasMap) {
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
                            .columnMap(aliasMap.getBaseTable(table));
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

    /**
     * Generates a logical join query plan
     *
     * @param selectBody The body of a select statement. The joins field must be non
     *                   null and not empty
     * @return the root of the logical join query plan
     */
    public static OldLogicalJoinOperator generateOldLogicalJoinTree(LogicalJoinOperator logicalJoin,
            AliasMap aliasMap) {
        OldLogicalJoinOperator root = new OldLogicalJoinOperator();
        List<Table> originalJoinOrder = logicalJoin.getChildren().stream().filter(elt -> elt != null)
                .map(elt -> ((LogicalJoinChild) elt).getTable()).collect(Collectors.toList());
        root.setOriginalJoinOrder(originalJoinOrder);
        OldLogicalJoinOperator currentParent = root;
        List<LogicalOperator> logicalJoinChildren = logicalJoin.getChildren();
        Map<String, Integer> tableOffset = LogicalQueryPlanUtils.generateJoinTableOffsets(logicalJoinChildren,
                aliasMap);
        Stack<BinaryExpression> expressions = LogicalQueryPlanUtils.getExpressions(logicalJoin.getWhereExpression());
        while (logicalJoinChildren.size() > 1) {
            LogicalOperator rightChildOperator = logicalJoinChildren.remove(logicalJoinChildren.size() - 1);
            LogicalJoinChild joinChild = (LogicalJoinChild) rightChildOperator;
            Table rightChildTable = joinChild.getTable();
            String rightChildTableName = rightChildTable.getAlias();
            rightChildTableName = (rightChildTableName != null) ? rightChildTableName : rightChildTable.getName();

            JoinExpressions joinExpressions = LogicalQueryPlanUtils.getJoinExpressions(expressions,
                    rightChildTableName);

            Stack<Expression> parentExpressions = joinExpressions.getParentExpressions();
            Stack<BinaryExpression> leftChildExpressions = joinExpressions.getLeftExpressions();
            // Stack<Expression> rightChildExpressions =
            // joinExpressions.getRightChildExpressions();
            expressions = leftChildExpressions;

            // Set Right Child of current Parent
            currentParent.setRightChild(rightChildOperator);
            currentParent.setAliasMap(aliasMap);

            // Set Join Condition of current parent
            currentParent.setJoinCondition(LogicalQueryPlanUtils.generateExpressionTree(parentExpressions));

            // Set ExpressionVisitor of current parent
            JoinExpressionVisitor visitor = new JoinExpressionVisitor(aliasMap, tableOffset,
                    rightChildTableName);
            currentParent.setJoinExpressionVisitor(visitor);

            // Set left child of current parent
            LogicalOperator leftOperator;
            if (logicalJoinChildren.size() == 1) {
                currentParent
                        .setLeftChild(getJoinChildOperator((Stack) leftChildExpressions,
                                ((LogicalJoinChild) logicalJoin.getChildren().get(0)).getTable(), aliasMap));
            } else {
                leftOperator = new OldLogicalJoinOperator();
                currentParent.setLeftChild(leftOperator);
                currentParent = (OldLogicalJoinOperator) leftOperator;
            }
        }
        return root;
    }

    /**
     * Generate a child operator evaluating childExpressions on childTable
     *
     * @param childExpressions stack of expressions to be evaluated by the child
     *                         operator
     * @param childTable       table corresponding to the child operator
     * @return the child operator
     */
    private static LogicalOperator getJoinChildOperator(Stack<Expression> childExpressions, FromItem childTable,
            AliasMap aliasMap) {
        LogicalOperator operator;
        PlainSelect selectBody = new PlainSelect();
        selectBody.setFromItem(childTable);
        if (childExpressions == null || childExpressions.size() == 0)
            operator = generateLogicalScan(selectBody, aliasMap);
        else {
            Expression rightChildExpression = LogicalQueryPlanUtils.generateExpressionTree(childExpressions);
            selectBody.setWhere(rightChildExpression);
            operator = generateLogicalSelection(selectBody, aliasMap);
        }
        return operator;
    }

    /**
     * Generate a new logical scan operator
     *
     * @param selectBody The body of the Select statement
     * @return The logical scan operator that was just created
     */
    private static LogicalScanOperator generateLogicalScan(PlainSelect selectBody, AliasMap aliasMap) {
        Table table = (Table) selectBody.getFromItem();
        return new LogicalScanOperator(table, aliasMap);
    }

    /**
     * Generate a new logical select operator
     *
     * @param selectBody The body of the Select statement
     * @return The logical select operator that was just created
     */
    private static LogicalSelectionOperator generateLogicalSelection(PlainSelect selectBody, AliasMap aliasMap) {
        Expression whereExpression = selectBody.getWhere();
        return new LogicalSelectionOperator(whereExpression, generateLogicalScan(selectBody, aliasMap),
                new SelectExpressionVisitor(),
                new IndexSelectionVisitor(), aliasMap);
    }
}
