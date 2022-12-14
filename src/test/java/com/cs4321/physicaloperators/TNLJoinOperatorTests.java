package com.cs4321.physicaloperators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;

import com.cs4321.app.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;

import net.sf.jsqlparser.expression.Expression;

class TNLJoinOperatorTests {

	Operator leftChild;
	Operator rightChild;
	Expression joinCondition;
	JoinExpressionVisitor visitor;
	TNLJoinOperator joinOperator;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		leftChild = Mockito.mock(Operator.class);
		Mockito.when(leftChild.toString()).thenReturn("Operator{}");

		rightChild = Mockito.mock(Operator.class);
		Mockito.when(rightChild.toString()).thenReturn("Operator{}");

		joinCondition = Mockito.mock(Expression.class);
		Mockito.when(joinCondition.toString()).thenReturn("S.A < T.B");

		visitor = Mockito.mock(JoinExpressionVisitor.class);
		joinOperator = new TNLJoinOperator(leftChild, rightChild, joinCondition, visitor, null, null);
	}

	@Test
	void testGetNextTuple() {
		Tuple expectedResult;

		// Left child returns null
		Mockito.when(leftChild.getNextTuple()).thenReturn(null);
		assertNull(joinOperator.getNextTuple());

		// Current row fails expression and is last row in both left and right children
		Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), null);
		Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), null);
		Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
		assertNull(joinOperator.getNextTuple());
		joinOperator.reset();

		// Current row fails expression but next row from right child passes
		expectedResult = new Tuple("1,2,3,6,7");
		Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), null);
		Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), new Tuple("6,7"), null);
		Mockito
				.when(
						visitor.evalExpression(Mockito.any(), Mockito.any(), AdditionalMatchers.not(Mockito.eq(new Tuple("6,7")))))
				.thenReturn(false);
		Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.eq(new Tuple("6,7")))).thenReturn(true);
		assertEquals(expectedResult, joinOperator.getNextTuple());
		joinOperator.reset();

		// Current row fails expression but next row from left child passes
		expectedResult = new Tuple("6,7,8,4,5");
		Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), new Tuple("6,7,8"), null);
		Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), null, new Tuple("4,5"), null);
		Mockito
				.when(
						visitor.evalExpression(Mockito.any(), AdditionalMatchers.not(Mockito.eq(new Tuple("6,7,8"))),
								Mockito.any()))
				.thenReturn(false);
		Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.eq(new Tuple("6,7,8")), Mockito.any())).thenReturn(true);
		assertEquals(expectedResult, joinOperator.getNextTuple());
		joinOperator.reset();

		// Current row passes expression
		expectedResult = new Tuple("1,2,3,4,5");
		Mockito.when(leftChild.getNextTuple()).thenReturn(new Tuple("1,2,3"), null);
		Mockito.when(rightChild.getNextTuple()).thenReturn(new Tuple("4,5"), null);
		Mockito.when(visitor.evalExpression(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		assertEquals(expectedResult, joinOperator.getNextTuple());
		joinOperator.reset();

	}

	@Test
	void testReset() {
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

		while (joinOperator.getNextTuple() != null) {
			;
		}
		assertNull(joinOperator.getNextTuple());
		joinOperator.reset();

		Tuple firstExpectedResult = new Tuple("1,2,3,4,5");
		assertEquals(firstExpectedResult, joinOperator.getNextTuple());

		Tuple secondExpectedResult = new Tuple("1,2,3,9,10");
		assertEquals(secondExpectedResult, joinOperator.getNextTuple());

		Tuple thirdExpectedResult = new Tuple("6,7,8,4,5");
		assertEquals(thirdExpectedResult, joinOperator.getNextTuple());

		Tuple fourthExpectedResult = new Tuple("6,7,8,9,10");
		assertEquals(fourthExpectedResult, joinOperator.getNextTuple());

	}

	@Test
	void testToString() {
		// TNLJoinOperator with Non-joinOperator children
		assertEquals("TNLJoinOperator{Operator{}, Operator{}, S.A < T.B}", joinOperator.toString());

		// joinOperator with TNLJoinOperator child
		Operator newOperator = Mockito.mock(Operator.class);
		Mockito.when(newOperator.toString()).thenReturn("Operator{}");
		Expression newJoinCondition = Mockito.mock(Expression.class);
		Mockito.when(newJoinCondition.toString()).thenReturn("R.C < S.B");
		TNLJoinOperator joinOperatorWithJoinChild = new TNLJoinOperator(joinOperator, newOperator, newJoinCondition,
				visitor, null, null);
		assertEquals("TNLJoinOperator{TNLJoinOperator{Operator{}, Operator{}, S.A < T.B}, Operator{}, R.C < S.B}",
				joinOperatorWithJoinChild.toString());

		// joinOperator with no join Condition
		joinOperator.setJoinCondition(null);
		assertEquals("TNLJoinOperator{Operator{}, Operator{}, null}", joinOperator.toString());
	}

}
