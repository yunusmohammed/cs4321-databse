package utils;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HumanReadableToBinaryUtilTest {
    private static final String sep = File.separator;
    String inputFilePath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary" + sep + "db" + sep + "data" + sep + "Boats_humanreadable";
    String expectedOutputFilePath = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary" + sep + "db" + sep + "data" + sep + "Boats";
    private static String outputFilePath;

    static {
        try {
            outputFilePath = String.valueOf(Files.createTempFile("output", null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void main() {
        HumanReadableToBinaryUtil.main(new String[]{inputFilePath, outputFilePath});
        try {
            boolean equal = FileUtils.contentEquals(new File(outputFilePath), new File(expectedOutputFilePath));
            assertTrue(equal);
        } catch (Exception e) {
            throw new Error("Binary and Human Readable files are not the same");
        }
    }
}