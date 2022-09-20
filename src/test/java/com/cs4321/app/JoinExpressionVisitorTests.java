package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ParseException;
import utils.Utils;

class JoinExpressionVisitorTests {
	static Tuple exampleLeftRowA;
	static Tuple exampleLeftRowAB;
	static Tuple exampleRightRowC;

	static HashMap<String, Integer> tableAColumnMap;
	static HashMap<String, Integer> tableBColumnMap;
	static HashMap<String, Integer> tableCColumnMap;

	static Map<String, Integer> exampleTableOffset;
	static String rightTable = "C";
	static JoinExpressionVisitor visitor;
	static DatabaseCatalog dbCatalog;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		exampleLeftRowA = new Tuple("1,2,7");
		exampleLeftRowAB = new Tuple("1,2,7,4,5");
		exampleRightRowC = new Tuple("1,5,6");

		tableAColumnMap = Mockito.mock(HashMap.class);
		Mockito.when(tableAColumnMap.get("X")).thenReturn(0);
		Mockito.when(tableAColumnMap.get("Y")).thenReturn(1);
		Mockito.when(tableAColumnMap.get("Z")).thenReturn(2);

		tableBColumnMap = Mockito.mock(HashMap.class);
		Mockito.when(tableBColumnMap.get("X")).thenReturn(0);
		Mockito.when(tableBColumnMap.get("Y")).thenReturn(1);

		tableCColumnMap = Mockito.mock(HashMap.class);
		Mockito.when(tableCColumnMap.get("X")).thenReturn(0);
		Mockito.when(tableCColumnMap.get("Y")).thenReturn(1);
		Mockito.when(tableCColumnMap.get("Z")).thenReturn(2);

		exampleTableOffset = new HashMap<>();
		exampleTableOffset.put("A", 0);
		exampleTableOffset.put("B", 3);
		exampleTableOffset.put("C", 5);

		visitor = new JoinExpressionVisitor(exampleTableOffset, rightTable);

