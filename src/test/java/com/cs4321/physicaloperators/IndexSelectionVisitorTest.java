package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;

class IndexSelectionVisitorTest {

    Expression exp;
    IndexSelectionVisitor visitor;
    AliasMap aliasMap;
    String indexColumnName;

    @BeforeEach
    void setUp() {
        visitor = new IndexSelectionVisitor();
        aliasMap = Mockito.mock(AliasMap.class);
        Mockito.when(aliasMap.getBaseTable(anyString())).then(AdditionalAnswers.returnsFirstArg());
        indexColumnName = "A";
    }

    @Test
    void testEqualsLeftColumnRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A = 1");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(1, lst.get(0));
        assertEquals(1, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testEqualsLeftValueRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 = R.A");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(1, lst.get(0));
        assertEquals(1, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testEqualsLeftColumnRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A = R.B");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A = R.B", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testEqualsLeftValueRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 = 2");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("1 = 2", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testMinorThanLeftColumnRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A < 1");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(Integer.MIN_VALUE, lst.get(0));
        assertEquals(0, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testMinorThanLeftValueRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 < R.A");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(Integer.MIN_VALUE, lst.get(0));
        assertEquals(0, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testMinorThanLeftColumnRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A < R.B");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A < R.B", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testMinorThanLeftValueRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 < 2");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("1 < 2", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testMinorThanEqualsLeftColumnRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A <= 1");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(Integer.MIN_VALUE, lst.get(0));
        assertEquals(1, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testMinorThanEqualsLeftValueRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 <= R.A");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(Integer.MIN_VALUE, lst.get(0));
        assertEquals(1, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testMinorThanEqualsLeftColumnRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A <= R.B");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A <= R.B", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testMinorThanEqualsLeftValueRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 <= 2");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("1 <= 2", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testGreaterThanLeftColumnRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A > 1");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(2, lst.get(0));
        assertEquals(Integer.MAX_VALUE, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testGreaterThanLeftValueRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 > R.A");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(2, lst.get(0));
        assertEquals(Integer.MAX_VALUE, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testGreaterThanLeftColumnRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A > R.B");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A > R.B", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testGreaterThanLeftValueRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 > 2");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("1 > 2", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testGreaterThanEqualsLeftColumnRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A >= 1");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(1, lst.get(0));
        assertEquals(Integer.MAX_VALUE, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testGreaterThanEqualsLeftValueRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 >= R.A");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(1, lst.get(0));
        assertEquals(Integer.MAX_VALUE, lst.get(1));

        // Ensure that non-index expressions is null
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testGreaterThanEqualsLeftColumnRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A >= R.B");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A >= R.B", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testGreaterThanEqualsLeftValueRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 >= 2");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("1 >= 2", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testNotEqualsLeftColumnRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A != 1");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A <> 1", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testNotEqualsLeftValueRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 != R.A");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A <> 1", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testNotEqualsLeftColumnRightColumn() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A != R.B");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("R.A <> R.B", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testNotEqualsLeftValueRightValue() throws ParseException {
        exp = Utils.getExpression("Sailors", "1 != 2");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is null
        assertNull(visitor.getIndexColumn());

        // Ensure that the lowHigh is empty
        assertEquals(0, visitor.getLowHigh().size());

        // Ensure that non-index expression is correct
        assertEquals("1 <> 2", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testAndWithoutUpdates() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A > 10 AND R.B < 5 AND R.C < 76");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(11, lst.get(0));
        assertEquals(Integer.MAX_VALUE, lst.get(1));

        // Ensure that non-index expression is correct
        assertEquals("R.B < 5 AND R.C < 76", visitor.getNoIndexExpression().toString());
    }

    @Test
    void testAndWithOneUpdates() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A > 10 AND R.A <= 20");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(11, lst.get(0));
        assertEquals(20, lst.get(1));

        // Ensure that non-index expression is empty
        assertNull(visitor.getNoIndexExpression());
    }

    @Test
    void testAndWithMultipleUpdates() throws ParseException {
        exp = Utils.getExpression("Sailors", "R.A < 500 AND R.A < 700 AND R.A >= 30 AND R.A <= 50 AND R.A > 34 AND R.A >= 0 AND R.A < 36");
        visitor.splitExpression(exp, indexColumnName);

        // Ensure that the column is correct
        assertEquals("R.A", visitor.getIndexColumn().getWholeColumnName());

        // Check values of list
        List<Integer> lst = visitor.getLowHigh();
        assertEquals(35, lst.get(0));
        assertEquals(35, lst.get(1));


        // Ensure that non-index expression is empty
        assertNull(visitor.getNoIndexExpression());
    }
}