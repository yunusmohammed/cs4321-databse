package com.cs4321.app;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The ScanOperator support queries that are full table scans,
 * e.g. SELECT * FROM SomeTable
 */
public class ScanOperator extends Operator {
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private String baseTablePath;
    private BufferedReader reader;
    private TupleReader tupleReader;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    public ScanOperator(String baseTable) {
        setBaseTablePath(baseTable);
        try {
            reader = new BufferedReader(new FileReader(getBaseTablePath()));
            tupleReader = new TupleReader(getBaseTablePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the next tuple of the ScanOperator’s output
     *
     * @return The next tuple of the ScanOperator’s output
     */
    @Override
    public Tuple getNextTuple() {
        try {
            return tupleReader.readNextTuple();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Resets the Table Index of the next tuple of the
     * ScanOperator’s output to the beginning of the table
     */
    @Override
    public void reset() {
        try {
            tupleReader.reset();
        } catch (FileNotFoundException e) {
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
     * Closes the initialised BufferedReader
     */
    @Override
    public void finalize() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
