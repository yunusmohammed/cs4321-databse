package com.cs4321.physicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doAnswer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;

import com.cs4321.app.Tuple;

import net.sf.jsqlparser.expression.Expression;

public class BNLJoinOperatorTests {

  Operator leftChild;
  Operator rightChild;
  Expression joinCondition;
  JoinExpressionVisitor visitor;
  BNLJoinOperator bnlj;

  @BeforeEach
  void setUp() {
    leftChild = Mockito.mock(Operator.class);
    Mockito.when(leftChild.toString()).thenReturn("Operator{}");
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3,4"));

    rightChild = Mockito.mock(Operator.class);
    Mockito.when(rightChild.toString()).thenReturn("Operator{}");

    joinCondition = Mockito.mock(Expression.class);
    Mockito.when(joinCondition.toString()).thenReturn("S.A < T.B");

    visitor = Mockito.mock(JoinExpressionVisitor.class);

    bnlj = new BNLJoinOperator(leftChild, rightChild, joinCondition, visitor, 1, null);
  }

  @Test
  void testTupleBufferSize() {
    // Buffer Size of 1 and tuple with 4 attributes
    assertEquals(256, bnlj.getTupleBufferSize());

    // Buffer Size of > 1 and tuple with 4 attributes
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3,4"));
    bnlj = new BNLJoinOperator(leftChild, rightChild, joinCondition, visitor, 2, null);
    assertEquals(512, bnlj.getTupleBufferSize());

    // Tuple of 1 attribute
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("10"));
    bnlj = new BNLJoinOperator(leftChild, rightChild, joinCondition, visitor, 1, null);
    assertEquals(1024, bnlj.getTupleBufferSize());

    // Tuple of 6 attribute
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3,4,5,6"));
    bnlj = new BNLJoinOperator(leftChild, rightChild, joinCondition, visitor, 1, null);
    assertEquals(170, bnlj.getTupleBufferSize());

    // Buffer size of Empty Left Child
    Mockito.when(leftChild.getNextTuple()).thenReturn(null);
    bnlj = new BNLJoinOperator(leftChild, rightChild, joinCondition, visitor, 1, null);
    assertEquals(0, bnlj.getTupleBufferSize());
  }

  @Test
  void testGetNextTuple() {
    bnlj.setTupleBufferSize(2);
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

    doAnswer(_invocation -> {
      Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("7,8"), new Tuple("9,10"), null);
      return null;
    }).when(rightChild).reset();

    // Left child returns null
    leftChild = Mockito.mock(Operator.class);
    bnlj.setLeftChild(leftChild);
    Mockito.when(leftChild.getNextTuple()).thenReturn(null);
    assertNull(bnlj.getNextTuple());

    rightChild.reset();

    // Current row fails expression and is last row in both left and right children
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), null);
    Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), null);
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
    assertNull(bnlj.getNextTuple());
    bnlj.reset();

    // Current row fails expression but next row from right child passes
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), null);
    Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), new Tuple("6,7"), null);
    Mockito
        .when(
            visitor.evalExpression(Mockito.any(), Mockito.any(), AdditionalMatchers.not(Mockito.eq(new Tuple("6,7")))))
        .thenReturn(false);
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.eq(new Tuple("6,7")))).thenReturn(true);
    assertEquals(new Tuple("1,2,3,6,7"), bnlj.getNextTuple());
    bnlj.reset();

    // Current row fails expression but next row from left child passes
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("6,7,8"), null);
    Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), null);
    Mockito
        .when(
            visitor.evalExpression(Mockito.any(), AdditionalMatchers.not(Mockito.eq(new Tuple("6,7,8"))),
                Mockito.any()))
        .thenReturn(false);
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.eq(new Tuple("6,7,8")), Mockito.any())).thenReturn(true);
    assertEquals(new Tuple("6,7,8,4,5"), bnlj.getNextTuple());
    bnlj.reset();

    // Current row passes expression
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), null);
    Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), null);
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
    assertEquals(new Tuple("1,2,3,4,5"), bnlj.getNextTuple());
    bnlj.reset();

    // leftChild fully fills 1 page
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("4,5,6"), null);
    assertEquals(new Tuple("1,2,3,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("4,5,6,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("1,2,3,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("4,5,6,9,10"), bnlj.getNextTuple());
    assertNull(bnlj.getNextTuple());
    bnlj.reset();

    // leftChild fully fills 2 page
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("4,5,6"), new Tuple("11,12,13"),
        new Tuple("14,15,16"), null);
    assertEquals(new Tuple("1,2,3,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("4,5,6,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("1,2,3,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("4,5,6,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("11,12,13,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("14,15,16,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("11,12,13,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("14,15,16,9,10"), bnlj.getNextTuple());
    assertNull(bnlj.getNextTuple());
    bnlj.reset();

    // leftChild partially fills 2 page
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("4,5,6"), new Tuple("11,12,13"),
        null);
    assertEquals(new Tuple("1,2,3,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("4,5,6,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("1,2,3,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("4,5,6,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("11,12,13,7,8"), bnlj.getNextTuple());
    assertEquals(new Tuple("11,12,13,9,10"), bnlj.getNextTuple());
    assertNull(bnlj.getNextTuple());
    bnlj.reset();

    // bnlj init with empty left child
    leftChild = Mockito.mock(Operator.class);
    bnlj = new BNLJoinOperator(leftChild, rightChild, joinCondition, visitor, 4, null);
    assertNull(bnlj.getNextTuple());
    bnlj.reset();

  }

  @Test
  void testReset() {
    bnlj.setTupleBufferSize(2);
    Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("6,7,8"), null);
    Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), new Tuple("9,10"), null);
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);

    doAnswer(_invocation -> {
      Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("6,7,8"), null);
      return null;
    }).when(leftChild).reset();

    doAnswer(_invocation -> {
      Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), new Tuple("9,10"), null);
      return null;
    }).when(rightChild).reset();

    while (bnlj.getNextTuple() != null) {
      ;
    }
    assertNull(bnlj.getNextTuple());
    bnlj.reset();

    assertEquals(new Tuple("1,2,3,4,5"), bnlj.getNextTuple());
    assertEquals(new Tuple("6,7,8,4,5"), bnlj.getNextTuple());
    assertEquals(new Tuple("1,2,3,9,10"), bnlj.getNextTuple());
    assertEquals(new Tuple("6,7,8,9,10"), bnlj.getNextTuple());
    assertNull(bnlj.getNextTuple());
  }

  @Test
  void testToString() {
    // BNLJoinOperator with Non-joinOperator children
    assertEquals("BNLJoinOperator{Operator{}, Operator{}, S.A < T.B}", bnlj.toString());

    // joinOperator with BNLJoinOperator child
    Operator newOperator = Mockito.mock(Operator.class);
    Mockito.when(newOperator.toString()).thenReturn("Operator{}");
    Expression newJoinCondition = Mockito.mock(Expression.class);
    Mockito.when(newJoinCondition.toString()).thenReturn("R.C < S.B");
    Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"));
    Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
    BNLJoinOperator bnljWithJoinChild = new BNLJoinOperator(bnlj, newOperator, newJoinCondition, visitor, 1, null);
    assertEquals("BNLJoinOperator{BNLJoinOperator{Operator{}, Operator{}, S.A < T.B}, Operator{}, R.C < S.B}",
        bnljWithJoinChild.toString());

    // joinOperator with no join Condition
    bnlj.setJoinCondition(null);
    assertEquals("BNLJoinOperator{Operator{}, Operator{}, null}", bnlj.toString());
  }
}
