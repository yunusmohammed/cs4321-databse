package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class FullScanOperatorTest {
    private static FullScanOperator scanOperator;
    private static final String sep = File.separator;
    private static final String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep
            + "resources" + sep + "input_binary";
    private final DatabaseCatalog dbc = DatabaseCatalog.getInstance();

    @BeforeAll
    static void setup() {
        DatabaseCatalog.setInputDir(inputdir);
        Table table = new Table();
        table.setName("Boats");
        AliasMap mockMap = Mockito.mock(AliasMap.class);
        Mockito.when(mockMap.getBaseTable(any())).thenReturn("Boats");
        scanOperator = new FullScanOperator(table, mockMap);
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

    @Test
    void toStringTest() {
        String s = String.format("ScanOperator{baseTablePath='%s'}", dbc.tablePath("Boats"));
        assertEquals(s, scanOperator.toString());
    }

}