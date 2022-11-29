package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

/**
 * Tests for LogicalScanOperator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalScanOperatorTest {
    Table t;
    AliasMap mockMap;
    LogicalScanOperator logicalScanOperator;

    @BeforeEach
    public void setup() {
        t = Mockito.mock(Table.class);
        Mockito.when(t.getName()).thenReturn("Boats");

        mockMap = Mockito.mock(AliasMap.class);
        logicalScanOperator = new LogicalScanOperator(t, mockMap);
    }

    @Test
    public void logicalScanOperatorCorrectlyInitializedTest() {
        assertEquals(t, logicalScanOperator.getTable());
        assertEquals(mockMap, logicalScanOperator.getAliasMap());
    }

    @Test
    public void testToString() {
        // Scan operator is at level 0
        String expecStringL0 = "Leaf[Boats]\n";
        assertEquals(expecStringL0, logicalScanOperator.toString(0));

        // Scan operator is at level > 0, eg 3
        String expecStringL3 = "---Leaf[Boats]\n";
        assertEquals(expecStringL3, logicalScanOperator.toString(3));
    }
}
