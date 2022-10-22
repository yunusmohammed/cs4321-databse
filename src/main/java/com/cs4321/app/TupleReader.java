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
    private int tupleNextIndex = 0;
    private List<Tuple> tupleList;
    private final int PAGE_SIZE = 4096;
    private final int SIZE_OF_AN_INTEGER = 4;
    private List pageToNumberOfTuplesOnPage;
    private int maximumNumberOfTuplesOnPage;
    private int tupleSize;
    private int numberOfPagesRead;

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
        this.pageToNumberOfTuplesOnPage = new ArrayList<>();
        this.maximumNumberOfTuplesOnPage = 0;
        this.tupleSize = 0;
        this.numberOfPagesRead = 0;
    }

    /**
     * Returns a list of all Tuples on the current page
     *
     * @return a list of all Tuples on the current page
     * @throws IOException
     */
    private List<Tuple> readFromFile(int startPostion, int pageSize, int tupleSize) throws IOException {
        List<Tuple> tupleList = new ArrayList<>();
        int checkEndOfFile = fc.read(buffer);
        if (checkEndOfFile == -1) {
            return null;
        }
        fc.position(numberOfPagesRead * PAGE_SIZE);
        buffer.clear();
        while (buffer.hasRemaining()) {
            if (startPostion == 0) {
                tupleSize = buffer.getInt();
                this.tupleSize = tupleSize;
                startPostion++;
            } else if (startPostion == 1) {
                pageSize = buffer.getInt();
                this.maximumNumberOfTuplesOnPage = pageSize;
                pageToNumberOfTuplesOnPage.add(pageSize);
                startPostion++;
            } else {
                if (tupleList.size() == pageSize) {
                    break;
                }
                StringBuilder data = new StringBuilder();
                for (int idx = 0; idx < tupleSize; idx++) {
                    data.append(buffer.getInt());
                    startPostion++;
                    if (idx != tupleSize - 1) {
                        data.append(",");
                    }
                }
                tupleList.add(new Tuple(data.toString()));
            }
        }
        // Reference: https://stackoverflow.com/a/14937929/13636444
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
        return tupleList;
    }

    /**
     * Returns a list of all Tuples on the current page
     *
     * @return a list of all Tuples on the current page
     * @throws IOException
     */
    private List<Tuple> readFromFile() throws IOException {
        return readFromFile(0, 0, 0);
    }

    /**
     * Returns the next Tuple to be read
     *
     * @return the next Tuple to be read
     * @throws IOException
     */
    public Tuple readNextTuple() throws IOException {
        if (tupleList == null) {
            numberOfPagesRead++;
            tupleList = readFromFile();
            tupleNextIndex = 0;
            // End of All Pages
            if (tupleList == null) {
                return null;
            }
        }
        if (tupleNextIndex == tupleList.size()) {
            numberOfPagesRead++;
            tupleList = readFromFile();
            if (tupleList == null) {
                return null;
            }
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
        tupleNextIndex = 0;
        tupleList = null;
        buffer.clear();
        fin = new FileInputStream(filename);
        fc = fin.getChannel();
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
        numberOfPagesRead = 0;
        pageToNumberOfTuplesOnPage = new ArrayList<>();
        maximumNumberOfTuplesOnPage = 0;
        tupleSize = 0;
        numberOfPagesRead = 0;
    }

    /**
     * Resets the tuple reader to start reading tuples from the tupleRow specified
     * Implementation assumes that the tupleRow has already previously been accessed
     *
     * @param tupleRow 0 indexed tupleRow to set tuple reader to
     * @throws IOException
     */
    public void smjReset(int tupleRow) throws IOException {
        // Figure out what page this index is supposed to be on
        int pageIndexIsOn = tupleRow / maximumNumberOfTuplesOnPage;
        int numberOfTuplesOnPage = maximumNumberOfTuplesOnPage;
        if (pageIndexIsOn != 0) {
            numberOfTuplesOnPage = (int) pageToNumberOfTuplesOnPage.get(pageIndexIsOn - 1);
        }

        // Calculate number of bytes I now need to read
        int numberOfTuplesFromPageStart = (tupleRow) % numberOfTuplesOnPage;
        int numberOfBytesTillTuple = (SIZE_OF_AN_INTEGER * 2) + (PAGE_SIZE * pageIndexIsOn) + (SIZE_OF_AN_INTEGER * numberOfTuplesFromPageStart * tupleSize);

        // Set file channel
        fc.position(numberOfBytesTillTuple);

        // Set buffer
        buffer = ByteBuffer.allocate(PAGE_SIZE);

        // Read tuples
        tupleNextIndex = 0;
        numberOfPagesRead = pageIndexIsOn + 1;
        tupleList = readFromFile(
                numberOfTuplesFromPageStart + 2,
                numberOfTuplesOnPage - numberOfTuplesFromPageStart,
                tupleSize);
    }

}
