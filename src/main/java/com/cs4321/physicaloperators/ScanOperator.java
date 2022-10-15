package com.cs4321.physicaloperators;


import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Tuple;
import com.cs4321.app.TupleReader;

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
    private boolean humanReadable = false;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param baseTable The table in the database the ScanOperator is scanning
     */
    public ScanOperator(String baseTable) {
        setBaseTablePath(baseTable);
        try {
            tupleReader = new TupleReader(getBaseTablePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ScanOperator(String baseTable, boolean humanReadable) {
        this(baseTable);
        this.humanReadable = humanReadable;
        try {
            reader = new BufferedReader(new FileReader(getBaseTablePath()));
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
        if (humanReadable) {
            Tuple tuple = null;
            try {
                String line = reader.readLine();
                if (line != null) {
                    tuple = new Tuple(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tuple;
        }

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
        if (humanReadable) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                reader = new BufferedReader(new FileReader(getBaseTablePath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tupleReader.reset();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
            tupleReader.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
