package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.app.ColumnMap;
import com.cs4321.physicaloperators.SelectExpressionVisitor;

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
    SelectExpressionVisitor expectedSelectExpressionVisitor = Mockito.mock(SelectExpressionVisitor.class);
    ColumnMap expectedColumnMap = Mockito.mock(ColumnMap.class);
    LogicalSelectionOperator logicalSelectOperator = new LogicalSelectionOperator(expectedExpression,
        expectedChildOperator, expectedSelectExpressionVisitor, expectedColumnMap);
    assertEquals(expectedChildOperator, logicalSelectOperator.getChild());
    assertEquals(expectedExpression, logicalSelectOperator.getSelectCondition());
    assertEquals(expectedSelectExpressionVisitor, logicalSelectOperator.getSelectExpressionVisitor());
    assertEquals(expectedColumnMap, logicalSelectOperator.getColumnMap());
  }
}
