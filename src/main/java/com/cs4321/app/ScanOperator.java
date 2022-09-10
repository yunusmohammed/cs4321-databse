package com.cs4321.app;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The ScanOperator support queries that are full table scans,
 * e.g. SELECT * FROM SomeTable
 */
public class ScanOperator extends Operator {
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private int nextIndex = 0;
    private String queryOutputFileName;
    private String baseTablePath;
    private BufferedReader reader;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    public ScanOperator(String baseTable) {
        setBaseTablePath(baseTable);
        try {
            reader = new BufferedReader(new FileReader(getBaseTablePath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param baseTable           The table in the database the ScanOperator is scanning
     * @param queryOutputFileName The name of the file that will contain the query results
     */
    public ScanOperator(String baseTable, String queryOutputFileName) {
        this(baseTable);
        setQueryOutputFileName(queryOutputFileName);
    }

    /**
     * Gets the next tuple of the ScanOperator’s output
     *
     * @return The next tuple of the ScanOperator’s output
     */
    @Override
    public Tuple getNextTuple() {
        Tuple tuple = null;
        try {
            String line = reader.readLine();
            if (line != null) {
                tuple = new Tuple(line);
                setNextIndex(getNextIndex() + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tuple;
    }

    /**
     * Resets the Table Index of the next tuple of the
     * ScanOperator’s output to the beginning of the table
     */
    @Override
    public void reset() {
        setNextIndex(0);
    }

    /**
     * Calls getNextTuple() until all tuples have been accessed and writes each tuple to
     * the queryOutputFile if the field has been set. Otherwise, writes each tuple to the console
     */
    @Override
    public void dump() {
        // If no output file is specified, print to console
        if (getQueryOutputFileName() == null) {
            Tuple nextTuple = getNextTuple();
            while (nextTuple != null) {
                System.out.println(nextTuple);
                nextTuple = getNextTuple();
            }
        } else {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(getQueryOutputFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter printWriter = new PrintWriter(fileWriter);
            Tuple nextTuple = getNextTuple();
            while (nextTuple != null) {
                printWriter.println(nextTuple);
                nextTuple = getNextTuple();
            }
            printWriter.close();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the baseTablePath
     *
     * @return The path to table in the database the ScanOperator is scanning
     */
    private String getBaseTablePath() {
        return baseTablePath;
    }

    /**
     * Populates the baseTablePath field
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    private void setBaseTablePath(String baseTable) {
        this.baseTablePath = dbc.tablePath(baseTable);
    }

    /**
     * Populates the nextIndex field
     *
     * @return Table Index of the next tuple of the ScanOperator’s output
     */
    public int getNextIndex() {
        return nextIndex;
    }

    /**
     * Sets the Table Index of the next tuple of the ScanOperator’s output
     *
     * @param nextIndex Table Index of the next tuple of the ScanOperator’s output
     */
    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }


    /**
     * Creates the queryOutput file and deletes any existing file with the same path
     * and sets the queryOutputFileName field
     *
     * @param queryOutputFileName The name of the file that will contain the query results
     */
    public void setQueryOutputFileName(String queryOutputFileName) {
        this.queryOutputFileName = queryOutputFileName;
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

    /**
     * Returns the queryOutputFileName
     *
     * @return The name of the file that will contain the query results
     */
    public String getQueryOutputFileName() {
        return queryOutputFileName;
    }
}


