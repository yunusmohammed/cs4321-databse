package com.cs4321.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TupleWriter {
    private FileOutputStream fout;
    private FileChannel fc;
    private ByteBuffer buffer;
    private final String filename;
    private final int MAX_TUPLES_PER_PAGE = 340;
    private final int PAGE_SIZE = 4096;
    private int numberOfTuplesSoFar = 0;
    private int sizeOfTuple = 0;

    public TupleWriter(String filename) throws IOException {
        this.fout = new FileOutputStream(filename);
        this.fc = fout.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
        this.buffer.putInt(0);
        this.buffer.putInt(0);
    }

    public void writeToFile(Tuple tuple, boolean endOfFile) throws IOException {
        if (!endOfFile) {
            String tupleStr = tuple.toString();
            String[] tupleArr = tupleStr.split(",");
            sizeOfTuple = Math.max(sizeOfTuple, tupleArr.length);

            for (String s : tupleArr) {
                buffer.putInt(Integer.parseInt(s));
            }
            numberOfTuplesSoFar++;
        }

        if (numberOfTuplesSoFar == MAX_TUPLES_PER_PAGE || endOfFile) {
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

    public void close() throws IOException {
        fout.close();
    }

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
