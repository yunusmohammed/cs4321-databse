package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for LogicalDuplicateEliminationOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalDuplicateEliminationOperatorTest {
  LogicalOperator expectedChildOperator;

  @BeforeEach
  public void setup() {
    expectedChildOperator = Mockito.mock(LogicalOperator.class);
    Mockito.when(expectedChildOperator.toString(Mockito.anyInt())).thenCallRealMethod();
  }

  @Test
  public void logicalDuplicateEliminationOperatorCorrectlyInitializedTest() {
    LogicalDuplicateEliminationOperator logicalSelectOperator = new LogicalDuplicateEliminationOperator(
        expectedChildOperator);
    assertEquals(expectedChildOperator, logicalSelectOperator.getChild());
  }

  @Test
  public void testToString() {
    LogicalDuplicateEliminationOperator logicalDuplicateEliminationOperator = new LogicalDuplicateEliminationOperator(
        expectedChildOperator);

    // Dup Elim operator is at level 0
    String expecStringL0 = "DupElim\n-LogicalOperator\n";
    assertEquals(expecStringL0, logicalDuplicateEliminationOperator.toString(0));

    // Dup Elim operator is at level > 0, eg 3
    String expecStringL3 = "---DupElim\n----LogicalOperator\n";
    assertEquals(expecStringL3, logicalDuplicateEliminationOperator.toString(3));
  }
}
