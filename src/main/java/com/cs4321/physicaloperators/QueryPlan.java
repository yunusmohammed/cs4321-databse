package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Interpreter;
import com.cs4321.app.Logger;
import com.cs4321.app.PhysicalPlanBuilder;
import com.cs4321.logicaloperators.LogicalOperator;
import com.cs4321.logicaloperators.LogicalQueryPlan;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private boolean humanReadable;
    private AliasMap aliasMap;
    private final Logger logger = Logger.getInstance();

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
            LogicalQueryPlan logicalPlan = new LogicalQueryPlan(statement);
            LogicalOperator logicalRoot = logicalPlan.getRoot();
            this.root = PhysicalPlanBuilder.getInstance().constructPhysical(logicalRoot);
        }
    }

    /**
     * Constructor that initialises a QueryPlan
     *
     * @param statement   The SQL statement being evaluated
     * @param queryNumber Specifies the index of the query being processed (starting
     *                    at 1)
     */
    public QueryPlan(Statement statement, int queryNumber, boolean humanReadable) {
        evaluateQueries(statement, queryNumber);
        if (Interpreter.getOutputdir() != null) {
            queryOutputName = Interpreter.getOutputdir() + sep + "query" + queryNumber;
            setQueryOutputFileName(queryOutputName);
        }
        this.humanReadable = humanReadable;
    }

    /**
     * Evaluates the result of the QueryPlan
     */
    public void evaluate() {
        if (queryOutputName != null) {
            long startTime = System.currentTimeMillis();
            if (humanReadable) {
                if (queryOutputName != null) {
                    try {
                        root.dump(new PrintStream(queryOutputName));
                    } catch (FileNotFoundException e) {
                        logger.log(e.getMessage());
                    }
                } else {
                    root.dump(System.out);
                }
            } else {
                root.dump(queryOutputName);
            }
            long finishTime = System.currentTimeMillis();
            // logger.log("Elapsed time for query " + queryNumber + ": " + (finishTime -
            // startTime) + "ms");
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
            logger.log(e.getMessage());
        }
    }

}
