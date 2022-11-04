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
    public void smjResetAfterReadingPage2() throws IOException {
        for (int i = 0; i < 640; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(500);
        assertEquals(tupleReader.readNextTuple().toString(), "86,180,110");
    }

    @Test
    public void smjResetAfterReadingPage3() throws IOException {
        for (int i = 0; i < 900; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(500);
        assertEquals(tupleReader.readNextTuple().toString(), "86,180,110");
    }

    @Test
    public void smjResetToFileStart() throws IOException {
        for (int i = 0; i < 900; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(0);
        String line = reader.readLine();
        while (line != null) {
            Tuple l = tupleReader.readNextTuple();
            assertEquals(l.toString(), line);
            line = reader.readLine();
        }
    }

    @Test
    public void smjResetToRandom() throws IOException {
        for (int i = 0; i < 900; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(789);
        assertEquals(tupleReader.readNextTuple().toString(), "179,172,49");
    }

    @Test
    public void smjResetToRandom2() throws IOException {
        for (int i = 0; i < 900; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(828);
        assertEquals(tupleReader.readNextTuple().toString(), "198,4,117");
    }

    @Test
    public void smjResetToRandom3() throws IOException {
        for (int i = 0; i < 340; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(8);
        assertEquals(tupleReader.readNextTuple().toString(), "1,146,192");
    }

    @Test
    public void smjResetToRandom4() throws IOException {
        for (int i = 0; i < 1000; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(999);
        assertEquals(tupleReader.readNextTuple().toString(), "44,39,136");
    }

    @Test
    public void smjResetToRandom5() throws IOException {
        for (int i = 0; i < 1000; i++) {
            tupleReader.readNextTuple();
        }
        tupleReader.smjReset(1000);
        assertEquals(tupleReader.readNextTuple(), null);
    }

    @Test
    void randomAccess() throws IOException {
        assertEquals(tupleReader.randomAccess(2, 2).toString(), "107,22,79");
        assertEquals(tupleReader.randomAccess(0, 5).toString(), "199,47,127");
        assertEquals(tupleReader.randomAccess(1, 9).toString(), "164,79,111");
    }

    @Test
    void indexReset() throws IOException {
        tupleReader.indexReset(2, 2);
        assertEquals(tupleReader.readNextTuple().toString(), "107,22,79");

        tupleReader.indexReset(0, 0);
        String line = reader.readLine();
        while (line != null) {
            Tuple l = tupleReader.readNextTuple();
            assertEquals(l.toString(), line);
            line = reader.readLine();
        }
    }
}