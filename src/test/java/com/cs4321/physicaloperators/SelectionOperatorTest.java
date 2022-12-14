package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;

class SelectionOperatorTest {

    Operator mockScan;
    SelectExpressionVisitor mockVisitor;
    AliasMap mockAliasMap;
    SelectionOperator selectOperator;

    @BeforeEach
    void setUp() throws Exception {
        Expression mockExpression = Mockito.mock(Expression.class);
        mockScan = Mockito.mock(Operator.class);
        mockVisitor = Mockito.mock(SelectExpressionVisitor.class);
        mockAliasMap = Mockito.mock(AliasMap.class);
        selectOperator = new SelectionOperator(mockVisitor, mockAliasMap, mockExpression, mockScan);
    }

    @Test
    void testGetNextTuple() {
        Tuple expectedResult;
        // Scan returns null
        Mockito.when(mockScan.getNextTuple()).thenReturn(null);
        assertNull(selectOperator.getNextTuple());

        // Current row fails expression and is last row in column
        Mockito.when(mockScan.getNextTuple()).thenReturn(new Tuple("1,1,1"));
        Mockito.when(mockVisitor.evalExpression(any(), any(), eq(mockAliasMap))).thenReturn(false);
        Mockito.when(mockScan.getNextTuple()).thenReturn(null);
        assertNull(selectOperator.getNextTuple());

        // Current row fails expression but next row passes
        expectedResult = new Tuple("2,2,2");
        Mockito.when(mockScan.getNextTuple()).thenReturn(new Tuple("1,1,1"));
        Mockito.when(mockVisitor.evalExpression(any(), any(), eq(mockAliasMap))).thenReturn(false);
        Mockito.when(mockScan.getNextTuple()).thenReturn(expectedResult);
        Mockito.when(mockVisitor.evalExpression(any(), any(), eq(mockAliasMap))).thenReturn(true);
        assertEquals(expectedResult, selectOperator.getNextTuple());

        // Current row passes expression
        expectedResult = new Tuple("1,2,3");
        Mockito.when(mockScan.getNextTuple()).thenReturn(expectedResult);
        Mockito.when(mockVisitor.evalExpression(any(), any(), eq(mockAliasMap))).thenReturn(true);
        assertEquals(expectedResult, selectOperator.getNextTuple());
    }

    @Test
    void testToString() throws ParseException {
        Expression exp;
        SelectionOperator selectionOperator;

        // Simple expression
        exp = Utils.getExpression("Sailors", "1 < 2");
        Mockito.when(mockScan.toString()).thenReturn("ScanOperator{}");
        selectionOperator = new SelectionOperator(mockVisitor, mockAliasMap, exp, mockScan);
        assertEquals("SelectionOperator{ScanOperator{}, 1 < 2}", selectionOperator.toString());

        // Complex expression
        exp = Utils.getExpression("Sailors", "1 < 2 AND R.A = R.B AND R.C > R.A");
        Mockito.when(mockScan.toString()).thenReturn("ScanOperator{}");
        selectionOperator = new SelectionOperator(mockVisitor, mockAliasMap, exp, mockScan);
        assertEquals("SelectionOperator{ScanOperator{}, 1 < 2 AND R.A = R.B AND R.C > R.A}",
                selectionOperator.toString());
    }

    @Test
    void testReset() {
        Tuple firstExpectedResult = new Tuple("1,2,3");
        Tuple secondExpectedResult = new Tuple("1,2,3");
        Mockito.when(mockScan.getNextTuple()).thenReturn(firstExpectedResult);
        Mockito.when(mockScan.getNextTuple()).thenReturn(secondExpectedResult);
        doNothing().when(mockScan).reset();
        assertEquals(firstExpectedResult, mockScan.getNextTuple());
        assertEquals(secondExpectedResult, mockScan.getNextTuple());
    }

    @Test
    void testToStringForPrinting() throws ParseException {
        Expression exp = Utils.getExpression("Sailors", "1 < 2 AND R.A = R.B AND R.C > R.A");
        Mockito.when(mockScan.toString()).thenCallRealMethod();
        Mockito.when(mockScan.toString(Mockito.anyInt())).thenCallRealMethod();
        SelectionOperator selectionOperator = new SelectionOperator(mockVisitor, mockAliasMap, exp, mockScan);

        // Selection at depth 0 of physical query plan tree
        assertEquals("Select[1 < 2 AND R.A = R.B AND R.C > R.A]\n-PhysicalOperator\n", selectionOperator.toString(0));

        // Projection at depth 3 of physical query plan tree
        assertEquals("---Select[1 < 2 AND R.A = R.B AND R.C > R.A]\n----PhysicalOperator\n",
                selectionOperator.toString(3));
    }

}
