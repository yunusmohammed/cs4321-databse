package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import com.cs4321.app.TupleWriter;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Abstract class which all operators extend.
 *
 * @author Jessica and Lenhard
 */
public abstract class Operator {

    /**
     * Gets the next tuple of the operator’s output. If the operator still has some available
     * output, it will return the next tuple, otherwise it would return null.
     *
     * @return The next tuple of the operator’s output
     */
    abstract Tuple getNextTuple();

    /**
     * Tells the operator to reset its state and start returning its output again from the
     * beginning;
     */
    abstract void reset();

    /**
     * For binary files, calls getNextTuple() until the next tuple is null (no more output)
     * and writes each tuple to a provided filename.
     *
     * @param filename The name of the file that will contain the query results
     */
    void dump(String filename) {
        try {
            TupleWriter tupleWriter = new TupleWriter(filename);
            Tuple nextTuple = getNextTuple();
            while (nextTuple != null) {
                tupleWriter.writeToFile(nextTuple, false);
                nextTuple = getNextTuple();
            }
            tupleWriter.writeToFile(null, true);
            tupleWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * For human-readable files, calls getNextTuple() until the next tuple is null (no more output)
     * and writes each tuple to a suitable PrintStream.
     *
     * @param output The output stream to write to
     */
    void dump(PrintStream output) {
        Tuple nextTuple = getNextTuple();
        while (nextTuple != null) {
            output.println(nextTuple);
            nextTuple = getNextTuple();
        }
        output.close();
    }

    /**
     * Cleanups any resources that need to be cleaned up such as BufferedReader, etc.
     */
    public abstract void finalize();
}
