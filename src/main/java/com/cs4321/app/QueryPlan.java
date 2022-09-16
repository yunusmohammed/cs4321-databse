package com.cs4321.app;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.hamcrest.core.IsInstanceOf;
import java.util.Map;

/**
 * The QueryPlan is a tree of operators. A QueryPlan is constructed for each
 * Statement
 * and returned to the interpreter, so it can read the results of the QueryPlan
 *
 * @author Jessica Tweneboah
 */
public class QueryPlan {
    private Operator root;
    private static final SelectExpressionVisitor visitor = new SelectExpressionVisitor();
    private static final String sep = File.separator;
    private String queryOutputName;

    /**
     * Evaluates SQL query statements
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting
     *                    at 1)
     */
    private void evaluateQueries(Statement statement, int queryNumber) {
        if (statement != null) {
            Select select = (Select) statement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();

            List<SelectItem> selectItemsList = selectBody.getSelectItems();
            SelectItem firstSelectItem = selectItemsList.get(0);
            FromItem fromItem = selectBody.getFromItem();
            List<Join> otherFromItemsArrayList = selectBody.getJoins();
            List<OrderByElement> orderByElementsList = selectBody.getOrderByElements();
            Expression whereExpression = selectBody.getWhere();
            Distinct distinct = selectBody.getDistinct();

            if ("*".equals(firstSelectItem.toString()) && otherFromItemsArrayList == null && whereExpression == null
                    && distinct == null && orderByElementsList == null) {
                this.root = generateScan(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns
                    && (otherFromItemsArrayList == null || otherFromItemsArrayList.size() == 0)
                    && whereExpression != null && distinct == null && orderByElementsList == null) {
                this.root = generateSelection(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns
                    && otherFromItemsArrayList != null && otherFromItemsArrayList.size() > 0) {
                this.root = generateJoin(selectBody);
            } else {
                // TODO: Add conditions for other operators @Lenhard, @Yohannes, @Yunus
                return;
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
        FromItem fromItem = selectBody.getFromItem();
        return new ScanOperator(fromItem.toString());
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

        Map<String, Integer> mapping = DatabaseCatalog.getInstance().columnMap(fromItem.toString());
        return new SelectionOperator(visitor, mapping, whereExpression, generateScan(selectBody));
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
        List<Join> joins = selectBody.getJoins();
        HashMap<String, Integer> tableOffset = generateJoinTableOffsets(selectBody);
        Stack<BinaryExpression> expressions = getExpressions(selectBody.getWhere());
        while (joins.size() > 0) {
            if (root == null) {
                root = currentParent;
            }
            FromItem rightChildTable = joins.remove(joins.size() - 1).getRightItem();
            Stack<Expression> rightChildExpressions;
            Stack<Expression> parentExpressions;
            Stack<BinaryExpression> leftChildExpressions;

            JoinExpressions joinExpressions = getJoinExpressions(expressions, rightChildTable);

            parentExpressions = joinExpressions.getParentExpressions();
            leftChildExpressions = joinExpressions.getLeftExpressions();
            rightChildExpressions = joinExpressions.getRightChildExpressions();
            expressions = leftChildExpressions;

            // Set Right Child of current Parent
            currentParent.setRightChild(getJoinChildOperator(rightChildExpressions, rightChildTable));

            // Set Join Condition of current parent
            currentParent.setJoinCondition(generateExpressionTree(parentExpressions));

            // Set ExpressionVisitor of current parent
            JoinExpressionVisitor visitor = new JoinExpressionVisitor(tableOffset, rightChildTable.toString());
            currentParent.setVisitor(visitor);

            // Set left child of current parent
            Operator leftOperator;
            if (joins.size() == 0) {
                currentParent.setLeftChild(getJoinChildOperator((Stack) leftChildExpressions, rightChildTable));
            } else {
                leftOperator = new JoinOperator();
                currentParent.setLeftChild(leftOperator);
                currentParent = (JoinOperator) leftOperator;
            }
        }
        return root;
    }

    private Stack<BinaryExpression> getExpressions(Expression expression) {
        Stack<BinaryExpression> expressions = new Stack<>();
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

    private Expression generateExpressionTree(Stack<Expression> expressions) {
        if (expressions.size() == 0)
            return null;
        while (expressions.size() >= 2) {
            Expression leftExpression = expressions.pop();
            Expression rightExpression = expressions.pop();
            AndExpression exp = new AndExpression(leftExpression, rightExpression);
            expressions.add(exp);
        }
        return expressions.pop();
    }

    private JoinExpressions getJoinExpressions(Stack<BinaryExpression> expressions, FromItem rightChildTable) {
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

            if (((leftTable != null && leftTable.equals(rightChildTable.toString()))
                    && (rightTable == null || rightTable.equals(rightChildTable.toString()))) ||
                    ((rightTable != null && rightTable.equals(rightChildTable.toString()))
                            && (leftTable == null || leftTable.equals(rightChildTable.toString())))) {
                // expression references only the columns from the right child's table
                rightChildExpressions.add(exp);

            } else if ((leftTable != null && leftTable.equals(rightChildTable.toString()))
                    || (rightTable != null && rightTable.equals(rightChildTable.toString()))) {
                // expression references columns from the rigth child's table and some other
                // tables in the left child
                parentExpressions.add(exp);
            } else
                leftChildExpressions.add(exp);
        }

        return new JoinExpressions(parentExpressions, rightChildExpressions, leftChildExpressions);
    }

    private Operator getJoinChildOperator(Stack<Expression> childExpressions, FromItem rightChildTable) {
        Operator operator;
        PlainSelect righSelectBody = new PlainSelect();
        righSelectBody.setFromItem(rightChildTable);
        if (childExpressions.size() == 0)
            operator = generateScan(righSelectBody);
        else {
            Expression rightChildExpression = generateExpressionTree(childExpressions);
            righSelectBody.setWhere(rightChildExpression);
            operator = generateSelection(righSelectBody);
        }
        return operator;
    }

    private HashMap<String, Integer> generateJoinTableOffsets(PlainSelect selectBody) {
        HashMap<String, Integer> tableOffset = new HashMap<>();
        List<Join> joins = selectBody.getJoins();
        int prevOffset = 0;
        String prevTable = selectBody.getFromItem().toString();
        tableOffset.put(prevTable, prevOffset);
        for (Join join : joins) {
            String curTable = join.getRightItem().toString();
            int newOffset = prevOffset + DatabaseCatalog.getInstance().columnMap(prevTable).size();
            tableOffset.put(curTable, newOffset);
            prevOffset = newOffset;
            prevTable = curTable;
        }

        return tableOffset;
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
            try {
                root.dump(new PrintStream(queryOutputName));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            root.dump(System.out);
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
