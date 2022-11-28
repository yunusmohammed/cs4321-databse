package com.cs4321.app;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Column;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class DSUExpressionVisitorTest {

    Expression exp;
    static AliasMap aliasMap;
    static DSUExpressionVisitor visitor;

    @BeforeAll
    static void beforeAll() {
        visitor = new DSUExpressionVisitor();
        aliasMap = Mockito.mock(AliasMap.class);
        Mockito.when(aliasMap.columnWithBaseTable(any())).then(
                invocation -> invocation.getArgument(0, Column.class).getWholeColumnName());
    }

    @Test
    public void oneUsableExpression() throws ParseException {
        // Null lower bound
        exp = Utils.getExpression("Sailors", "R.A < 100");
        visitor.processExpression(exp, aliasMap);
        List<UnionFindElement> elements = visitor.getUnionFind().getCollections();

        assertNull(visitor.getUnusable());
        assertEquals(1, elements.size());

        UnionFindElement e1 = elements.get(0);
        assertNull(e1.getLowerBound());
        assertEquals(99, e1.getUpperBound());
        assertNull(e1.getEqualityConstraint());
        assertEquals("[R.A]", e1.getAttributes().toString());

        // Null upper bound
        exp = Utils.getExpression("Sailors", "R.A > 100");
        visitor.processExpression(exp, aliasMap);
        elements = visitor.getUnionFind().getCollections();

        assertNull(visitor.getUnusable());
        assertEquals(1, elements.size());

        e1 = elements.get(0);
        assertEquals(101, e1.getLowerBound());
        assertNull(e1.getUpperBound());
        assertNull(e1.getEqualityConstraint());
        assertEquals("[R.A]", e1.getAttributes().toString());
    }

    @Test
    public void oneUsableExpressionWithUpdate() throws ParseException {
        // Null lower bound
        exp = Utils.getExpression("Sailors", "R.A < 100 AND R.A < 50 AND R.A < 75");
        visitor.processExpression(exp, aliasMap);
        List<UnionFindElement> elements = visitor.getUnionFind().getCollections();

        assertNull(visitor.getUnusable());
        assertEquals(1, elements.size());

        UnionFindElement e1 = elements.get(0);
        assertNull(e1.getLowerBound());
        assertEquals(49, e1.getUpperBound());
        assertNull(e1.getEqualityConstraint());
        assertEquals("[R.A]", e1.getAttributes().toString());

        // Null upper bound
        exp = Utils.getExpression("Sailors", "R.A > 100 AND R.A > 200 AND R.A > 150");
        visitor.processExpression(exp, aliasMap);
        elements = visitor.getUnionFind().getCollections();

        assertNull(visitor.getUnusable());
        assertEquals(1, elements.size());

        e1 = elements.get(0);
        assertEquals(201, e1.getLowerBound());
        assertNull(e1.getUpperBound());
        assertNull(e1.getEqualityConstraint());
        assertEquals("[R.A]", e1.getAttributes().toString());
    }

    @Test
    public void multipleUsableExpressions() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A < 100 AND R.A = R.B AND R.B = S.C AND S.C > 50 AND S.D = 42 AND S.D = T.F");
        visitor.processExpression(exp, aliasMap);
        List<UnionFindElement> elements = visitor.getUnionFind().getCollections();

        assertNull(visitor.getUnusable());
        assertEquals(2, elements.size());

        UnionFindElement e1 = elements.get(0);
        UnionFindElement e2 = elements.get(1);

        assertEquals(51, e1.getLowerBound());
        assertEquals(99, e1.getUpperBound());
        assertNull(e1.getEqualityConstraint());
        assertEquals("[R.A, R.B, S.C]", e1.getAttributes().toString());

        assertEquals(42, e2.getLowerBound());
        assertEquals(42, e2.getUpperBound());
        assertEquals(42, e2.getEqualityConstraint());
        assertEquals("[S.D, T.F]", e2.getAttributes().toString());
    }

    @Test
    public void noUsableExpression() throws ParseException {
        // Null lower bound
        exp = Utils.getExpression("Sailors", "R.A <> R.B AND S.C <> T.D");
        visitor.processExpression(exp, aliasMap);
        List<UnionFindElement> elements = visitor.getUnionFind().getCollections();

        assertEquals(0, elements.size());
        assertEquals("R.A <> R.B AND S.C <> T.D", visitor.getUnusable().toString());
    }

    @Test
    public void oneUnusableExpression() throws ParseException{
        exp = Utils.getExpression("Sailors", "R.A <> U.B AND R.A = S.B AND S.C = T.D AND R.A = 2 AND T.D = T.X AND U.Y <> 42");
        visitor.processExpression(exp, aliasMap);
        List<UnionFindElement> elements = visitor.getUnionFind().getCollections();

        assertEquals("R.A <> U.B", visitor.getUnusable().toString());
        assertEquals(2, elements.size());

        UnionFindElement e1 = elements.get(0);
        UnionFindElement e2 = elements.get(1);

        assertEquals(2, e1.getLowerBound());
        assertEquals(2, e1.getUpperBound());
        assertEquals(2, e1.getEqualityConstraint());
        assertEquals("[R.A, S.B]", e1.getAttributes().toString());

        assertNull(e2.getLowerBound());
        assertNull(e2.getUpperBound());
        assertNull(e2.getEqualityConstraint());
        assertEquals("[S.C, T.D, T.X]", e2.getAttributes().toString());
    }
}