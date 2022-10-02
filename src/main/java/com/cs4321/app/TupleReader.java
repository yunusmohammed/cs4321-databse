package com.cs4321.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TupleReader {
    private FileInputStream fin;
    private FileChannel fc;
    private ByteBuffer buffer;
    private final String filename;
    private int numberOfPages = 0;
    private int currentPage = 0;
    private int tupleNextIndex = 0;
    private List<Tuple> tupleList;
    private final int PAGE_SIZE = 4096;

    public TupleReader(String filename) throws IOException {
        this.fin = new FileInputStream(filename);
        this.fc = fin.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
    }

    public List<Tuple> readFromFile() throws IOException {
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
                i++;
            } else if (i == 1) {
                pageSize = buffer.getInt();
                i++;
            } else {
                if (tupleList.size() == pageSize) {
                    break;
                }
                StringBuilder data = new StringBuilder();
                for (int idx = 0; idx < tupleSize; idx++) {
                    data.append(buffer.getInt());
                    i++;
                    if (idx != tupleSize - 1) {
                        data.append(",");
                    }
                }
                tupleList.add(new Tuple(data.toString()));
            }
        }
        while (buffer.hasRemaining()) {
            buffer.getInt();
        }
        numberOfPages++;
        // Reference: https://stackoverflow.com/a/14937929/13636444
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
        return tupleList;
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
            if (tupleList == null) {
                return null;
            }
            currentPage++;
            tupleNextIndex = 0;
        }
        tupleNextIndex++;
        return tupleList.get(tupleNextIndex - 1);
    }

    public void close() throws IOException {
        fin.close();
    }

    public void reset() throws FileNotFoundException {
        numberOfPages = 0;
        currentPage = 0;
        tupleNextIndex = 0;
        tupleList = null;
        buffer.clear();
        fin = new FileInputStream(filename);
        fc = fin.getChannel();
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
    }

}
