package com.cs4321.app;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingUtilities {

    private static Logger logger = Logger.getInstance();

    /**
     * Takes in a file, sorts the tuples in memory, and then writes out a sorted file.
     * @param filename - the name of the file to sort
     * @param outputName - the name of the file to output. Must be unique.
     * @return - the path to the sorted file
     */
    public static String sortFile(String filename, String outputName) {
        String outputPath = null;
        try {
            TupleReader reader = new TupleReader(filename);
            List<Tuple> tuples = new ArrayList<>();
            Tuple tuple = reader.readNextTuple();
            while(tuple != null) {
                tuples.add(tuple);
                tuple = reader.readNextTuple();
            }
            Collections.sort(tuples, (a, b) -> compare(a, b));
            outputPath = Files.createTempFile(outputName, null).toString();
            TupleWriter writer = new TupleWriter(outputPath);
            if(writer != null) {
                for(Tuple t : tuples) {
                    writer.writeToFile(t, false);
                }
            }
            writer.writeToFile(null, true);
        } catch (IOException e) {
            logger.log("Unable to sort file " + filename + ".");
            throw new Error();
        }
        return outputPath;
    }

    /**
     * Returns -1 if a should be before b in sorted order, 0 if a and b are equivalent, and 1 otherwise.
     * Assumes a and b have the same size.
     * @param a - a tuple in the comparison
     * @param b - a tuple in the comparison
     * @return - an integer that determines whether a or b should come first
     */
    private static int compare(Tuple a, Tuple b) {
        for(int i=0; i<a.size(); i++) {
            int difference = a.get(i) - b.get(i);
            if(difference != 0) return difference;
        }
        return 0;
    }
}
