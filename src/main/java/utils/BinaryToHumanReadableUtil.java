package utils;

import com.cs4321.app.Tuple;
import com.cs4321.app.TupleReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class BinaryToHumanReadableUtil {
    private static final String sep = File.separator;
    private static TupleReader tupleReader;
    private static String inputFilePath;
    private static String outputFilePath;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Incorrect input format");
            return;
        }
        setInputFilePath(args[0]);
        setOutputFilePath(args[1]);
        try {
            tupleReader = new TupleReader(inputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dump(new PrintStream(outputFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Tuple getNextTuple() {
        try {
            return tupleReader.readNextTuple();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * For human-readable files, calls getNextTuple() until the next tuple is null (no more output)
     * and writes each tuple to a suitable PrintStream.
     *
     * @param output The output stream to write to
     */
    private static void dump(PrintStream output) {
        Tuple nextTuple = getNextTuple();
        while (nextTuple != null) {
            output.println(nextTuple);
            nextTuple = getNextTuple();
        }
        output.close();
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
        BinaryToHumanReadableUtil.outputFilePath = outputFilePath;
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
     *                 a db subdirectory, which contains a schema.txt file specifying the schema for your
     *                 database as well as a data subdirectory, where the data itself is stored.
     */
    private static void setInputFilePath(String inputFilePath) {
        BinaryToHumanReadableUtil.inputFilePath = inputFilePath;
    }
}
