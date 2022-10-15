package utils;

import com.cs4321.app.Tuple;
import com.cs4321.app.TupleWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class HumanReadableToBinaryUtil {
    private static String inputFilePath;
    private static String outputFilePath;
    private static BufferedReader reader;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Incorrect input format");
            return;
        }
        setInputFilePath(args[0]);
        setOutputFilePath(args[1]);
        try {
            reader = new BufferedReader(new FileReader(inputFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        dump(outputFilePath);
    }

    private static Tuple getNextTuple() {
        Tuple tuple = null;
        try {
            String line = reader.readLine();
            if (line != null) {
                tuple = new Tuple(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tuple;
    }

    /**
     * For binary files, calls getNextTuple() until the next tuple is null (no more output)
     * and writes each tuple to a provided filename.
     *
     * @param filename The name of the file that will contain the query results
     */
    private static void dump(String filename) {
        try {
            TupleWriter tupleWriter = new TupleWriter(filename);
            Tuple nextTuple = getNextTuple();
            while (nextTuple != null) {
                tupleWriter.writeToFile(nextTuple, false);
                nextTuple = getNextTuple();
            }
            tupleWriter.writeToFile(null, true);
            tupleWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the query output directory
     *
     * @return The empty Output Directory that contains the results of the queries
     */
    private static String getOutputFilePath() {
        return outputFilePath;
    }

    /**
     * Sets the query output directory
     *
     * @param outputFilePath The empty Output Directory that contains the results of the queries
     */
    private static void setOutputFilePath(String outputFilePath) {
        HumanReadableToBinaryUtil.outputFilePath = outputFilePath;
    }

    /**
     * Sets the query input directory
     *
     * @return The input directory, which contains a queries.sql file containing the sql queries.
     * a db subdirectory, which contains a schema.txt file specifying the schema for your
     * database as well as a data subdirectory, where the data itself is stored.
     */
    private static String getInputFilePath() {
        return inputFilePath;
    }

    /**
     * Returns the query input directory
     *
     * @param inputFilePath The input directory, which contains a queries.sql file containing the sql queries.
     *                      a db subdirectory, which contains a schema.txt file specifying the schema for your
     *                      database as well as a data subdirectory, where the data itself is stored.
     */
    private static void setInputFilePath(String inputFilePath) {
        HumanReadableToBinaryUtil.inputFilePath = inputFilePath;
    }
}
