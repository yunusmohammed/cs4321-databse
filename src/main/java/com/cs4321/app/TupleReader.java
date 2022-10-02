package com.cs4321.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TupleReader {
    static FileInputStream fin;
    static FileChannel fc;
    static ByteBuffer buffer;
    static String filename;
    static int numberOfPages = 0;
    static int currentPage = 0;
    static int tupleNextIndex = 0;
    static List<Tuple> tupleList;

    public TupleReader(String filename) throws IOException {
        fin = new FileInputStream(filename);
        fc = fin.getChannel();
        buffer = ByteBuffer.allocate(4096);
        TupleReader.filename = filename;
    }

    public static List<Tuple> readFromFile() throws IOException {
        List<Tuple> tupleList = new ArrayList<>();
        int checkEndOfFile = fc.read(buffer);
        if (checkEndOfFile == -1) {
            return null;
        }
        buffer.clear();
        int i = 0;
        int tupleSize = 0;
        int pageSize = 0;
        while (buffer.hasRemaining()) {
            if (i == 0) {
                tupleSize = buffer.getInt();
            } else if (i == 1) {
                pageSize = buffer.getInt();
            } else {
                if (tupleList.size() == pageSize) {
                    break;
                }
                StringBuilder data = new StringBuilder();
                for (int idx = 0; idx < tupleSize; idx++) {
                    data.append(buffer.getInt());
                    if (idx != tupleSize - 1) {
                        data.append(",");
                    }
                }
                tupleList.add(new Tuple(data.toString()));
            }
            i++;
        }
        while (buffer.hasRemaining()) {
            buffer.getInt();
        }
        numberOfPages++;
        // Reference: https://stackoverflow.com/a/14937929/13636444
        buffer.clear();
        buffer.put(new byte[4096]);
        buffer.clear();
        return tupleList;
    }

    public static void main(String[] args) throws IOException {
        new TupleReader("src/main/java/com/cs4321/app/query1");
        List<Tuple> tupleList = readFromFile();
        while (tupleList != null) {
            System.out.println(tupleList.size());
            tupleList = readFromFile();
        }
    }

    public Tuple readNextTuple() throws IOException {
        if (tupleList == null) {
            tupleList = readFromFile();
            // End of All Pages
            if (tupleList == null) {
                return null;
            }
        }
        if (tupleNextIndex == tupleList.size()) {
            tupleList = readFromFile();
            currentPage++;
            tupleNextIndex = 0;
        }
        tupleNextIndex++;
        return tupleList.get(tupleNextIndex - 1);
    }

    public static void close() throws IOException {
        fin.close();
    }

    public static void reset() throws FileNotFoundException {
        numberOfPages = 0;
        currentPage = 0;
        tupleNextIndex = 0;
        tupleList = null;
        buffer.clear();
        fin = new FileInputStream(filename);
        fc = fin.getChannel();
        buffer.clear();
        buffer.put(new byte[4096]);
        buffer.clear();
    }

}
