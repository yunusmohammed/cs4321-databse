package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.app.AliasMap;
import com.cs4321.physicaloperators.JoinExpressionVisitor;

import net.sf.jsqlparser.expression.Expression;

/**
 * Tests for LogicalJoinOperator
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public class OldLogicalJoinOperatorTest {
  LogicalOperator expectedLeftChild;
  LogicalOperator expectedRightChild;
  Expression expectedJoinCondition;
  JoinExpressionVisitor expectedExpressionVisitor;
  AliasMap aliasMap;

  @BeforeEach
  void setUp() {
    expectedLeftChild = Mockito.mock(LogicalOperator.class);
    expectedRightChild = Mockito.mock(LogicalOperator.class);
    expectedJoinCondition = Mockito.mock(Expression.class);
    expectedExpressionVisitor = Mockito.mock(JoinExpressionVisitor.class);
    aliasMap = Mockito.mock(AliasMap.class);
  }

  @Test
  public void logicalJoinOperatorCorrectlyInitializedTest() {
    OldLogicalJoinOperator logicalJoinOperator = new OldLogicalJoinOperator(expectedLeftChild, expectedRightChild,
        expectedJoinCondition, expectedExpressionVisitor, new ArrayList<>(), aliasMap);
    assertEquals(expectedLeftChild, logicalJoinOperator.getLeftChild());
    assertEquals(expectedRightChild, logicalJoinOperator.getRightChild());
    assertEquals(expectedJoinCondition, logicalJoinOperator.getJoinCondition());
  }

  @Test
  public void logicalJoinOperatorGettersAndSettersTest() {
    OldLogicalJoinOperator logicalJoinOperator = new OldLogicalJoinOperator();
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
