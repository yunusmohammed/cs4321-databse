package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class QueryPlanTest {

    private Operator operator;
    private Operator childOperator;
    private final QueryPlan queryPlan = new QueryPlan(operator);
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
        queryPlan.setRoot(childOperator);
        assertEquals(childOperator, queryPlan.getRoot());
    }

    @Test
    void leftChild() {
        queryPlan.setLeftChild(childOperator);
        assertEquals(childOperator, queryPlan.getLeftChild().getRoot());
    }

    @Test
    void rightChild() {
        queryPlan.setRightChild(childOperator);
        assertEquals(childOperator, queryPlan.getRightChild().getRoot());
    }
}