		dbCatalog = Mockito.mock(DatabaseCatalog.class);
		Mockito.when(dbCatalog.columnMap("A")).thenReturn(tableAColumnMap);
		Mockito.when(dbCatalog.columnMap("B")).thenReturn(tableBColumnMap);
		Mockito.when(dbCatalog.columnMap("C")).thenReturn(tableCColumnMap);
		visitor.setDbCatalog(dbCatalog);
	}

	@Test
	void testSimpleEquals() throws ParseException {
		// True Case
		Expression exp = Utils.getExpression("A, B, C", "1 = 1");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// False Case
		exp = Utils.getExpression("A, B, C", "1 = 2");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));
	}

	@Test
	void testColumnsEquals() throws ParseException {

		// Same column True Case
		Expression exp = Utils.getExpression("A, B, C", "A.X = A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables True Case
		exp = Utils.getExpression("A, B, C", "A.X = C.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "B.Y = C.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table False Case
		exp = Utils.getExpression("A, B, C", "A.X = A.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Different tables both left False Case
		exp = Utils.getExpression("A, B, C", "A.X = B.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables False Case
		exp = Utils.getExpression("A, B, C", "A.X = C.Z");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "B.X = C.Z");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testSimpleNotEquals() throws ParseException {
		// True Case
		Expression exp = Utils.getExpression("A, B, C", "1 != 2");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// False Case
		exp = Utils.getExpression("A, B, C", "1 != 1");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));
	}

	@Test
	void testColumnsNotEquals() throws ParseException {

		// Same Table True Case
		Expression exp = Utils.getExpression("A, B, C", "A.X != A.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables True Case
		exp = Utils.getExpression("A, B, C", "A.X != C.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "B.Y != C.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table False Case
		exp = Utils.getExpression("A, B, C", "A.X != A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables False Case
		exp = Utils.getExpression("A, B, C", "A.X != C.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "B.Y != C.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testSimpleGreaterThan() throws ParseException {
		// Strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "1 > 0");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Equals
		exp = Utils.getExpression("A, B, C", "1 > 1");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Strictly less than
		exp = Utils.getExpression("A, B, C", "1 > 2");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));
	}

	@Test
	void testColumnsGreaterThan() throws ParseException {

		// Same table strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "A.Y > A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table equals
		exp = Utils.getExpression("A, B, C", "A.X > A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table strictly less than
		exp = Utils.getExpression("A, B, C", "A.X > A.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly greater than
		exp = Utils.getExpression("A, B, C", "C.Z > A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Z > B.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables equals
		exp = Utils.getExpression("A, B, C", "C.X > A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Y > B.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly less than
		exp = Utils.getExpression("A, B, C", "C.Z > A.Z");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.X > B.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testSimpleGreaterThanEquals() throws ParseException {
		// Strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "1 >= 0");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Equals
		exp = Utils.getExpression("A, B, C", "1 >= 1");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Strictly less than
		exp = Utils.getExpression("A, B, C", "1 >= 2");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));
	}

	@Test
	void testColumnsGreaterThanEquals() throws ParseException {

		// Same table strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "A.Y >= A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table equals
		exp = Utils.getExpression("A, B, C", "A.X >= A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table strictly less than
		exp = Utils.getExpression("A, B, C", "A.X >= A.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly greater than
		exp = Utils.getExpression("A, B, C", "C.Z >= A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Z >= B.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables equals
		exp = Utils.getExpression("A, B, C", "C.X >= A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Y >= B.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly less than
		exp = Utils.getExpression("A, B, C", "C.Z >= A.Z");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.X >= B.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testSimpleMinorThan() throws ParseException {
		// Strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "1 < 0");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Equals
		exp = Utils.getExpression("A, B, C", "1 < 1");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Strictly less than
		exp = Utils.getExpression("A, B, C", "1 < 2");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));
	}

	@Test
	void testColumnsMinorThan() throws ParseException {

		// Same table strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "A.Y < A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table equals
		exp = Utils.getExpression("A, B, C", "A.X < A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table strictly less than
		exp = Utils.getExpression("A, B, C", "A.X < A.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly greater than
		exp = Utils.getExpression("A, B, C", "C.Z < A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Z < B.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables equals
		exp = Utils.getExpression("A, B, C", "C.X < A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Y < B.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly less than
		exp = Utils.getExpression("A, B, C", "C.Z < A.Z");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.X < B.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testSimpleMinorThanEquals() throws ParseException {
		// Strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "1 <= 0");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Equals
		exp = Utils.getExpression("A, B, C", "1 <= 1");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));

		// Strictly less than
		exp = Utils.getExpression("A, B, C", "1 <= 2");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowA, exampleRightRowC));
	}

	@Test
	void testColumnsMinorThanEquals() throws ParseException {

		// Same table strictly greater than
		Expression exp = Utils.getExpression("A, B, C", "A.Y <= A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table equals
		exp = Utils.getExpression("A, B, C", "A.X <= A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Same table strictly less than
		exp = Utils.getExpression("A, B, C", "A.X <= A.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly greater than
		exp = Utils.getExpression("A, B, C", "C.Z <= A.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Z <= B.X");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables equals
		exp = Utils.getExpression("A, B, C", "C.X <= A.X");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.Y <= B.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Left and right tables strictly less than
		exp = Utils.getExpression("A, B, C", "C.Z <= A.Z");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
		exp = Utils.getExpression("A, B, C", "C.X <= B.Y");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testSimpleAnd() throws ParseException {
		// Both True
		Expression exp = Utils.getExpression("A, B, C", "0 < 1 AND 1 < 2");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// only left True
		exp = Utils.getExpression("A, B, C", "1 < 2 AND 1 < 0");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Only right True
		exp = Utils.getExpression("A, B, C", "1 < 0 AND 1 < 2");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Neither True
		exp = Utils.getExpression("A, B, C", "3 < 2 AND 2 < 1");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}

	@Test
	void testColumnAnd() throws ParseException {
		// Both True, one table referenced
		Expression exp = Utils.getExpression("A, B, C", "A.X < A.Y AND A.Y < A.Z");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Both True, left and right tables referenced
		exp = Utils.getExpression("A, B, C", "A.X < C.Y AND B.Y < C.Z");
		assertTrue(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// only left True, one table referenced
		exp = Utils.getExpression("A, B, C", "A.X < A.Y AND A.Z < A.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// only left True, left and right tables referenced
		exp = Utils.getExpression("A, B, C", "A.X < C.Y AND C.Z < B.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// only right True, one table referenced
		exp = Utils.getExpression("A, B, C", "A.Y < A.X AND A.Y < A.Z");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// only right True, left and right tables referenced
		exp = Utils.getExpression("A, B, C", "C.Y < A.X AND B.Y < C.Z");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Neither True, one table referenced
		exp = Utils.getExpression("A, B, C", "A.Y < A.X AND A.Z < A.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));

		// Neither True, left and right tables referenced
		exp = Utils.getExpression("A, B, C", "C.Y < A.X AND C.Z < B.Y");
		assertFalse(visitor.evalExpression(exp, exampleLeftRowAB, exampleRightRowC));
	}
}
