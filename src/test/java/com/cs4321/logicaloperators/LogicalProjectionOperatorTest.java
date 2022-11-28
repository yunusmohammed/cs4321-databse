package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import net.sf.jsqlparser.statement.select.SelectItem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for LogicalProjectionOperator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalProjectionOperatorTest {

    LogicalOperator expectedChildOperator;
    List<SelectItem> expectedSelectItems;
    AliasMap expectedAliasMap;
    LogicalProjectionOperator logicalProjectionOperator;

    @BeforeEach
    public void setup() {
        expectedChildOperator = Mockito.mock(LogicalOperator.class);
        Mockito.when(expectedChildOperator.toString(Mockito.anyInt())).thenCallRealMethod();

        expectedSelectItems = Mockito.mock(List.class);
        Mockito.when(expectedSelectItems.toString()).thenReturn("[S.A, R.G]");

        expectedAliasMap = Mockito.mock(AliasMap.class);
        logicalProjectionOperator = new LogicalProjectionOperator(expectedSelectItems,
                expectedChildOperator, expectedAliasMap);
    }

    @Test
    public void logicalProjectionOperatorCorrectlyInitializedTest() {
        assertEquals(expectedChildOperator, logicalProjectionOperator.getChild());
        assertEquals(expectedSelectItems, logicalProjectionOperator.getSelectItems());
        assertEquals(expectedAliasMap, logicalProjectionOperator.getAliasMap());
    }

    @Test
    public void testToString() {
        // Projection operator is at level 0
        String expecStringL0 = "Project[S.A, R.G]\n-LogicalOperator\n";
        assertEquals(expecStringL0, logicalProjectionOperator.toString(0));

        // Projection operator is at level > 0, eg 3
        String expecStringL3 = "---Project[S.A, R.G]\n----LogicalOperator\n";
        assertEquals(expecStringL3, logicalProjectionOperator.toString(3));
    }
}
