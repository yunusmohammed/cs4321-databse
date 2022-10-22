package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import net.sf.jsqlparser.statement.select.SelectItem;
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

    @Test
    public void logicalProjectionOperatorCorrectlyInitializedTest() {
        LogicalOperator expectedChildOperator = Mockito.mock(LogicalOperator.class);
        List<SelectItem> expectedSelectItems = Mockito.mock(List.class);
        AliasMap expectedAliasMap = Mockito.mock(AliasMap.class);
        LogicalProjectionOperator logicalProjectionOperator = new LogicalProjectionOperator(expectedSelectItems,
                expectedChildOperator, expectedAliasMap);
        assertEquals(expectedChildOperator, logicalProjectionOperator.getChild());
        assertEquals(expectedSelectItems, logicalProjectionOperator.getSelectItems());
        assertEquals(expectedAliasMap, logicalProjectionOperator.getAliasMap());
    }

}
