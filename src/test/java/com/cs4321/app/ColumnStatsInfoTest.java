package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColumnStatsInfoTest {
    private static ColumnStatsInfo columnStatsInfo;

    @BeforeAll
    static void beforeAll() {
        columnStatsInfo = new ColumnStatsInfo("V");
        columnStatsInfo.setMaxValue(1000);
        columnStatsInfo.setMinValue(-1);
    }

    @Test
    void getMinValue() {
        assertEquals(-1, columnStatsInfo.getMinValue());
    }

    @Test
    void getMaxValue() {
        assertEquals(1000, columnStatsInfo.getMaxValue());
    }

    @Test
    void testToString() {
        assertEquals(columnStatsInfo.toString(), "V,-1,1000");
    }
}