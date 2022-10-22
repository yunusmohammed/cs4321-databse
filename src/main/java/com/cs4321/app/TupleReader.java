package com.cs4321.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads Tuples from a specified file using the Java NIO block format
 *
 * @author Jessica
 */
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
    private int i = 0;
    private int tupleSize = 0;
    private int pageSize = 0;
    private int maxPageSize = 0;
    private ArrayList pageToPageSize = new ArrayList<Integer>();
    private int numTuplesSoFar = 0;

    /**
     * Initialises a Tuple Reader instance
     *
     * @param filename The query file to be read from
     * @throws IOException
     */
    public TupleReader(String filename) throws IOException {
        this.fin = new FileInputStream(filename);
        this.fc = fin.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
    }

    /**
     * Returns a list of all Tuples on the current page
     *
     * @return a list of all Tuples on the current page
     * @throws IOException
     */
    private List<Tuple> readFromFile() throws IOException {
        List<Tuple> tupleList = new ArrayList<>();
        int checkEndOfFile = fc.read(buffer);
        if (checkEndOfFile == -1) {
            return null;
        }
        buffer.clear();
        while (buffer.hasRemaining()) {
            if (i == 0) {
                tupleSize = buffer.getInt();
                i++;
            } else if (i == 1) {
                pageSize = buffer.getInt();
                maxPageSize = Math.max(pageSize, maxPageSize);
                pageToPageSize.add(pageSize);
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
                numTuplesSoFar++;
                tupleList.add(new Tuple(data.toString()));
            }
        }
        numberOfPages++;
        // Reference: https://stackoverflow.com/a/14937929/13636444
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
        return tupleList;
    }

    /**
     * Returns the next Tuple to be read
     *
     * @return the next Tuple to be read
     * @throws IOException
     */
    public Tuple readNextTuple() throws IOException {
        if (tupleList == null) {
            tupleList = readFromFile();
            resetIndexToZero();
            // End of All Pages
            if (tupleList == null) {
                return null;
            }
        }
        if (tupleNextIndex == tupleList.size()) {
            tupleList = readFromFile();
            resetIndexToZero();
            if (tupleList == null) {
                return null;
            }
            currentPage++;
            tupleNextIndex = 0;
        }
        tupleNextIndex++;
        return tupleList.get(tupleNextIndex - 1);
    }

    /**
     * Removes all opened and unused resources
     *
     * @throws IOException
     */
    public void close() throws IOException {
        fin.close();
    }

    /**
     * Resets the read to the beginning of the file
     *
     * @throws FileNotFoundException
     */
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
        numTuplesSoFar = 0;
    }

    public void resetToIndex(int index) throws IOException {
        fc.position(index);
        findNewPageInfo(index);
        resetIndexToNewIndex(index);
        resetTupleNextIndex(index);
    }

    private void findNewPageInfo(int index) {
        int indexPage = maxPageSize % index;
        pageSize = (int) pageToPageSize.get(indexPage);
    }

    private void resetIndexToNewIndex(int index) {
        i = index % pageSize + 1;
    }

    private void resetTupleNextIndex(int index) {
        tupleNextIndex = index % pageSize - 1;
    }

    private void resetIndexToZero() {
        i = 0;
    }

}
