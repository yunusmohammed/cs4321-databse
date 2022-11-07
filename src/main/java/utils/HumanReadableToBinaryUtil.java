package utils;

import com.cs4321.app.Logger;
import com.cs4321.app.Tuple;
import com.cs4321.app.TupleWriter;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utility file for converting from Human Readable files to Binary files
 *
 * @author Jessica
 */
public class HumanReadableToBinaryUtil {
    private static String inputFilePath;
    private static String outputFilePath;
    private static BufferedReader reader;
    private static Logger logger = Logger.getInstance();

//    public static void main(String[] args) {
//        if (args.length < 2) {
//            System.out.println("Incorrect input format");
//            return;
//        }
//        setInputFilePath(args[0]);
//        setOutputFilePath(args[1]);
//        try {
//            reader = new BufferedReader(new FileReader(inputFilePath));
//        } catch (FileNotFoundException e) {
//            logger.log(e.getMessage());
//        }
//        dump(outputFilePath);
//    }

    /**
     * Gets the next tuple to be read from the input file
     *
     * @return The next tuple to be read
     */
    private static Tuple getNextTuple() {
        Tuple tuple = null;
        try {
            String line = reader.readLine();
            if (line != null) {
                tuple = new Tuple(line);
            }
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        return tuple;
    }

    /**
     * Calls getNextTuple() until the next tuple is null (no more output)
     * and writes each tuple to a provided filename.
     *
     * @param filename The name of the written binary file
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
            logger.log(e.getMessage());
        }
    }

    /**
     * Sets the output file path
     *
     * @param outputFilePath The Output File that contains the written binary file
     */
    private static void setOutputFilePath(String outputFilePath) {
        HumanReadableToBinaryUtil.outputFilePath = outputFilePath;
    }

    /**
     * Returns the input file path
     *
     * @param inputFilePath The input File that contains the human readable file to be read
     */
    private static void setInputFilePath(String inputFilePath) {
        HumanReadableToBinaryUtil.inputFilePath = inputFilePath;
    }
}
