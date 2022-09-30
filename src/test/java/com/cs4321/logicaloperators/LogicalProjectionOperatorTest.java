package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.app.ColumnMap;

import net.sf.jsqlparser.statement.select.SelectItem;

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
    ColumnMap expectedColumnMap = Mockito.mock(ColumnMap.class);
    LogicalProjectionOperator logicalProjectionOperator = new LogicalProjectionOperator(expectedSelectItems,
        expectedChildOperator, expectedColumnMap);
    assertEquals(expectedChildOperator, logicalProjectionOperator.getChild());
    assertEquals(expectedSelectItems, logicalProjectionOperator.getSelectItems());
    assertEquals(expectedColumnMap, logicalProjectionOperator.getColumnMap());
  }

}
