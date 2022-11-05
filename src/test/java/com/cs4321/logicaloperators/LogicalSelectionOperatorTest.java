package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.physicaloperators.IndexSelectionVisitor;
import com.cs4321.physicaloperators.SelectExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for LogicalSelectOperator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class LogicalSelectionOperatorTest {

    @Test
    public void logicalSelectionOperatorCorrectlyInitializedTest() {
        LogicalScanOperator expectedChildOperator = Mockito.mock(LogicalScanOperator.class);
        Expression expectedExpression = Mockito.mock(Expression.class);
        SelectExpressionVisitor expectedSelectExpressionVisitor = Mockito.mock(SelectExpressionVisitor.class);
        IndexSelectionVisitor expectedIndexSelectionVisitor = Mockito.mock(IndexSelectionVisitor.class);
        AliasMap expectedAliasMap = Mockito.mock(AliasMap.class);
        LogicalSelectionOperator logicalSelectOperator = new LogicalSelectionOperator(expectedExpression,
                expectedChildOperator, expectedSelectExpressionVisitor, expectedIndexSelectionVisitor, expectedAliasMap);
        assertEquals(expectedChildOperator, logicalSelectOperator.getChild());
        assertEquals(expectedExpression, logicalSelectOperator.getSelectCondition());
        assertEquals(expectedSelectExpressionVisitor, logicalSelectOperator.getSelectExpressionVisitor());
        assertEquals(expectedAliasMap, logicalSelectOperator.getAliasMap());
        assertEquals(expectedIndexSelectionVisitor, logicalSelectOperator.getIndexVisitor());
    }
}
