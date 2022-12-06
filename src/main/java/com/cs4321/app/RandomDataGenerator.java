package com.cs4321.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Creates a temporary file with random data according to a set of specifications.
 *
 * @author Yohanes Kidane
 */
public class RandomDataGenerator {

    private String tablePath;
    private int numTuples;
    private int numColumns;
    private String[] columnNames;
    private Logger logger = Logger.getInstance();
    private int minValue;
    private int maxValue;

    /**
     * Creates a new file and generates random data within the file. The number of columns and tuples, and the range of
     * values in the data will fall within a given range.
     *
     * @param tableName- the name of the newly created table. Requires: tableName is unique.
     * @param minTuples  - the minimum number of tuples in our table. Requires: minTuples >= 0
     * @param maxTuples  - the maximum Number of tuples in our table. Requires: maxTuples >= minTuples
     * @param minColumns - the minimum number of columns in our table. Requires: minColumns >= 0
     * @param maxColumns - the maximum number of columns in our table. Requires: maxColumns >= minColumns
     * @param minValue   - the minimum value for an attribute in the table. Requires: minValue >= 0
     * @param maxValue   - the maximum value for an attribute in the table. Requires: maxValue >= minValue
     */
    public RandomDataGenerator(String tableName, int minTuples, int maxTuples, int minColumns, int maxColumns, int minValue, int maxValue) {
        try {
            tablePath = Files.createTempFile(tableName, null).toString();
            this.minValue = minValue;
            this.maxValue = maxValue;
            numTuples = randomIntWithinRange(minTuples, maxTuples);
            numColumns = randomIntWithinRange(minColumns, maxColumns);
            columnNames = new String[numColumns];
            for (int i = 0; i < numColumns; i++) {
                Character letter = (char) ('A' + i);
                columnNames[i] = letter.toString();
            }
            TupleWriter writer = new TupleWriter(tablePath);
            for (int i = 0; i < numTuples; i++) {
                Tuple t = generateTuple();
                writer.writeToFile(t, false);
            }
            writer.writeToFile(null, true);
        } catch (IOException e) {
            logger.log("Unable to create random data table " + tableName + ".");
            throw new Error();
        }
    }

    public RandomDataGenerator(String filename) {
        try {
            List<String> readLines = Files.readAllLines(Paths.get(filename));
            for (String line : readLines) {
                String[] lineArr = line.split(" ");
                numColumns = lineArr.length - 2;
                columnNames = new String[numColumns];
                int[][] columnMaxMinArr = new int[numColumns][2];
                String tableName = lineArr[0];
                numTuples = Integer.parseInt(lineArr[1]);
                for (int i = 2; i < lineArr.length; i++) {
                    String[] columnMetaData = lineArr[i].split(",");
                    String columnName = columnMetaData[0];
                    columnNames[i - 2] = columnName;
                    columnMaxMinArr[i - 2][0] = Integer.parseInt(columnMetaData[1]);
                    columnMaxMinArr[i - 2][1] = Integer.parseInt(columnMetaData[2]);
                }
                tablePath = Files.createFile(Path.of(tableName)).toString();
                TupleWriter writer = new TupleWriter(tablePath);
                for (int i = 0; i < 499; i++) {
                    Tuple t = generateTuple(columnMaxMinArr);
                    writer.writeToFile(t, false);
                }
                for (int i = 0; i < 1500; i++) {
                    Tuple t = new Tuple("1,2,3");
                    writer.writeToFile(t, false);
                }
                Tuple t = new Tuple("0,1,2");
                writer.writeToFile(t, false);
                writer.writeToFile(null, true);
            }
        } catch (IOException e) {
            logger.log(e.getMessage());
            throw new Error();
        }
    }


//    public static void main(String[] args) {
//        String tableName = args[0];
//        int minTuples = Integer.parseInt(args[1]);
//        int maxTuples = Integer.parseInt(args[2]);
//        int minColumns = Integer.parseInt(args[3]);
//        int maxColumns = Integer.parseInt(args[4]);
//        int minValue = Integer.parseInt(args[5]);
//        int maxValue = Integer.parseInt(args[6]);
//        RandomDataGenerator randomDataGenerator = new RandomDataGenerator(tableName, minTuples, maxTuples, minColumns, maxColumns, minValue, maxValue);
//        System.out.println(Arrays.toString(randomDataGenerator.getColumnNames()));
//    }

    public static void main(String[] args) {
        String filename = args[0];
        new RandomDataGenerator(filename);
    }

    /**
     * Generates a random tuple where each attribute value lies between this.minValue and this.maxValue.
     *
     * @return - a random tuple of length numColumns.
     */
    private Tuple generateTuple() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numColumns; i++) {
            int value = randomIntWithinRange(minValue, maxValue);
            sb.append(value);
            if (i != numColumns - 1) sb.append(",");
        }
        return new Tuple(sb.toString());
    }

    /**
     * Generates a random tuple where each attribute value lies between this.minValue and this.maxValue.
     *
     * @return - a random tuple of length numColumns.
     */
    private Tuple generateTuple(int[][] columnMaxMinArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numColumns; i++) {
            int value = randomIntWithinRange(columnMaxMinArr[i][0], columnMaxMinArr[i][1]);
            sb.append(value);
            if (i != numColumns - 1) sb.append(",");
        }
        return new Tuple(sb.toString());
    }

    /**
     * Returns a random integer in the range [lowerBound, upperBound] (inclusive)
     *
     * @param lowerBound - the lower bound for the range. Requires: lowerBound >= 0
     * @param upperBound - the upper bound for the range. Requires: upperBound >= lowerBound
     * @return - a random integer in the given range
     */
    private int randomIntWithinRange(int lowerBound, int upperBound) {
        return ((int) (Math.random() * (upperBound - lowerBound + 1))) + lowerBound;
    }

    /**
     * ]
     * Returns the path to this table.
     *
     * @return - a string which contains the path to this table
     */
    public String getTablePath() {
        return tablePath;
    }

    /**
     * Returns the columnNames for this table.
     *
     * @return - a list of strings which contains the column names for this table
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Returns the number of tuples for this table.
     *
     * @return - the number of tuples in this table.
     */
    public int getNumTuples() {
        return numTuples;
    }

    /**
     * Returns the number of columns for this table.
     *
     * @return - the number of columns for this table.
     */
    public int getNumColumns() {
        return numColumns;
    }

}
