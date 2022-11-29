package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Tests for LogicalSortOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSortOperatorTest {

  LogicalOperator expectedChildOperator;
  List<OrderByElement> expectedOrderByElementList;
  Map<String, Integer> expectedSortColumnMap;
  LogicalSortOperator logicalSortOperator;

  @BeforeEach
  public void setup() {
    expectedChildOperator = Mockito.mock(LogicalOperator.class);
    Mockito.when(expectedChildOperator.toString(Mockito.anyInt())).thenCallRealMethod();

    expectedOrderByElementList = Mockito.mock(List.class);
    Mockito.when(expectedOrderByElementList.toString()).thenReturn("[S.A]");

    expectedSortColumnMap = Mockito.mock(HashMap.class);

    logicalSortOperator = new LogicalSortOperator(expectedChildOperator, expectedSortColumnMap,
        expectedOrderByElementList);
  }

  @Test
  public void logicalSelectionOperatorCorrectlyInitializedTest() {
    assertEquals(expectedChildOperator, logicalSortOperator.getChild());
    assertEquals(expectedSortColumnMap, logicalSortOperator.getSortColumnMap());
    assertEquals(expectedOrderByElementList, logicalSortOperator.getOrderByElements());
  }

  @Test
  public void testToString() {

    // Sort operator is at level 0
    String expecStringL0 = "Sort[S.A]\n-LogicalOperator\n";
    assertEquals(expecStringL0, logicalSortOperator.toString(0));

    // Dup Elim operator is at level > 0, eg 3
    String expecStringL3 = "---Sort[S.A]\n----LogicalOperator\n";
    assertEquals(expecStringL3, logicalSortOperator.toString(3));

  }
}
