package com.cs4321.physicaloperators;

import com.cs4321.app.*;
import com.cs4321.logicaloperators.LogicalOperator;
import com.cs4321.logicaloperators.LogicalQueryPlan;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * The QueryPlan is a tree of operators. A QueryPlan is constructed for each
 * Statement
 * and returned to the interpreter, so it can read the results of the QueryPlan
 *
 * @author Jessica Tweneboah, Yunus Mohammed (ymm26@cornell.edu)
 */
public class QueryPlan {
    private Operator root;
    private static final SelectExpressionVisitor visitor = new SelectExpressionVisitor();
    private static final String sep = File.separator;
    private String queryOutputName;
    private ColumnMap columnMap;

    private Logger logger = Logger.getInstance();

    private int queryNumber;

    /**
     * Evaluates SQL query statements
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting
     *                    at 1)
     */
    private void evaluateQueries(Statement statement, int queryNumber) {
        if (statement != null) {

            // need to implement physical plan builder to use logical query plan
            LogicalQueryPlan logicalPlan = new LogicalQueryPlan(statement);
            LogicalOperator logicalRoot = logicalPlan.getRoot();
            this.queryNumber = queryNumber;

            Select select = (Select) statement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();

            List<SelectItem> selectItemsList = selectBody.getSelectItems();
            SelectItem firstSelectItem = selectItemsList.get(0);
            FromItem fromItem = selectBody.getFromItem();
            List<Join> joinsList = selectBody.getJoins();
            List<OrderByElement> orderByElementsList = selectBody.getOrderByElements();
            Expression whereExpression = selectBody.getWhere();
            Distinct distinct = selectBody.getDistinct();

            this.columnMap = new ColumnMap(fromItem, joinsList);

            if ("*".equals(firstSelectItem.toString()) && joinsList == null && whereExpression == null) {
                this.root = generateScan(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns
                    && (joinsList == null || joinsList.size() == 0)
                    && whereExpression != null) {
                this.root = generateSelection(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns
                    && joinsList != null && joinsList.size() > 0) {
                this.root = generateJoin(selectBody);
            } else {
                // TODO: Add conditions for other operators @Yohannes @Lenhard
                this.root = generateProjection(selectBody);
            }

            boolean ordered = false;
            if (orderByElementsList != null && orderByElementsList.size() > 0) {
                this.root = generateSort(selectBody);
                ordered = true;
            }
            if (distinct != null) {
                // DuplicateEliminationOperator expects sorted child
                if (!ordered)
                    this.root = generateSort(selectBody);
                this.root = generateDistinct();
            }
        }
    }

    /**
     * Generate a new scan operator
     *
     * @param selectBody The body of the Select statement
     * @return The scan operator that was just created
     */
    private ScanOperator generateScan(PlainSelect selectBody) {
        String baseTable = selectBody.getFromItem().toString();
        if (selectBody.getFromItem().getAlias() != null)
            baseTable = columnMap.getBaseTable(selectBody.getFromItem().getAlias());
        return new ScanOperator(baseTable);
    }

    /**
     * Generate a new select operator and makes it the root
     *
     * @param selectBody The body of the Select statement
     * @return The select operator that was just created
     */
    private SelectionOperator generateSelection(PlainSelect selectBody) {
        FromItem fromItem = selectBody.getFromItem();
        Expression whereExpression = selectBody.getWhere();
        return new SelectionOperator(visitor, this.columnMap, whereExpression, generateScan(selectBody));
    }

    /**
     * Generate a new select operator and makes it the root
     *
     * @param selectBody The body of the Select statement
     * @return The select operator that was just created
     */
    private ProjectionOperator generateProjection(PlainSelect selectBody) {
        FromItem fromItem = selectBody.getFromItem();
        List<SelectItem> selectItemsList = selectBody.getSelectItems();
        Expression whereExpression = selectBody.getWhere();
        List<Join> joinsList = selectBody.getJoins();

        if (joinsList != null) {
            return new ProjectionOperator(this.columnMap, selectItemsList, generateJoin(selectBody));
        } else if (whereExpression == null) {
            return new ProjectionOperator(this.columnMap, selectItemsList, generateScan(selectBody));
        } else {
            return new ProjectionOperator(this.columnMap, selectItemsList, generateSelection(selectBody));
        }

    }

    /**
     * Generates a join query plan
     *
     * @param selectBody The body of a select statement. The joins field must be non
     *                   null and non empty
     * @return the root of the join query plan
     */
    private JoinOperator generateJoin(PlainSelect selectBody) {
        JoinOperator root = new JoinOperator();
        JoinOperator currentParent = root;
        List<Join> joins = new ArrayList<>(selectBody.getJoins());
        HashMap<String, Integer> tableOffset = generateJoinTableOffsets(selectBody);
        Stack<BinaryExpression> expressions = getExpressions(selectBody.getWhere());
        while (joins.size() > 0) {
            if (root == null) {
                root = currentParent;
            }
            Table rightChildTable = (Table) joins.remove(joins.size() - 1).getRightItem();

            String rightChildTableName = rightChildTable.getAlias();
            rightChildTableName = (rightChildTableName != null) ? rightChildTableName : rightChildTable.getName();

            JoinExpressions joinExpressions = getJoinExpressions(expressions, rightChildTableName);

            Stack<Expression> parentExpressions = joinExpressions.getParentExpressions();
            Stack<BinaryExpression> leftChildExpressions = joinExpressions.getLeftExpressions();
            Stack<Expression> rightChildExpressions = joinExpressions.getRightChildExpressions();
            expressions = leftChildExpressions;

            // Set Right Child of current Parent
            currentParent.setRightChild(getJoinChildOperator(rightChildExpressions, rightChildTable));

            // Set Join Condition of current parent
            currentParent.setJoinCondition(generateExpressionTree(parentExpressions));

            // Set ExpressionVisitor of current parent
            JoinExpressionVisitor visitor = new JoinExpressionVisitor(this.columnMap, tableOffset,
                    rightChildTableName);
            currentParent.setVisitor(visitor);

            // Set left child of current parent
            Operator leftOperator;
            if (joins.size() == 0) {
                currentParent
                        .setLeftChild(getJoinChildOperator((Stack) leftChildExpressions, selectBody.getFromItem()));
            } else {
                leftOperator = new JoinOperator();
                currentParent.setLeftChild(leftOperator);
                currentParent = (JoinOperator) leftOperator;
            }
        }
        return root;
    }

    /**
     * Decouple expression into all component binary expressions that are not AND
     * expressions
     *
     * @param expression the expression to decouple
     * @return a stack of the decoupled expressions
     */
    private Stack<BinaryExpression> getExpressions(Expression expression) {
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
     * Conjoins a stack of expressions to build an expression
     * tree
     *
     * @param expressions a stack of expressions to conjoin
     * @return root of the expression tree from conjoining expressions
     */
    private Expression generateExpressionTree(Stack<Expression> expressions) {
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
     * Distributes expressions among a join operator and its children
     *
     * @param expressions         expressions to be distribuited among the join
     *                            operator
     *                            and its children
     * @param rightChildTableName table corresponding to the right child of the Join
     *                            Operator
     * @return a JoinExpressions intance representing the result of the distribution
     */
    private JoinExpressions getJoinExpressions(Stack<BinaryExpression> expressions, String rightChildTableName) {
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
     * Generate a child operator evaluating childExpressions on childTable
     *
     * @param childExpressions stack of expressions to be evaluated by the child
     *                         operator
     * @param childTable       table corresponding to the child operator
     * @return the child operator
     */
    private Operator getJoinChildOperator(Stack<Expression> childExpressions, FromItem childTable) {
        Operator operator;
        PlainSelect selectBody = new PlainSelect();
        selectBody.setFromItem(childTable);
        if (childExpressions == null || childExpressions.size() == 0)
            operator = generateScan(selectBody);
        else {
            Expression rightChildExpression = generateExpressionTree(childExpressions);
            selectBody.setWhere(rightChildExpression);
            operator = generateSelection(selectBody);
        }
        return operator;
    }

    /**
     * Generates a map of offsets for column indices of tables in the results of
     * joins
     *
     * @param selectBody a select body containing the order in which tables are
     *                   joined
     * @return a map of column index offsets for tables after a join operation
     */
    private HashMap<String, Integer> generateJoinTableOffsets(PlainSelect selectBody) {
        HashMap<String, Integer> tableOffset = new HashMap<>();
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
                    + DatabaseCatalog.getInstance().columnMap(this.columnMap.getBaseTable(prevTable)).size();
            tableOffset.put(curTable, newOffset);
            prevOffset = newOffset;
            prevTable = curTable;
        }

        return tableOffset;
    }

    /**
     * Generates a new sort operator. Places sort operator directly above the
     * current root.
     *
     * @param selectBody The body of the select statement.
     * @return A new sort operator.
     */
    private SortOperator generateSort(PlainSelect selectBody) {
        return new SortOperator(root, getColumnIndex(selectBody), selectBody.getOrderByElements());
    }

    /**
     * Creates a mapping from columns names in the select clause to indexes in a
     * corresponding tuple.
     * 
     * @param selectBody- The body of the select statement.
     * @return- A HashMap from column names to indexes in a tuple.
     */
    private HashMap<String, Integer> getColumnIndex(PlainSelect selectBody) {
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

    private Operator generateDistinct() {
        return new DuplicateEliminationOperator(root);
    }

    /**
     * Constructor that initialises a QueryPlan
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting
     *                    at 1)
     */
    public QueryPlan(Statement statement, int queryNumber) {
        evaluateQueries(statement, queryNumber);
        if (Interpreter.getOutputdir() != null) {
            queryOutputName = Interpreter.getOutputdir() + sep + "query" + queryNumber;
            setQueryOutputFileName(queryOutputName);
        }
    }

    /**
     * Evaluates the result of the QueryPlan
     */
    public void evaluate() {
        if (queryOutputName != null) {
            long startTime = System.currentTimeMillis();
            root.dump(queryOutputName);
            long finishTime = System.currentTimeMillis();
            logger.log("Elapsed time for query " + queryNumber + ": " + (finishTime - startTime));
        }
    }

    /**
     * Returns the root of this QueryPlan
     *
     * @return The root operator of the QueryPlan
     */
    public Operator getRoot() {
        return root;
    }

    /**
     * Populates the root field
     *
     * @param root The root operator of the QueryPlan
     */
    public void setRoot(Operator root) {
        this.root = root;
    }

    /**
     * Creates the queryOutput file and deletes any existing file with the same path
     * and sets the queryOutputFileName field
     *
     * @param queryOutputFileName The name of the file that will contain the query
     *                            results
     */
    public void setQueryOutputFileName(String queryOutputFileName) {
        File new_file = new File(queryOutputFileName);
        if (new_file.isFile()) {
            new_file.delete();
        }
        try {
            Files.createFile(Paths.get(queryOutputFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
