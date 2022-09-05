package com.cs4321.app;

/**
 * Abstract class which all operators extend.
 */
public interface Operator {
    /**
     * Gets the next tuple of the operator’s output. If the operator still has some available
     * output, it will return the next tuple, otherwise it would return null.
     */
    Tuple getNextTuple();

    /**
     * Tells the operator to reset its state and start returning its output again from the
     * beginning;
     */
    void reset();

    /**
     * Calls getNextTuple() until the next tuple is null (no more output) and writes each tuple to
     * a suitable PrintStream.
     */
    void dump();


}
