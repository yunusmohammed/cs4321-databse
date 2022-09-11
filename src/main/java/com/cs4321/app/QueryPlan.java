package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * The QueryPlan is a tree of operators.  A QueryPlan is constructed for each Statement
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
                this.root = generateScan(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns && (otherFromItemsArrayList == null || otherFromItemsArrayList.size() == 0)
                    && whereExpression != null && distinct == null && orderByElementsList == null) {
                this.root = generateSelection(selectBody);
            } else {
                //TODO: Add conditions for other operators @Lenhard, @Yohannes, @Yunus
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
     * Constructor that initialises a QueryPlan
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting at 1)
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
     * @param queryOutputFileName The name of the file that will contain the query results
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
