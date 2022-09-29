package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sf.jsqlparser.expression.Expression;

/**
 * Tests for LogicalSelectOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSelectionOperatorTest {

  @Test
  public void logicalSelectionOperatorCorrectlyInitializedTest() {
    LogicalScanOperator expectedChildOperator = Mockito.mock(LogicalScanOperator.class);
    Expression expectedExpression = Mockito.mock(Expression.class);
    LogicalSelectionOperator logicalSelectOperator = new LogicalSelectionOperator(expectedExpression,
        expectedChildOperator);
    assertEquals(expectedChildOperator, logicalSelectOperator.getChildOperator());
    assertEquals(expectedExpression, logicalSelectOperator.getSelectCondition());
  }
}
