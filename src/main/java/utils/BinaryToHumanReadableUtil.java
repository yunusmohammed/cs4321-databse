package utils;

import com.cs4321.app.Logger;
import com.cs4321.app.Tuple;
import com.cs4321.app.TupleReader;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Utility file for converting from Binary files to Human Readable files
 *
 * @author Jessica
 */
public class BinaryToHumanReadableUtil {
    private static final String sep = File.separator;
    private static TupleReader tupleReader;
    private static String inputFilePath;
    private static String outputFilePath;
    private static Logger logger = Logger.getInstance();

//    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("Incorrect input format");
//            return;
//        }
//        setInputFilePath(args[0]);
//        setOutputFilePath(args[1]);
//        try {
//            tupleReader = new TupleReader(inputFilePath);
//        } catch (IOException e) {
//            logger.log(e.getMessage());
//        }
//        try {
//            dump(new PrintStream(outputFilePath));
//        } catch (FileNotFoundException e) {
//            logger.log(e.getMessage());
//        }
//    }

    /**
     * Gets the next tuple to be read from the input file
     *
     * @return The next tuple to be read
     */
    private static Tuple getNextTuple() {
        try {
            return tupleReader.readNextTuple();
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return null;
    }

    /**
     * Converts the input file from binary to human-readable and stores it at the output file path
     *
     * @param inputFile  - the path to the input file
     * @param outputFile - the path to the output file
     */
    public static void binaryToHuman(String inputFile, String outputFile) {
        try {
            tupleReader = new TupleReader(inputFile);
            dump(new PrintStream(outputFile));
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
    }

    /**
     * Calls getNextTuple() until the next tuple is null (no more output)
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
     * Sets the output file path
     *
     * @param outputFilePath The Output File that contains the written human
     *                       readable file
     */
    private static void setOutputFilePath(String outputFilePath) {
        BinaryToHumanReadableUtil.outputFilePath = outputFilePath;
    }

    /**
     * Returns the input file path
     *
     * @param inputFilePath The input File that contains the binary file to be read
     */
    private static void setInputFilePath(String inputFilePath) {
        BinaryToHumanReadableUtil.inputFilePath = inputFilePath;
    }
}
