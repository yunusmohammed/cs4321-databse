package com.cs4321.app;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The ScanOperator support queries that are full table scans,
 * e.g. SELECT * FROM SomeTable
 */
public class ScanOperator implements Operator {
    private List<Tuple> baseTable;
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private int nextIndex = 0;
    private String queryOutputFileName;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    public ScanOperator(String baseTable) {
        setBaseTable(baseTable);
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
        return baseTable.get(getNextIndex());
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
        int tableLength = getBaseTable().size();
        if (getQueryOutputFileName() == null) {
            while (getNextIndex() < tableLength) {
                System.out.println(getNextTuple());
                setNextIndex(getNextIndex() + 1);
            }
        } else {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(getQueryOutputFileName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter printWriter = new PrintWriter(fileWriter);
            while (getNextIndex() < tableLength) {
                printWriter.println(getNextTuple().toString());
                setNextIndex(getNextIndex() + 1);
            }
            printWriter.close();
        }
    }

    /**
     * Returns the baseTable
     *
     * @return The table in the database the ScanOperator is scanning
     */
    public List<Tuple> getBaseTable() {
        return baseTable;
    }

    /**
     * Populates the baseTable field
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    public void setBaseTable(String baseTable) {
        this.baseTable = getTable(baseTable);
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

    /**
     * Returns a list of tuples containing the data from a given table.
     *
     * @param table- The name of the table we want to read data from.
     * @return- A list of tuples containing the data from the table.
     */
    public List<Tuple> getTable(String table) {
        List<String> tableContents = DatabaseCatalog.readFile(DatabaseCatalog.tablePath(table));
        List<Tuple> rows = new ArrayList<>();
        for (String row : tableContents) {
            rows.add(new Tuple(row));
        }
        return rows;
    }

}


