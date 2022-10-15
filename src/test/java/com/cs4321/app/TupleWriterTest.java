package com.cs4321.app;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class TupleWriterTest {
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary";
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private static TupleWriter tupleWriter;
    private static TupleReader tupleReader;
    private static BufferedReader reader;
    private static final Logger logger = Logger.getInstance();

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }

    @After
    public void clean() throws IOException {
        tupleWriter.close();
        tupleReader.close();
        reader.close();
    }

    {
        try {
            File fileToWrite = File.createTempFile("temp", null);
            tupleWriter = new TupleWriter(fileToWrite.toString());
            tupleReader = new TupleReader(fileToWrite.toString());
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
        try {
            reader = new BufferedReader(new FileReader(dbc.tablePath("Boats_humanreadable")));
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    @Test
    void writeToFile() {
        try {
            // Write tuples to file
            String line = reader.readLine();
            while (line != null) {
                tupleWriter.writeToFile(new Tuple(line), false);
                line = reader.readLine();
            }
            tupleWriter.writeToFile(null, true);

            // Verify tuples written
            line = reader.readLine();
            while (line != null) {
                assertEquals(tupleReader.readNextTuple().toString(), line);
                line = reader.readLine();
            }

        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    @Test
    void close() throws IOException {
        tupleWriter.close();
        assertDoesNotThrow(() -> tupleWriter.writeToFile(new Tuple("1,2,3"), false));
    }

    @Test
    void reset() throws FileNotFoundException {
        tupleWriter.reset();
        assertDoesNotThrow(() -> tupleWriter.writeToFile(new Tuple("1,2,3"), false));
    }
}