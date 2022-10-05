package com.cs4321.app;

import com.cs4321.physicaloperators.QueryPlan;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


/**
 * The SQL interpreter reads from the db directory and from the queries.sql file,
 * and writes output to a suitable output directory.
 *
 * @author Jessica Tweneboah
 */
public class Interpreter {
    private static String inputdir;
    private static String outputdir;
    private static final String sep = File.separator;

    /**
     * Main function that is executed to run the project
     *
     * @param args The command-line arguments that are passed to the interpreter to run sql queries <br>
     *             args[0]: Specifies an absolute path to the input directory. <br>
     *             args[1]: Specifies an absolute path to the output directory.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Incorrect input format");
            return;
        }
        setInputdir(args[0]);
        setOutputdir(args[1]);
        DatabaseCatalog.setInputDir(getInputdir());
        PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
        parseQueries();
    }

    /**
     * Parses the SQL Queries from the input queries.sql file
     */
    public static void parseQueries() {
        CCJSqlParser parser = null;
        try {
            parser = new CCJSqlParser(new FileReader(queriesPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Statement statement = null;
        int queryNumber = 1;
        while (true) {
            try {
                assert parser != null;
                if ((statement = parser.Statement()) == null) break;
            } catch (ParseException e) {
                System.err.println(e.getLocalizedMessage());
                try {
                    parser.getNextToken();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            try {
                if (statement != null) {
                    QueryPlan queryPlan = new QueryPlan(statement, queryNumber);
                    queryPlan.evaluate();
                }
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
            queryNumber++;
        }
    }


    /**
     * Returns the query output directory
     *
     * @return The empty Output Directory that contains the results of the queries
     */
    public static String getOutputdir() {
        return outputdir;
    }

    /**
     * Sets the query output directory
     *
     * @param outputdir The empty Output Directory that contains the results of the queries
     */
    public static void setOutputdir(String outputdir) {
        Interpreter.outputdir = outputdir;
    }

    /**
     * Sets the query input directory
     *
     * @return The input directory, which contains a queries.sql file containing the sql queries.
     * a db subdirectory, which contains a schema.txt file specifying the schema for your
     * database as well as a data subdirectory, where the data itself is stored.
     */
    public static String getInputdir() {
        return inputdir;
    }

    /**
     * Returns the query input directory
     *
     * @param inputdir The input directory, which contains a queries.sql file containing the sql queries.
     *                 a db subdirectory, which contains a schema.txt file specifying the schema for your
     *                 database as well as a data subdirectory, where the data itself is stored.
     */
    public static void setInputdir(String inputdir) {
        Interpreter.inputdir = inputdir;
    }

    /**
     * Returns the absolute path to the sql queries
     *
     * @return The absolute path to the sql queries
     */
    public static String queriesPath() {
        return inputdir + sep + "queries.sql";
    }

}
