package com.cs4321.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Writes Tuples to a specified file using the Java NIO block format
 *
 * @author Jessica
 */
public class TupleWriter {
    private FileOutputStream fout;
    private FileChannel fc;
    private ByteBuffer buffer;
    private final String filename;
    private final int PAGE_SIZE = 4096;
    private int numberOfTuplesSoFar = 0;
    private int sizeOfTuple = 0;

    /**
     * Initialises a Tuple Writer instance
     *
     * @param filename The query file to be written to
     * @throws IOException
     */
    public TupleWriter(String filename) throws IOException {
        this.fout = new FileOutputStream(filename);
        this.fc = fout.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
        int a = buffer.remaining();
        this.buffer.putInt(0);
        a = buffer.remaining();
        this.buffer.putInt(0);
        a = buffer.remaining();
    }

    /**
     * Writes the specified Tuple to the specified file
     *
     * @param tuple     The tuple to be written to the file
     * @param endOfFile true iff there are no more tuples to write to the file
     * @throws IOException
     */
    public void writeToFile(Tuple tuple, boolean endOfFile) throws IOException {
        // Empty file
        if (endOfFile && (numberOfTuplesSoFar == 0)) {
            close();
            return;
        }

        if (!endOfFile) {
            String tupleStr = tuple.toString();
            String[] tupleArr = tupleStr.split(",");
            sizeOfTuple = Math.max(sizeOfTuple, tupleArr.length);

            for (String s : tupleArr) {
                buffer.putInt(Integer.parseInt(s));
            }
            int a = buffer.remaining();
            numberOfTuplesSoFar++;
        }

        if (buffer.remaining() < (sizeOfTuple * 4) || endOfFile) {
            buffer.putInt(0, sizeOfTuple);
            buffer.putInt(4, numberOfTuplesSoFar);
            buffer.clear();
            fc.write(buffer);

            numberOfTuplesSoFar = 0;
            buffer.clear();
            buffer.put(new byte[PAGE_SIZE]);
            buffer.clear();
            buffer.putInt(0);
            buffer.putInt(0);
        }
    }

    /**
     * Removes all opened and unused resources
     *
     * @throws IOException
     */
    public void close() throws IOException {
        fout.close();
        fc.close();
    }

    /**
     * Resets the write to the beginning of the file
     *
     * @throws FileNotFoundException
     */
    public void reset() throws FileNotFoundException {
        fout = new FileOutputStream(filename);
        fc = fout.getChannel();
        buffer = ByteBuffer.allocate(PAGE_SIZE);

        numberOfTuplesSoFar = 0;
        sizeOfTuple = 0;
        buffer.putInt(0);
        buffer.putInt(0);
    }

}
