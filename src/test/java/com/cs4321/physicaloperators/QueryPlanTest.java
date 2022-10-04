package com.cs4321.physicaloperators;

import com.cs4321.app.DatabaseCatalog;
import com.cs4321.physicaloperators.Operator;
import com.cs4321.physicaloperators.QueryPlan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryPlanTest {

    private Operator operator;
    private final QueryPlan queryPlan = new QueryPlan(null, 0);
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);}

    @Test
    void setRoot() {
        queryPlan.setRoot(operator);
        assertEquals(operator, queryPlan.getRoot());
    }

    @Test
    void getRoot() {
        assertEquals(operator, queryPlan.getRoot());
    }
}