package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScanOperatorTest {
    private final ScanOperator scanOperator = new ScanOperator("Boats");
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary";

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }


    @Test
    void getNextTuple() {
        scanOperator.reset();
        Tuple tuple1 = new Tuple("12,143,196");
        assertEquals(tuple1, scanOperator.getNextTuple());

        Tuple tuple2 = new Tuple("30,63,101");
        assertEquals(tuple2, scanOperator.getNextTuple());

        Tuple tuple3 = new Tuple("57,24,130");
        assertEquals(tuple3, scanOperator.getNextTuple());
    }


    @Test
    void reset() {
        scanOperator.reset();
        Tuple tuple1 = new Tuple("12,143,196");
        assertEquals(tuple1, scanOperator.getNextTuple());
    }


}