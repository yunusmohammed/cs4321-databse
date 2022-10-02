package com.cs4321.app;

import java.io.IOException;

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
     * Calls getNextTuple() until the next tuple is null (no more output) and writes each tuple to
     * a suitable PrintStream.
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
     * Cleanups any resources that need to be cleaned up such as BufferedReader, etc.
     */
    public abstract void finalize();
}
