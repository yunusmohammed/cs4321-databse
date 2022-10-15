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
            e.printStackTrace();
        }
        try {
            reader = new BufferedReader(new FileReader(dbc.tablePath("Boats_humanreadable")));
        } catch (IOException e) {
            e.printStackTrace();
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
}