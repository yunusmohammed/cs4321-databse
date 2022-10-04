package com.cs4321.app;

import com.cs4321.physicaloperators.DuplicateEliminationOperator;
import com.cs4321.physicaloperators.Operator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateEliminationOperatorTest {

    Operator child;
    DuplicateEliminationOperator dupElimOperator;
    Tuple firstTuple = new Tuple("1,2,3,4");
    Tuple secondTuple = new Tuple("1,2,3,4");
    Tuple thirdTuple = new Tuple("8");
    Tuple fourthTuple = new Tuple("5,6,7");
    Tuple fifthTuple = new Tuple("5,6,7");

    @BeforeEach
    void setUp() {
        child = Mockito.mock(Operator.class);
        dupElimOperator = new DuplicateEliminationOperator(child);
    }

    @Test
    void testGetNextTuple() {
        // secondTuple and fifth Tuple should get skipped
        Mockito.when(child.getNextTuple()).thenReturn(firstTuple, secondTuple, thirdTuple, fourthTuple, fifthTuple, null);
        assertEquals(firstTuple, dupElimOperator.getNextTuple());
        assertEquals(thirdTuple, dupElimOperator.getNextTuple());
        assertEquals(fourthTuple, dupElimOperator.getNextTuple());
        assertNull(dupElimOperator.getNextTuple());
    }

    @Test
    void testReset() {
        Mockito.when(child.getNextTuple()).thenReturn(firstTuple, secondTuple, thirdTuple, fourthTuple, fifthTuple, null);
        assertEquals(firstTuple, dupElimOperator.getNextTuple());
        // when dupElimOperator is reset, the prevTuple gets set to null so secondTuple, which is equivalent to firstTuple,
        // is returned by getNextTuple
        dupElimOperator.reset();
        assertEquals(firstTuple, dupElimOperator.getNextTuple());
        assertEquals(thirdTuple, dupElimOperator.getNextTuple());
    }
}