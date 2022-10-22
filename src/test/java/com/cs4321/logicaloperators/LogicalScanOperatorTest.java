package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for LogicalScanOperator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalScanOperatorTest {

    @Test
    public void logicalScanOperatorCorrectlyInitializedTest() {
        Table t = new Table();
        AliasMap mockMap = Mockito.mock(AliasMap.class);
        LogicalScanOperator logicalScanOperator = new LogicalScanOperator(t, mockMap);
        assertEquals(t, logicalScanOperator.getTable());
        assertEquals(mockMap, logicalScanOperator.getAliasMap());
    }
}
