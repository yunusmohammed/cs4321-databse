package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;


/**
 * @author Jessica Tweneboah
 */
public class Interpreter {
    private static String inputdir;
    private static String outputdir;

    DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private static final String sep = File.separator;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Incorrect input format");
            return;
        }
        setInputdir(args[0]);
        setOutputdir(args[1]);
        DatabaseCatalog.setInputDir(getInputdir());
        parseQueries();
    }

    public static void parseQueries() {
        CCJSqlParser parser = null;
        try {
            parser = new CCJSqlParser(new FileReader(queriesPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Statement statement = null;
        int queryNumber = 0;
        while (true) {
            try {
                if ((statement = parser.Statement()) == null) break;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            evaluateQueries(statement, queryNumber);
            queryNumber++;
        }
    }

    public static void evaluateQueries(Statement statement, int queryNumber) {
        Operator operator;
        String queryOutputName = getOutputdir() + sep + "query" + queryNumber;

        Select select = (Select) statement;
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();

        List<SelectItem> selectItemsList = selectBody.getSelectItems();
        SelectItem firstSelectItem = selectItemsList.get(0);
        FromItem fromItem = selectBody.getFromItem();
        List<Join> otherFromItemsArrayList = selectBody.getJoins();
        List<OrderByElement> orderByElementsList = selectBody.getOrderByElements();
        Expression whereExpression = selectBody.getWhere();
        Distinct distinct = selectBody.getDistinct();

        if ("*".equals(firstSelectItem.toString())
                && otherFromItemsArrayList == null && whereExpression == null
                && distinct == null && orderByElementsList == null) {
            operator = new ScanOperator(fromItem.toString(), queryOutputName);
            QueryPlan queryPlan = new ScanQueryPlan(operator);
            queryPlan.evaluate();
        } else {
            //TODO: Add conditions for other operators @Lenhard, @Yohannes, @Yunus
            return;
        }
    }

    /**
     * @return
     */
    public static String getOutputdir() {
        return outputdir;
    }

    /**
     * @param outputdir
     */
    public static void setOutputdir(String outputdir) {
        Interpreter.outputdir = outputdir;
    }

    /**
     * @return
     */
    public static String getInputdir() {
        return inputdir;
    }

    /**
     * @param inputdir
     */
    public static void setInputdir(String inputdir) {
        Interpreter.inputdir = inputdir;
    }

    public static String queriesPath() {
        return inputdir + sep + "queries.sql";
    }

}
