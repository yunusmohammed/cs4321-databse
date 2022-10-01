package com.cs4321.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TupleWriter {

    public static void writeToFile(String filename, Tuple tuple) throws IOException {
        FileOutputStream fout = new FileOutputStream(filename);
        FileChannel fc = fout.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(4096);
        String tupleStr = tuple.toString();

        for (int i = 0; i < tupleStr.length(); ++i) {
            buffer.put((byte) tupleStr.charAt(i));
        }
        buffer.flip();
        fc.write( buffer );
        fout.close();
    }

    public static void main(String[] args) throws IOException {
        writeToFile("output", new Tuple("1,2,3"));
    }

    public void close() {

    }

    public void reset() {

    }

}
