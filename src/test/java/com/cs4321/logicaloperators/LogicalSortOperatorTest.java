package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Tests for LogicalSortOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSortOperatorTest {

  @Test
  public void logicalSelectionOperatorCorrectlyInitializedTest() {
    LogicalOperator expectedChildOperator = Mockito.mock(LogicalOperator.class);
    List<OrderByElement> expectedOrderByElementList = Mockito.mock(List.class);
    Map<String, Integer> expectedSortColumnMap = Mockito.mock(HashMap.class);
    LogicalSortOperator logicalSortOperator = new LogicalSortOperator(expectedChildOperator, expectedSortColumnMap,
        expectedOrderByElementList, false);
    assertEquals(expectedChildOperator, logicalSortOperator.getChild());
    assertEquals(expectedSortColumnMap, logicalSortOperator.getSortColumnMap());
    assertEquals(expectedOrderByElementList, logicalSortOperator.getOrderByElements());
  }
}
