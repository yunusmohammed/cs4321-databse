package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
		exampleLeftRowA = new Tuple("1,2,3");
		exampleLeftRowAB = new Tuple("1,2,3,4,5");
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
	}

	@BeforeEach
	void setUp() throws Exception {

	}

	// @Test
	// void testSimpleEquals() {
	// // True Case
	// Expression exp = getExpression("1 = 1");
	// assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

	// // False Case
	// exp = getExpression("1 = 2");
	// assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
	// }

}
