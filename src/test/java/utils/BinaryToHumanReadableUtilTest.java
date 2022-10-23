package utils;

import com.cs4321.app.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class BinaryToHumanReadableUtilTest {

    private static final String sep = File.separator;
    String inputFilePath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary" + sep + "db" + sep + "data" + sep + "Boats";
    String expectedOutputFilePath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary" + sep + "db" + sep + "data" + sep + "Boats_humanreadable";
    private static String outputFilePath;
    private static final Logger logger = Logger.getInstance();

    static {
        try {
            outputFilePath = String.valueOf(Files.createTempFile("output", null));
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

//    @Test
//    void main() {
//        BinaryToHumanReadableUtil.main(new String[]{inputFilePath, outputFilePath});
//        try {
//            boolean equal = FileUtils.contentEquals(new File(outputFilePath), new File(expectedOutputFilePath));
//            assertTrue(equal);
//        } catch (Exception e) {
//            throw new Error("Binary and Human Readable files are not the same");
//        }
//    }
}