package com.cs4321.indexes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RidTest {
    private Rid rid = new Rid(1, 2);

    @Test
    void getPageId() {
        assertEquals(1, rid.getPageId());
    }

    @Test
    void getTupleId() {
        assertEquals(2, rid.getTupleId());
    }
}