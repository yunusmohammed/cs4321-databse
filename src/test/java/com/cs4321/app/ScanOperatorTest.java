package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        scanOperator.setNextIndex(scanOperator.getNextIndex() + 1);

        Tuple tuple2 = new Tuple("2,200,200");
        assertEquals(tuple2, scanOperator.getNextTuple());
        scanOperator.setNextIndex(scanOperator.getNextIndex() + 1);

        Tuple tuple3 = new Tuple("3,100,105");
        assertEquals(tuple3, scanOperator.getNextTuple());
        scanOperator.reset();
    }


    @Test
    void reset() {
        scanOperator.setNextIndex(3);
        scanOperator.reset();
        assertEquals(0, scanOperator.getNextIndex());
    }


    @Test
    void dump() {
        assertDoesNotThrow(scanOperator::dump);
    }


    @Test
    void baseTable() {
        List<Tuple> sailorsTable = scanOperator.getBaseTable();
        Tuple tuple1 = new Tuple("1,200,50");
        assertEquals(tuple1, sailorsTable.get(0));

        Tuple tuple2 = new Tuple("2,200,200");
        assertEquals(tuple2, sailorsTable.get(1));

        Tuple tuple3 = new Tuple("3,100,105");
        assertEquals(tuple3, sailorsTable.get(2));
    }


    @Test
    void queryOutputFileName() {
        scanOperator.setQueryOutputFileName("ABC");
        assertEquals("ABC", scanOperator.getQueryOutputFileName());
        File queryOutputFile = new File(scanOperator.getQueryOutputFileName());
        assert queryOutputFile.isFile();
        queryOutputFile.delete();
    }


    @Test
    void getTable() {
        List<Tuple> boatsTable = scanOperator.getTable("Boats");
        List<Tuple> reservesTable = scanOperator.getTable("Reserves");
        List<Tuple> emptyTable = scanOperator.getTable("Empty");

        // boats table rows
        assertEquals(new Tuple("101,2,3"), boatsTable.get(0));
        assertEquals(new Tuple("102,3,4"), boatsTable.get(1));
        assertEquals(new Tuple("104,104,2"), boatsTable.get(2));
        assertEquals(new Tuple("107,2,8"), boatsTable.get(4));

        // reserves table rows
        assertEquals(new Tuple("1,101"), reservesTable.get(0));
        assertEquals(new Tuple("4,104"), reservesTable.get(5));

        // empty table
        assertEquals(0, emptyTable.size());
    }
}