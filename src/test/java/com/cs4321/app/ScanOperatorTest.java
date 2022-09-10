package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScanOperatorTest {
    private final ScanOperator scanOperator = new ScanOperator("Sailors");
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }


    @Test
    void nextIndex() {
        scanOperator.setNextIndex(3);
        assertEquals(3, scanOperator.getNextIndex());
        scanOperator.reset();
    }


    @Test
    void getNextTuple() {
        Tuple tuple1 = new Tuple("1,200,50");
        assertEquals(tuple1, scanOperator.getNextTuple());

        Tuple tuple2 = new Tuple("2,200,200");
        assertEquals(tuple2, scanOperator.getNextTuple());

        Tuple tuple3 = new Tuple("3,100,105");
        assertEquals(tuple3, scanOperator.getNextTuple());
        scanOperator.reset();
    }


    @Test
    void reset() {
        scanOperator.setNextIndex(2);
        Tuple tuple3 = new Tuple("3,100,105");
        assertEquals(tuple3, scanOperator.getNextTuple());

        scanOperator.reset();

        assertEquals(0, scanOperator.getNextIndex());
        Tuple tuple1 = new Tuple("1,200,50");
        assertEquals(tuple1, scanOperator.getNextTuple());
    }


    @Test
    void dump() {
        assertDoesNotThrow(() -> scanOperator.dump());
    }


    @Test
    void queryOutputFileName() {
        scanOperator.setQueryOutputFileName("ABC");
        assertEquals("ABC", scanOperator.getQueryOutputFileName());
        File queryOutputFile = new File(scanOperator.getQueryOutputFileName());
        assert queryOutputFile.isFile();
        queryOutputFile.delete();
    }


}