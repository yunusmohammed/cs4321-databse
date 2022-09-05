package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

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
    void getNextTuple() {
    }

    @Test
    void reset() {
    }

    @Test
    void dump() {
    }


    @Test
    void baseTable() {
    }

    @Test
    void nextIndex() {
    }


    @Test
    void queryOutputFileName() {
    }


    @Test
    void getTable() {
    }
}