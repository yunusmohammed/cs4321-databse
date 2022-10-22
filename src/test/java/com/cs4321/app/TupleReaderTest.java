package com.cs4321.app;

import org.junit.After;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TupleReaderTest {
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary";
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private static TupleReader tupleReader;
    private static BufferedReader reader;
    private static final Logger logger = Logger.getInstance();

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }

    @After
    public void clean() throws IOException {
        tupleReader.close();
        reader.close();
    }

    {
        try {
            tupleReader = new TupleReader(dbc.tablePath("Boats"));
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
    void readNextTuple() throws IOException {
        String line = reader.readLine();
        while (line != null) {
            assertEquals(tupleReader.readNextTuple().toString(), line);
            line = reader.readLine();
        }
    }

    @Test
    void close() throws IOException {
        tupleReader.close();
        assertThrows(IOException.class, () -> tupleReader.readNextTuple());
    }

    @Test
    void reset() throws IOException {
        tupleReader.reset();
        assertEquals(tupleReader.readNextTuple().toString(), "12,143,196");
    }

    @Test
    public void smjReset() throws IOException {
        for (int i = 0; i < 640; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(500);
        assertEquals(tupleReader.readNextTuple().toString(), "86,180,110");
    }

    @Test
    public void smjResetAnother() throws IOException {
        for (int i = 0; i < 900; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(500);
        assertEquals(tupleReader.readNextTuple().toString(), "86,180,110");
    }

    @Test
    public void yetAnotherSmjReset() throws IOException {
        for (int i = 0; i < 900; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(0);
        assertEquals(tupleReader.readNextTuple().toString(), "12,143,196");
    }
}