package com.cs4321.logicaloperators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.cs4321.app.UnionFind;

import net.sf.jsqlparser.expression.Expression;

public class LogicalJoinOperatorTest {

  List<LogicalOperator> children;
  LogicalJoinOperator logicalJoinOperator;
  UnionFind unionFind;
  Expression joinCondition;

  @BeforeEach
  public void setup() {
    children = new ArrayList<>();

    LogicalOperator child1 = Mockito.mock(LogicalOperator.class);
    Mockito.when(child1.toString(Mockito.anyInt())).thenCallRealMethod();
    children.add(child1);

    LogicalOperator child2 = Mockito.mock(LogicalOperator.class);
    Mockito.when(child2.toString(Mockito.anyInt())).thenCallRealMethod();
    children.add(child2);

    unionFind = Mockito.mock(UnionFind.class);
    Mockito.when(unionFind.toString())
        .thenReturn("[[S.B, R.G], equals null, min null, max null]\n[[S.A, B.D], equals null, min null, max null]");

    joinCondition = Mockito.mock(Expression.class);
    Mockito.when(joinCondition.toString()).thenReturn("R.H <> B.D");

    logicalJoinOperator = new LogicalJoinOperator(joinCondition, children, unionFind);
  }

  @Test
  public void testToString() {
    // Projection operator is at level 0
    String expecStringL0 = "Join[R.H <> B.D]\n[[S.B, R.G], equals null, min null, max null]\n[[S.A, B.D], equals null, min null, max null]\n-LogicalOperator\n-LogicalOperator\n";
    assertEquals(expecStringL0, logicalJoinOperator.toString(0));

    // Projection operator is at level > 0, eg 3
    String expecStringL3 = "---Join[R.H <> B.D]\n[[S.B, R.G], equals null, min null, max null]\n[[S.A, B.D], equals null, min null, max null]\n----LogicalOperator\n----LogicalOperator\n";
    assertEquals(expecStringL3, logicalJoinOperator.toString(3));
  }
}
