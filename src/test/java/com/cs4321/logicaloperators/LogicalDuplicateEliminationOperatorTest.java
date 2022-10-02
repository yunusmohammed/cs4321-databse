package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for LogicalDuplicateEliminationOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalDuplicateEliminationOperatorTest {

  @Test
  public void logicalDuplicateEliminationOperatorCorrectlyInitializedTest() {
    LogicalOperator expectedChildOperator = Mockito.mock(LogicalOperator.class);
    LogicalDuplicateEliminationOperator logicalSelectOperator = new LogicalDuplicateEliminationOperator(
        expectedChildOperator);
    assertEquals(expectedChildOperator, logicalSelectOperator.getChild());
  }
}
