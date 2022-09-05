package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ScanQueryPlanTest {
    private final ScanOperator scanOperator = new ScanOperator("Sailors");
    private final ScanQueryPlan scanQueryPlan = new ScanQueryPlan(scanOperator);
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }


    @Test
    void evaluate() {
        assertDoesNotThrow(scanQueryPlan::evaluate);
    }
}