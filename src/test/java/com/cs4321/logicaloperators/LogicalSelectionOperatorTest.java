package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.physicaloperators.IndexSelectionVisitor;
import com.cs4321.physicaloperators.SelectExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

/**
 * Tests for LogicalSelectOperator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSelectionOperatorTest {

    LogicalOperator expectedChildOperator;
    Expression expectedExpression;
    SelectExpressionVisitor expectedSelectExpressionVisitor;
    IndexSelectionVisitor expectedIndexSelectionVisitor;
    AliasMap expectedAliasMap;
    LogicalSelectionOperator logicalSelectOperator;

    @BeforeEach
    public void setup() {
        expectedChildOperator = Mockito.mock(LogicalOperator.class);
        Mockito.when(expectedChildOperator.toString(Mockito.anyInt())).thenCallRealMethod();

        expectedExpression = Mockito.mock(Expression.class);
        Mockito.when(expectedExpression.toString()).thenReturn("R.H <= 99");

        expectedSelectExpressionVisitor = Mockito.mock(SelectExpressionVisitor.class);
        expectedIndexSelectionVisitor = Mockito.mock(IndexSelectionVisitor.class);

        expectedAliasMap = Mockito.mock(AliasMap.class);
        logicalSelectOperator = new LogicalSelectionOperator(expectedExpression,
                expectedChildOperator, expectedSelectExpressionVisitor, expectedIndexSelectionVisitor,
                expectedAliasMap);
    }

    @Test
    public void logicalSelectionOperatorCorrectlyInitializedTest() {
        assertEquals(expectedChildOperator, logicalSelectOperator.getChild());
        assertEquals(expectedExpression, logicalSelectOperator.getSelectCondition());
        assertEquals(expectedSelectExpressionVisitor, logicalSelectOperator.getSelectExpressionVisitor());
        assertEquals(expectedAliasMap, logicalSelectOperator.getAliasMap());
        assertEquals(expectedIndexSelectionVisitor, logicalSelectOperator.getIndexVisitor());
    }

    @Test
    public void testToString() {
        // Selection operator is at level 0
        String expecStringL0 = "Select[R.H <= 99]\n-LogicalOperator\n";
        assertEquals(expecStringL0, logicalSelectOperator.toString(0));

        // Selection operator is at level > 0, eg 3
        String expecStringL3 = "---Select[R.H <= 99]\n----LogicalOperator\n";
        assertEquals(expecStringL3, logicalSelectOperator.toString(3));
    }
}
