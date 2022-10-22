package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;

class SelectExpressionVisitorTest {

    Expression exp;
    SelectExpressionVisitor visitor;

    Tuple emptyRow;
    AliasMap emptyAliasMap;

    Tuple exampleRow;
    AliasMap exampleAliasMap;

    @BeforeEach
    void setUp() {
        emptyRow = new Tuple("");
        emptyAliasMap = Mockito.mock(AliasMap.class);
        exampleRow = new Tuple("1,2,3,4,5");
        exampleAliasMap = emptyAliasMap = Mockito.mock(AliasMap.class);
        Mockito.when(exampleAliasMap.get(argThat(a -> a != null && a.getColumnName().equals("A")))).thenReturn(1);
        Mockito.when(exampleAliasMap.get(argThat(b -> b != null && b.getColumnName().equals("B")))).thenReturn(2);
        Mockito.when(exampleAliasMap.get(argThat(c -> c != null && c.getColumnName().equals("C")))).thenReturn(3);

        visitor = new SelectExpressionVisitor();
    }

    @Test
    void testSimpleEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "1 = 1");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // False Case
        exp = Utils.getExpression("Sailors", "1 = 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "R.A = R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // False Case
        exp = Utils.getExpression("Sailors", "R.A = R.B");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

    @Test
    void testSimpleNotEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "1 != 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // False Case
        exp = Utils.getExpression("Sailors", "1 != 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsNotEquals() throws ParseException {

        // True Case
        exp = Utils.getExpression("Sailors", "R.A != R.B");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // False Case
        exp = Utils.getExpression("Sailors", "R.A != R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

    @Test
    void testSimpleGreaterThan() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 > 0");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 > 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 > 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsGreaterThan() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B > R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A > R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A > R.B");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

    @Test
    void testSimpleGreaterThanEquals() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 >= 0");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 >= 1");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 >= 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsGreaterThanEquals() throws ParseException {

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B >= R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A >= R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A >= R.B");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

    @Test
    void testSimpleMinorThan() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 < 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 < 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 < 0");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsMinorThan() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A < R.B");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

    @Test
    void testSimpleMinorThanEquals() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "1 <= 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "1 <= 1");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "1 <= 0");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsMinorThanEquals() throws ParseException {

        // Strictly less than
        exp = Utils.getExpression("Sailors", "R.A <= R.B");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Equals
        exp = Utils.getExpression("Sailors", "R.A <= R.A");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Strictly greater than
        exp = Utils.getExpression("Sailors", "R.B <= R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

    @Test
    void testSimpleAnd() throws ParseException {

        // Both True
        exp = Utils.getExpression("Sailors", "0 < 1 AND 1 < 2");
        assertTrue(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Left True
        exp = Utils.getExpression("Sailors", "1 < 2 AND 1 < 0");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Right True
        exp = Utils.getExpression("Sailors", "1 < 0 AND 1 < 2");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));

        // Neither True
        exp = Utils.getExpression("Sailors", "3 < 2 AND 2 < 1");
        assertFalse(visitor.evalExpression(exp, emptyRow, emptyAliasMap));
    }

    @Test
    void testColumnsAnd() throws ParseException {

        // Both True
        exp = Utils.getExpression("Sailors", "R.A < R.B AND R.B < R.C");
        assertTrue(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Left True
        exp = Utils.getExpression("Sailors", "R.B < R.C AND R.C < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Right True
        exp = Utils.getExpression("Sailors", "R.B < R.A AND R.B < R.C");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));

        // Neither True
        exp = Utils.getExpression("Sailors", "R.C < R.B AND R.B < R.A");
        assertFalse(visitor.evalExpression(exp, exampleRow, exampleAliasMap));
    }

}
