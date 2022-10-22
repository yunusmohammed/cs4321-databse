package com.cs4321.app;

import com.cs4321.physicaloperators.QueryPlan;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


/**
 * The SQL interpreter reads from the db directory and from the queries.sql file,
 * and writes output to a suitable output directory.
 *
 * @author Jessica Tweneboah
 */
public class Interpreter {
    private static String inputdir;
    private static String outputdir;
    private static String tempdir;
    private static final String sep = File.separator;
    private static boolean humanReadable = false;
    private static final Logger logger = Logger.getInstance();
    private static List<Statement> statements = new ArrayList<>();

    /**
     * Main function that is executed to run the project
     *
     * @param args The command-line arguments that are passed to the interpreter to run sql queries <br>
     *             args[0]: Specifies an absolute path to the input directory. <br>
     *             args[1]: Specifies an absolute path to the output directory. <br>
     *             args[2]: Specifies is the temporary directory where your external <br>
     *             sort operators should write their “scratch” files. <br>
     *             args[3]: optional -h parameter to specify human-readable files
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Incorrect input format");
            return;
        }
        setInputdir(args[0]);
        setOutputdir(args[1]);
        setTempdir(args[2]);
        if (args.length > 3) {
            humanReadable = "-h".equals(args[3]);
        }
        DatabaseCatalog.setInputDir(getInputdir());
        PhysicalPlanBuilder.setConfigs("plan_builder_config.txt");
        PhysicalPlanBuilder.setHumanReadable(humanReadable);
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
            logger.log(e.getMessage());
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
                    statements.add(statement);
                    QueryPlan queryPlan = new QueryPlan(statement, queryNumber, humanReadable);
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

    /**
     * Returns the temporary directory where your external sort operators
     * should write their “scratch” files.
     *
     * @return the temporary directory where your external sort operators
     * should write their “scratch” files.
     */
    public static String getTempdir() {
        return tempdir;
    }

    /**
     * Sets the value of tempdir
     *
     * @param tempdir the temporary directory where your external sort operators
     *                should write their “scratch” files.
     */
    public static void setTempdir(String tempdir) {
        Interpreter.tempdir = tempdir;
    }

    /**
     * Returns true to set the project to read/write human readable files
     *
     * @return true to set the project to read/write human readable files
     */
    public static boolean isHumanReadable() {
        return humanReadable;
    }

    /**
     * Sets the value of humanReadable
     *
     * @param humanReadable true if the project is set to read/write human readable files
     */
    public static void setHumanReadable(boolean humanReadable) {
        Interpreter.humanReadable = humanReadable;
    }

    /**
     * Returns all the statements from the queries.sql file that have been evaluated
     * @return- all the statements from the queries.sql file that have been evaluated
     */
    public static List<Statement> getStatements() {
        return statements;
    }
}
