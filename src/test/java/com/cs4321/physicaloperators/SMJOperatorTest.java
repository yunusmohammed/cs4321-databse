package com.cs4321.physicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.app.Tuple;

import net.sf.jsqlparser.expression.Expression;

public class SMJOperatorTest {

  Operator leftChild;
  Operator rightChild;
  Expression joinCondition;
  JoinExpressionVisitor visitor;
  SMJOperator smj;

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

    smj = new SMJOperator(leftChild, rightChild, joinCondition, visitor);
  }

  @Test
  void testToStringForPrinting() {
    Mockito.when(leftChild.toString(Mockito.anyInt())).thenCallRealMethod();
    Mockito.when(rightChild.toString(Mockito.anyInt())).thenCallRealMethod();

    // BNLJ at depth 0 of physical query plan tree
    assertEquals("SMJ[S.A < T.B]\n-PhysicalOperator\n-PhysicalOperator\n", smj.toString(0));

    // BNLJ at depth 3 of physical query plan tree
    assertEquals("---SMJ[S.A < T.B]\n----PhysicalOperator\n----PhysicalOperator\n",
        smj.toString(3));
  }

}
