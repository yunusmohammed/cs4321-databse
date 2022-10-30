package com.cs4321.app;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Jessica Tweneboah
 */
public class InterpreterConfig {
    private String inputdir;
    private String outputdir;
    private String tempdir;
    private boolean evaluateQueries;
    private boolean buildIndexes;
    private boolean humanReadable = false;

    /**
     * Extracts the config info from the file at `filePath`.
     *
     * @param filePath The path to the config file.
     */
    public InterpreterConfig(String filePath) {
        try {
            List<String> configLines = Files.readAllLines(Paths.get(filePath));
            if (configLines.size() < 5) {
                Logger.getInstance().log("Invalid format for Interpreter config file");
                throw new Exception("Invalid format for Interpreter config file");
            }
            this.inputdir = configLines.get(0);
            this.outputdir = configLines.get(1);
            this.tempdir = configLines.get(2);
            this.buildIndexes = Integer.parseInt(configLines.get(3)) == 1;
            this.evaluateQueries = Integer.parseInt(configLines.get(4)) == 1;
            if (configLines.size() == 6) {
                this.humanReadable = Integer.parseInt(configLines.get(5)) == 1;
            }
        } catch (Exception e) {
            Logger.getInstance().log(e.getMessage());
        }
    }


    /**
     * Returns the query output directory
     *
     * @return The empty Output Directory that contains the results of the queries
     */
    public String getOutputdir() {
        return outputdir;
    }

    /**
     * Sets the query output directory
     *
     * @param outputdir The empty Output Directory that contains the results of the queries
     */
    public void setOutputdir(String outputdir) {
        this.outputdir = outputdir;
    }

    /**
     * Sets the query input directory
     *
     * @return The input directory, which contains a queries.sql file containing the sql queries.
     * a db subdirectory, which contains a schema.txt file specifying the schema for your
     * database as well as a data subdirectory, where the data itself is stored.
     */
    public String getInputdir() {
        return inputdir;
    }

    /**
     * Returns the query input directory
     *
     * @param inputdir The input directory, which contains a queries.sql file containing the sql queries.
     *                 a db subdirectory, which contains a schema.txt file specifying the schema for your
     *                 database as well as a data subdirectory, where the data itself is stored.
     */
    public void setInputdir(String inputdir) {
        this.inputdir = inputdir;
    }

    /**
     * Returns the temporary directory where your external sort operators
     * should write their “scratch” files.
     *
     * @return the temporary directory where your external sort operators
     * should write their “scratch” files.
     */
    public String getTempdir() {
        return tempdir;
    }

    /**
     * Sets the value of tempdir
     *
     * @param tempdir the temporary directory where your external sort operators
     *                should write their “scratch” files.
     */
    public void setTempdir(String tempdir) {
        this.tempdir = tempdir;
    }

    /**
     * Returns a flag to indicate whether the interpreter should actually evaluate the SQL queries
     * (false = no, true = yes)
     *
     * @return a flag to indicate whether the interpreter should actually evaluate the SQL queries
     * (false = no, true = yes)
     */
    public boolean shouldEvaluateQueries() {
        return evaluateQueries;
    }

    /**
     * Returns a flag to indicate whether the interpreter should build indexes (false = no, true = yes)
     *
     * @return a flag to indicate whether the interpreter should build indexes (false = no, true = yes)
     */
    public boolean shouldBuildIndexes() {
        return buildIndexes;
    }

    /**
     * Returns true to set the project to read/write human readable files
     *
     * @return true to set the project to read/write human readable files
     */
    public boolean isHumanReadable() {
        return humanReadable;
    }

    /**
     * Sets the value of humanReadable
     *
     * @param humanReadable true if the project is set to read/write human readable files
     */
    public void setHumanReadable(boolean humanReadable) {
        this.humanReadable = humanReadable;
    }
}
