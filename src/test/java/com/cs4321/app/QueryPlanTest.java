package com.cs4321.app;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class QueryPlanTest {

    private Operator operator;
    private final QueryPlan queryPlan = new QueryPlan(null, 0);
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
    }

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