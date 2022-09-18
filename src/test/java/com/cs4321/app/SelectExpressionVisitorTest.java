package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ParseException;
import utils.Utils;

class SelectExpressionVisitorTest {

    Expression exp;
    SelectExpressionVisitor visitor;

    Tuple emptyRow;
    Map<String, Integer> emptyColumnMap;

    Tuple exampleRow;
    Map<String, Integer> exampleColumnMap;

    @BeforeEach
    void setUp() {
        emptyRow = new Tuple("");
        emptyColumnMap = new HashMap<>();
        exampleRow = new Tuple("1,2,3,4,5");
        exampleColumnMap = new HashMap<>();
        exampleColumnMap.put("A", 1);
        exampleColumnMap.put("B", 2);
        exampleColumnMap.put("C", 3);

        visitor = new SelectExpressionVisitor();
    }

    @Test
    void testSimpleEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "1 = 1");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // False Case
        exp = Utils.getExpression("Sailors", "1 = 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "R.A = R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // False Case
        exp = Utils.getExpression("Sailors", "R.A = R.B");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

    @Test
    void testSimpleNotEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "1 != 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // False Case
        exp = Utils.getExpression("Sailors", "1 != 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsNotEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "R.A != R.B");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // False Case
        exp = Utils.getExpression("Sailors", "R.A != R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

    @Test
    void testSimpleGreaterThan() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 > 0");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 > 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 > 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsGreaterThan() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B > R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A > R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A > R.B");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

    @Test
    void testSimpleGreaterThanEquals() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 >= 0");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 >= 1");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 >= 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsGreaterThanEquals() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B >= R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A >= R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A >= R.B");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

    @Test
    void testSimpleMinorThan() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 < 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 < 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 < 0");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsMinorThan() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A < R.B");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

    @Test
    void testSimpleMinorThanEquals() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 <= 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 <= 1");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 <= 0");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsMinorThanEquals() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A <= R.B");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A <= R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B <= R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

    @Test
    void testSimpleAnd() throws ParseException {

        // Both True
        exp = Utils.getExpression("Sailors", "0 < 1 AND 1 < 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Left True
        exp = Utils.getExpression("Sailors", "1 < 2 AND 1 < 0");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Right True
        exp = Utils.getExpression("Sailors", "1 < 0 AND 1 < 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));

        // Neither True
        exp = Utils.getExpression("Sailors", "3 < 2 AND 2 < 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyColumnMap));
    }

    @Test
    void testColumnsAnd() throws ParseException {

        // Both True
        exp = Utils.getExpression("Sailors", "R.A < R.B AND R.B < R.C");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Left True
        exp = Utils.getExpression("Sailors", "R.B < R.C AND R.C < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Right True
        exp = Utils.getExpression("Sailors", "R.B < R.A AND R.B < R.C");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));

        // Neither True
        exp = Utils.getExpression("Sailors", "R.C < R.B AND R.B < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleColumnMap));
    }

}
