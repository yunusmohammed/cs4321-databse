package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class QueryPlanTest {

    private Operator operator;
    private final QueryPlan queryPlan = new QueryPlan(operator);
    private final ScanOperator scanOperator = new ScanOperator("Sailors");
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }

    @Test
    void getRoot() {
        assertEquals(operator, queryPlan.getRoot());
    }

    @Test
    void setRoot() {
        queryPlan.setRoot(scanOperator);
        assertEquals(scanOperator, queryPlan.getRoot());
    }

    @Test
    void leftChild() {
        queryPlan.setLeftChild(scanOperator);
        assertEquals(scanOperator, queryPlan.getLeftChild().getRoot());
    }

    @Test
    void rightChild() {
        queryPlan.setRightChild(scanOperator);
        assertEquals(scanOperator, queryPlan.getRightChild().getRoot());
    }
}