package com.cs4321.physicaloperators;

import com.cs4321.app.Logger;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExternalSortOperatorTest {

    Operator mockChild;
    HashMap<String, Integer> columnMap;
    List<OrderByElement> orderByElementList;
    ExternalSortOperator externalSortOperator;
    String tempFileDir;
    Logger logger = Logger.getInstance();
    Tuple firstTuple = new Tuple("90,0,100,1");
    Tuple secondTuple = new Tuple("89,0,99,2");
    Tuple thirdTuple = new Tuple("90,0,99,2");
    Tuple fourthTuple = new Tuple("90,0,100,2");
    int numFolders = 1;

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
        columnMap.put("Table.A", 0);
        columnMap.put("Table.B", 1);
        columnMap.put("Table.C", 2);
        columnMap.put("Table.D", 3);
        orderByElementList = new ArrayList<>();
        try {
            tempFileDir = Files.createTempDirectory("externalSortTest").toString();
        } catch (IOException e) {
            logger.log("Error creating temp directory for external sort test");
            throw new Error();
        }
        addOrderByElement("Table.B");
        addOrderByElement("Table.D");
        Mockito.when(mockChild.getNextTuple()).thenReturn(thirdTuple, secondTuple, fourthTuple, firstTuple, null);
        externalSortOperator = new ExternalSortOperator(mockChild, columnMap, orderByElementList, tempFileDir, "" + numFolders++, 3);
    }

    @Test
    void testGetNextTuple() {
        // order by clause sorts by columns "B", "D", "A", and "C"
        assertEquals(firstTuple, externalSortOperator.getNextTuple());
        assertEquals(secondTuple, externalSortOperator.getNextTuple());
        assertEquals(thirdTuple, externalSortOperator.getNextTuple());
        assertEquals(fourthTuple, externalSortOperator.getNextTuple());
        assertNull(externalSortOperator.getNextTuple());
        assertNull(externalSortOperator.getNextTuple());

        // order by clause sorts by columns "A", "B", "C", and "D"
        for(int i=0; i<orderByElementList.size(); i++) {
            orderByElementList.remove(orderByElementList.size()-1);
        }
        Mockito.when(mockChild.getNextTuple()).thenReturn(firstTuple, secondTuple, thirdTuple, fourthTuple, null);
        externalSortOperator = new ExternalSortOperator(mockChild, columnMap, orderByElementList, tempFileDir, "" + numFolders++, 3);
        assertEquals(secondTuple, externalSortOperator.getNextTuple());
        assertEquals(thirdTuple, externalSortOperator.getNextTuple());
        assertEquals(firstTuple, externalSortOperator.getNextTuple());
        assertEquals(fourthTuple, externalSortOperator.getNextTuple());
        assertNull(externalSortOperator.getNextTuple());
        assertNull(externalSortOperator.getNextTuple());
    }

    @Test
    void testToString() {
        Mockito.when(mockChild.toString()).thenReturn("Operator{}");
        assertEquals("ExternalSortOperator{Operator{}, Order By : Table.B, Table.D}", externalSortOperator.toString());
    }

}