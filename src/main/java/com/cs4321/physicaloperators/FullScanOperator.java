package com.cs4321.physicaloperators;

import com.cs4321.app.*;
import net.sf.jsqlparser.schema.Table;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The ScanOperator support queries that are full table scans,
 * e.g. SELECT * FROM SomeTable
 */
public class FullScanOperator extends ScanOperator {

    private boolean humanReadable = false;

    /**
     * Constructor that initialises a ScanOperator
     *
     * @param table    The table in the database the ScanOperator is scanning
     * @param aliasMap The mapping from table names to base table names
     */
    public FullScanOperator(Table table, AliasMap aliasMap) {
        super(table, aliasMap);
        try {
            tupleReader = new TupleReader(getBaseTablePath());
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    public FullScanOperator(Table table, AliasMap aliasMap, boolean humanReadable) {
        super(table, aliasMap);
        this.humanReadable = humanReadable;
        try {
            reader = new BufferedReader(new FileReader(getBaseTablePath()));
        } catch (IOException e) {
            logger.log(e.getMessage());
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
                logger.log(e.getMessage());
            }
            return tuple;
        }

        try {
            return tupleReader.readNextTuple();
        } catch (IOException e) {
            logger.log(e.getMessage());
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
                logger.log(e.getMessage());
            }
            try {
                reader = new BufferedReader(new FileReader(getBaseTablePath()));
            } catch (FileNotFoundException e) {
                logger.log(e.getMessage());
            }
        } else {
            try {
                tupleReader.reset();
            } catch (FileNotFoundException e) {
                logger.log(e.getMessage());
            }
        }
    }

    /**
     * Closes the initialised BufferedReader
     */
    @Override
    public void finalize() {
        super.finalize();
    }

    /**
     * Returns the string representation of the Full Scan Operator
     *
     * @return The string representation of the Scan Operator
     *         Eg:
     *         FullScanOperator{baseTablePath='../src/test/resources/input_binary/db/data/Boats'}
     */
    @Override
    public String toString() {
        return "FullScanOperator{" +
                "baseTablePath='" + baseTablePath + '\'' +
                '}';
    }
}
