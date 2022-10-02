package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for LogicalScanOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalScanOperatorTest {

  @Test
  public void logicalScanOperatorCorrectlyInitializedTest() {
    String expectedBaseTableName = "testBaseTableName";
    LogicalScanOperator logicalScanOperator = new LogicalScanOperator("testBaseTableName");
    assertEquals(expectedBaseTableName, logicalScanOperator.getBaseTable());
  }
}
