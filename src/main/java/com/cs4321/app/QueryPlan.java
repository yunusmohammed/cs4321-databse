package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.util.List;

/**
 * The QueryPlan is a tree of operators.  A QueryPlan is constructed for each Statement
 * and returned to the interpreter, so it can read the results of the QueryPlan
 *
 * @author Jessica Tweneboah
 */
public class QueryPlan {
    private Operator root;
    private static final String sep = File.separator;


    /**
     * Evaluates SQL query statements
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting at 1)
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

            if ("*".equals(firstSelectItem.toString()) && otherFromItemsArrayList == null && whereExpression == null && distinct == null && orderByElementsList == null) {
                String queryOutputName = Interpreter.getOutputdir() + sep + "query" + queryNumber;
                generateScan(fromItem, queryOutputName);
            } else {
                //TODO: Add conditions for other operators @Lenhard, @Yohannes, @Yunus
                return;
            }
        }
    }

    /**
     * Generate a new scan operator and makes it the root
     *
     * @param fromItem The expression in the from section of a SQL statement
     * @param queryOutputName The name of the file that will contain the query results
     */
    private void generateScan(FromItem fromItem, String queryOutputName) {
        ScanOperator scanOperator = new ScanOperator(fromItem.toString(), queryOutputName);
        setRoot(scanOperator);
    }

    /**
     * Constructor that initialises a QueryPlan
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting at 1)
     */
    public QueryPlan(Statement statement, int queryNumber) {
        evaluateQueries(statement, queryNumber);
    }


    /**
     * Evaluates the result of the QueryPlan
     */
    public void evaluate() {
        root.dump();
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


}
