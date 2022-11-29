package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.argThat;

class ProjectionOperatorTest {
    Operator mockChild;
    AliasMap mockAliasMap;
    AliasMap aliasMap;

    @BeforeEach
    void setUp() {
        mockChild = Mockito.mock(Operator.class);
        mockAliasMap = Mockito.mock(AliasMap.class);
        aliasMap = Mockito.mock(AliasMap.class);
        Mockito.when(aliasMap.get(argThat(a -> a != null && a.getColumnName().equals("A")))).thenReturn(0);
        Mockito.when(aliasMap.get(argThat(b -> b != null && b.getColumnName().equals("B")))).thenReturn(1);
        Mockito.when(aliasMap.get(argThat(c -> c != null && c.getColumnName().equals("C")))).thenReturn(2);
        Mockito.when(aliasMap.get(argThat(d -> d != null && d.getColumnName().equals("D")))).thenReturn(3);
    }

    List<SelectItem> generateList(String[] characters) {
        Table fakeTable = new Table();
        List<SelectItem> selectItems = new ArrayList<>();
        for (String s : characters) {
            if (s.equals("*")) {
                selectItems.add(new AllColumns());
            } else {
                SelectExpressionItem expItem = new SelectExpressionItem();
                expItem.setExpression(new Column(fakeTable, s));
                selectItems.add(expItem);
            }

        }
        return selectItems;
    }

    @Test
    void getNextTuple() {
        Tuple expectedResult;
        ProjectionOperator projectOperator;
        List<SelectItem> selectItems;

        // Child returns null
        selectItems = generateList(new String[] { "A", "B", "C", "D" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        Mockito.when(mockChild.getNextTuple()).thenReturn(null);
        assertNull(projectOperator.getNextTuple());

        // Selection is one column
        selectItems = generateList(new String[] { "B" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        expectedResult = new Tuple("20");
        Mockito.when(mockChild.getNextTuple()).thenReturn(new Tuple("10,20,30,40"));
        assertEquals(expectedResult, projectOperator.getNextTuple());

        // Selection is multiple but not all columns & non-consecutive
        selectItems = generateList(new String[] { "A", "C" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        expectedResult = new Tuple("10,30");
        Mockito.when(mockChild.getNextTuple()).thenReturn(new Tuple("10,20,30,40"));
        assertEquals(expectedResult, projectOperator.getNextTuple());

        // Selection is multiple con but not all columns & consecutive
        selectItems = generateList(new String[] { "A", "B" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        expectedResult = new Tuple("10,20");
        Mockito.when(mockChild.getNextTuple()).thenReturn(new Tuple("10,20,30,40"));
        assertEquals(expectedResult, projectOperator.getNextTuple());

        // Selection is done out of order
        selectItems = generateList(new String[] { "B", "A" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        expectedResult = new Tuple("20,10");
        Mockito.when(mockChild.getNextTuple()).thenReturn(new Tuple("10,20,30,40"));
        assertEquals(expectedResult, projectOperator.getNextTuple());

        // Selection is every column without using AllColumns
        selectItems = generateList(new String[] { "A", "B", "C", "D" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        Mockito.when(mockChild.getNextTuple()).thenReturn(new Tuple("10,20,30,40"));
        expectedResult = new Tuple("10,20,30,40");
        assertEquals(expectedResult, projectOperator.getNextTuple());
    }

    @Test
    void testToString() throws ParseException {
        ProjectionOperator projectOperator;
        List<SelectItem> selectItems;

        // One column projection
        selectItems = generateList(new String[] { "A" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        Mockito.when(mockChild.toString()).thenReturn("ScanOperator{}");
        assertEquals("ProjectionOperator{ScanOperator{}, [A]}", projectOperator.toString());

        // Multiple columns projection
        selectItems = generateList(new String[] { "A", "D", "C", "B" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        Mockito.when(mockChild.toString()).thenReturn("ScanOperator{}");
        assertEquals("ProjectionOperator{ScanOperator{}, [A, D, C, B]}", projectOperator.toString());
    }

    @Test
    void testToStringForPrinting() {
        List<SelectItem> selectItems = generateList(new String[] { "A" });
        ProjectionOperator projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        Mockito.when(mockChild.toString(Mockito.anyInt())).thenCallRealMethod();

        // Projection at depth 0 of physical query plan tree
        assertEquals("Project[A]\n-PhysicalOperator\n", projectOperator.toString(0));

        // Projection at depth 3 of physical query plan tree
        assertEquals("---Project[A]\n----PhysicalOperator\n",
                projectOperator.toString(3));

        // Multiple columns projection
        selectItems = generateList(new String[] { "A", "D", "C", "B" });
        projectOperator = new ProjectionOperator(aliasMap, selectItems, mockChild);
        assertEquals("Project[A, D, C, B]\n-PhysicalOperator\n", projectOperator.toString(0));
    }
}