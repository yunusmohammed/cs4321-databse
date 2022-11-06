package com.cs4321.app;

import net.sf.jsqlparser.statement.select.OrderByElement;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class SortingUtilities {

    private static Logger logger = Logger.getInstance();
    private static int numFilesSorted = 0;

    /**
     * Takes in a file, sorts the tuples in memory, and then writes out a sorted
     * file.
     * 
     * @param filename the name of the file to sort
     * @param outputFile the path to output the sorted file
     * @param indexInfo information on the index if sorting a relation on an attribute for a clustered index. Is null
     *                  if not building a clustered index.
     * @return the path to the sorted file
     */
    public static String sortFile(String filename, String outputFile, IndexInfo indexInfo) {
        String outputPath = null;
        try {
            TupleReader reader = new TupleReader(filename);
            List<Tuple> tuples = new ArrayList<>();
            Tuple tuple = reader.readNextTuple();
            while (tuple != null) {
                tuples.add(tuple);
                tuple = reader.readNextTuple();
            }
            Collections.sort(tuples, (a, b) -> compare(a, b, null, null, indexInfo));
            if(outputFile == null) outputPath = Files.createTempFile("" + numFilesSorted++, null).toString();
            else outputPath = outputFile;
            TupleWriter writer = new TupleWriter(outputPath);
            if (writer != null) {
                for (Tuple t : tuples) {
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
     * Compares Tuples [a] and [b] and returns a negative integer if [a] should be
     * placed before [b] in sorted order,
     * returns a positive integer if [b] should be placed before [a] in sorted
     * order, and returns 0 if [a] and [b] are equal.
     *
     * @param a                  the Tuple to compare [b] with
     * @param b                  the Tuple to compare [a] with
     * @param orderByElementList the list of column names in the order by clause.
     *                           Should be null if no order by clause
     *                           is used.
     * @param columnMap          a map from column names in the table to their
     *                           associated indexes. Should be null if no order
     *                           by clause is used.
     * @param indexInfo information on the index if sorting a relation on an attribute for a clustered index. Is null
     *      *           if not building a clustered index.
     * @return- an integer in accordance to the rules mentioned above
     */
    public static int compare(Tuple a, Tuple b, List<OrderByElement> orderByElementList,
            Map<String, Integer> columnMap, IndexInfo indexInfo) {
        HashSet<Integer> seenColumns = new HashSet<>();
        if (orderByElementList != null) {
            for (OrderByElement o : orderByElementList) {
                int index = columnMap.get(o.toString());
                seenColumns.add(index);
                int aVal = a.get(index), bVal = b.get(index);
                if (aVal != bVal)
                    return aVal - bVal;
            }
        }
        if(indexInfo != null) {
            String table = indexInfo.getRelationName();
            String column = indexInfo.getAttributeName();
            int index = DatabaseCatalog.getInstance().columnMap(table).get(column);
            seenColumns.add(index);
            int aVal = a.get(index), bVal = b.get(index);
            if(aVal != bVal)
                return aVal - bVal;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!seenColumns.contains(i)) {
                int aVal = a.get(i), bVal = b.get(i);
                if (aVal != bVal)
                    return aVal - bVal;
            }
        }
        return 0;
    }
}
