package com.cs4321.app;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SortOperatorTest {

    Operator mockChild;
    HashMap<String, Integer> columnMap;
    List<OrderByElement> orderByElementList;
    SortOperator sortOperator;
    Tuple firstTuple = new Tuple("90,0,100,1");
    Tuple secondTuple = new Tuple("89,0,99,2");
    Tuple thirdTuple = new Tuple("90,0,99,2");
    Tuple fourthTuple = new Tuple("90,0,100,2");

    private void addOrderByElement(String s) {
        // adds a column name at the end of the order by clause
        OrderByElement mockOrderByElement = Mockito.mock(OrderByElement.class);
        Mockito.when(mockOrderByElement.toString()).thenReturn(s);
        orderByElementList.add(mockOrderByElement);
    }

    @BeforeEach
    void setUp() {
        // initializes objects for sort operator
        mockChild = Mockito.mock(Operator.class);
        columnMap = new HashMap<>();
        columnMap.put("A", 0);
        columnMap.put("B", 1);
        columnMap.put("C", 2);
        columnMap.put("D", 3);
        orderByElementList = new ArrayList<>();
        sortOperator = new SortOperator(mockChild, columnMap, orderByElementList);
    }

    @Test
    void testGetNextTuple() {
        // order by clause sorts by columns "B", "D", "A", and "C"
        addOrderByElement("B");
        addOrderByElement("D");
        // sets the value returned by getNextTuple for the child. null will be returned after four calls to getNextTuple
        Mockito.when(mockChild.getNextTuple()).thenReturn(thirdTuple, secondTuple, fourthTuple, firstTuple, null);
        assertEquals(firstTuple, sortOperator.getNextTuple());
        assertEquals(secondTuple, sortOperator.getNextTuple());
        assertEquals(thirdTuple, sortOperator.getNextTuple());
        assertEquals(fourthTuple, sortOperator.getNextTuple());
        assertNull(sortOperator.getNextTuple());
        assertNull(sortOperator.getNextTuple());

        // order by clause sorts by columns "A", "B", "C", and "D"
        for(int i=0; i<orderByElementList.size(); i++) {
            orderByElementList.remove(orderByElementList.size()-1);
        }
        sortOperator = new SortOperator(mockChild, columnMap, orderByElementList);
        Mockito.when(mockChild.getNextTuple()).thenReturn(firstTuple, secondTuple, thirdTuple, fourthTuple, null);
        assertEquals(secondTuple, sortOperator.getNextTuple());
        assertEquals(thirdTuple, sortOperator.getNextTuple());
        assertEquals(firstTuple, sortOperator.getNextTuple());
        assertEquals(fourthTuple, sortOperator.getNextTuple());
        assertNull(sortOperator.getNextTuple());
        assertNull(sortOperator.getNextTuple());
    }

    @Test
    void testReset() {
        Mockito.when(mockChild.getNextTuple()).thenReturn(thirdTuple, secondTuple, fourthTuple, firstTuple, null);
        assertEquals(secondTuple, sortOperator.getNextTuple());
        // when reset is called, sortOperator will try to read in the results of its child again, and since mockChild
        // hasn't implemented reset(), mockChild.getNextTuple() will return null
        sortOperator.reset();
        assertNull(sortOperator.getNextTuple());
        Mockito.when(mockChild.getNextTuple()).thenReturn(thirdTuple, secondTuple, fourthTuple, firstTuple, null);
        // after resetting, sortOperator will again read the tuples from mockChild
        sortOperator.reset();
        assertEquals(secondTuple, sortOperator.getNextTuple());
        assertEquals(thirdTuple, sortOperator.getNextTuple());

    }
}