package com.cs4321.physicaloperators;

import com.cs4321.app.*;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Operator for handling external sorts for order by clauses.
 * @author Yohanes
 */
public class ExternalSortOperator extends Operator{

    private Operator child;
    private Map<String, Integer> columnMap;
    private List<OrderByElement> orderByElementList;
    private PriorityQueue<Tuple> tuples;
    private Path tempFolderName;
    private int bufferSize;
    private TupleReader sortedReader;
    private Logger logger = Logger.getInstance();
    private List<String> currentPassFilenames;
    private static int numOperators = 1;

    /**
     * Creates an operator to represent an order by clause without needing to perform an in-memory sort.
     * This operator is at the root of the query plan unless there is a distinct clause.
     * @param child - the rest of the query plan, besides a potential DuplicateEliminationOperator.
     * @param columnMap - a map from column names in the table to their associated indexes.
     * @param orderByElementList - the list of elements for which our order by clause will sort.
     * @param tempFolderDir - the path to the folder we will use to store our temporary files.
     * @param bufferSize - the number of pages available to be used in memory. Requires: bufferSize >= 3
     */
    public ExternalSortOperator(Operator child, Map<String, Integer> columnMap, List<OrderByElement> orderByElementList, String tempFolderDir, int bufferSize) {
        try {
            this.child = child;
            this.columnMap = columnMap;
            this.orderByElementList = orderByElementList;
            File tempDir = new File(tempFolderDir);
            this.tempFolderName = Files.createTempDirectory(tempDir.toPath(), "" + numOperators++);
            this.bufferSize = bufferSize;
            currentPassFilenames = new ArrayList<>();
            createSortedRelation();
        } catch (IOException e) {
            logger.log("Unable to create external sort operator.");
            throw new Error();
        }
    }

    /**
     * Performs the external merge sort with [bufferSize] buffer pages.
     * @throws IOException - throws an exception if an error occurs on IO
     */
    private void createSortedRelation() throws IOException {
        Tuple cur = child.getNextTuple();
        if(cur == null) return;
        int maxTuples = (bufferSize * 4096) / (4 * cur.size());
        int numFiles = 1;
        // pass 0
        while(cur != null) {
            String outputFile = Files.createTempFile(tempFolderName, "" + numFiles++, "").toString();
            currentPassFilenames.add(outputFile);
            TupleWriter writer = new TupleWriter(outputFile);
            List<Tuple> sortedTuples = new ArrayList<>();
            for(int i=0; i<maxTuples && cur != null; i++) {
                sortedTuples.add(cur);
                cur = child.getNextTuple();
            }
            Collections.sort(sortedTuples, (a, b) -> SortingUtilities.compare(a, b, orderByElementList, columnMap));
            for(Tuple t : sortedTuples) {
                writer.writeToFile(t, false);
            }
            writer.writeToFile(null, true);
        }
        // rest of passes
        while(currentPassFilenames.size() > 1) {
            List<String> nextPassFilenames = new ArrayList<>();
            int index = 0;
            while(index < currentPassFilenames.size()) {
                // [buffersize - 1] way merge
                PriorityQueue<Map.Entry<Tuple, TupleReader>> nextTuples =
                        new PriorityQueue<>((a, b) -> SortingUtilities.compare(a.getKey(), b.getKey(), orderByElementList, columnMap));
                // In the current pass, we have [currentPassFilenames.size()] runs, and we want to merge [bufferSize - 1]
                // runs at a time.
                for(int i=index; i<index + bufferSize - 1 && i < currentPassFilenames.size(); i++) {
                    TupleReader reader = new TupleReader(currentPassFilenames.get(i));
                    Tuple readerTuple = reader.readNextTuple();
                    if(readerTuple != null) nextTuples.add(new AbstractMap.SimpleEntry<>(readerTuple, reader));
                }
                String outputFile = Files.createTempFile(tempFolderName, "" + numFiles++, "").toString();
                nextPassFilenames.add(outputFile);
                TupleWriter writer = new TupleWriter(outputFile);
                while(nextTuples.size() > 0) {
                    Map.Entry<Tuple, TupleReader> nextPair = nextTuples.poll();
                    writer.writeToFile(nextPair.getKey(), false);
                    Tuple readerTuple = nextPair.getValue().readNextTuple();
                    if(readerTuple != null) nextTuples.add(new AbstractMap.SimpleEntry<>(readerTuple, nextPair.getValue()));
                }
                writer.writeToFile(null, true);
                index += bufferSize - 1;
            }
            currentPassFilenames = nextPassFilenames;
        }
        if(currentPassFilenames.size() == 1) sortedReader= new TupleReader(currentPassFilenames.get(0));
    }

    /**
     * Returns the next tuple in ascending order determined by the columns in the
     * order by clause.
     * @return- the next tuple in sorted order.
     */
    @Override
    public Tuple getNextTuple() {
        try {
            if(sortedReader == null) return null;
            return sortedReader.readNextTuple();
        } catch (IOException e) {
            logger.log("Unable to read tuple from sorted reader in External Sort Operator.");
            throw new Error();
        }
    }

    /**
     * Resets the operator so that it can read tuples from the beginning.
     */
    @Override
    public void reset() {
        try {
            if(sortedReader != null) sortedReader.reset();
        } catch (FileNotFoundException e) {
            logger.log("Unable to reset sorted reader in External Sort Operator");
            throw new Error();
        }

    }

    /**
     * Returns a string representation of this operator.
     * @return- a string representation of this operator.
     */
    @Override
    public String toString() {
        StringBuilder orderBy = new StringBuilder();
        orderBy.append("Order By : ");
        for(int i=0; i<orderByElementList.size(); i++) {
            orderBy.append(orderByElementList.get(i).toString());
            if(i < orderByElementList.size() - 1) orderBy.append(", ");
        }
        return "ExternalSortOperator{" + child.toString() + ", " + orderBy + "}";
    }

    /**
     * Closes the query so that there cannot be more calls to getNextTuple.
     */
    @Override
    public void finalize() {
        child.finalize();
    }
}
