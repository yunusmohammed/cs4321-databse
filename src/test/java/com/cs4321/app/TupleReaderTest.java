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
        tupleReader.reset();
    }

    @Test
    void reset() throws IOException {
        tupleReader.reset();
        assertEquals(tupleReader.readNextTuple().toString(), "12,143,196");
    }

    @Test
    public void resetToPage() throws IOException {
        tupleReader.reset();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.readNextTuple();
        tupleReader.resetToIndex(5);
        assertEquals(tupleReader.readNextTuple().toString(), "61,58,36");
    }

    @Test
    public void anotherResetToPage() throws IOException {
        tupleReader.reset();
        for (int i = 0; i < 400; i++) {
            tupleReader.readNextTuple();
        }
        assertEquals(tupleReader.readNextTuple().toString(), "93,146,184");
        tupleReader.resetToIndex(5);
        assertEquals(tupleReader.readNextTuple().toString(), "61,58,36");
        tupleReader.reset();
        for (int i = 0; i < 700; i++) {
            tupleReader.readNextTuple();
        }
        assertEquals(tupleReader.readNextTuple().toString(), "173,94,10");
        tupleReader.resetToIndex(5);
        assertEquals(tupleReader.readNextTuple().toString(), "61,58,36");
    }
}