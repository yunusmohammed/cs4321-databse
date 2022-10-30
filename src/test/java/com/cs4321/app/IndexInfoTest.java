package com.cs4321.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndexInfoTest {
    private static final IndexInfo indexInfo = new IndexInfo("Sailors", "A",true, 15);

    @Test
    void getRelationName() {
        assertEquals("Sailors", indexInfo.getRelationName());
    }

    @Test
    void getAttributeName() {
        assertEquals("A", indexInfo.getAttributeName());
    }

    @Test
    void isClustered() {
        assertTrue(indexInfo.isClustered());
    }

    @Test
    void getOrder() {
        assertEquals(15, indexInfo.getOrder());
    }
}