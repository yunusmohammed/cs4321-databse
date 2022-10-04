package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.physicaloperators.JoinExpressionVisitor;

import net.sf.jsqlparser.expression.Expression;

/**
 * Tests for LogicalJoinOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalJoinOperatorTest {
  LogicalOperator expectedLeftChild;
  LogicalOperator expectedRightChild;
  Expression expectedJoinCondition;
  JoinExpressionVisitor expectedExpressionVisitor;

  @BeforeEach
  void setUp() {
    expectedLeftChild = Mockito.mock(LogicalOperator.class);
    expectedRightChild = Mockito.mock(LogicalOperator.class);
    expectedJoinCondition = Mockito.mock(Expression.class);
    expectedExpressionVisitor = Mockito.mock(JoinExpressionVisitor.class);
  }

  @Test
  public void logicalJoinOperatorCorrectlyInitializedTest() {
    LogicalJoinOperator logicalJoinOperator = new LogicalJoinOperator(expectedLeftChild, expectedRightChild,
        expectedJoinCondition, expectedExpressionVisitor);
    assertEquals(expectedLeftChild, logicalJoinOperator.getLeftChild());
    assertEquals(expectedRightChild, logicalJoinOperator.getRightChild());
    assertEquals(expectedJoinCondition, logicalJoinOperator.getJoinCondition());
  }

  @Test
  public void logicalJoinOperatorGettersAndSettersTest() {
    LogicalJoinOperator logicalJoinOperator = new LogicalJoinOperator();
    logicalJoinOperator.setLeftChild(expectedLeftChild);
    logicalJoinOperator.setRightChild(expectedRightChild);
    logicalJoinOperator.setJoinCondition(expectedJoinCondition);
    logicalJoinOperator.setJoinExpressionVisitor(expectedExpressionVisitor);
    assertEquals(expectedLeftChild, logicalJoinOperator.getLeftChild());
    assertEquals(expectedRightChild, logicalJoinOperator.getRightChild());
    assertEquals(expectedJoinCondition, logicalJoinOperator.getJoinCondition());
    assertEquals(expectedExpressionVisitor, logicalJoinOperator.getJoinExpressionVisitor());
  }
}
