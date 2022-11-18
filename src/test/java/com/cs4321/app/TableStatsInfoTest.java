package com.cs4321.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableStatsInfoTest {
    private static TableStatsInfo tableStatsInfo;
    private static ColumnStatsInfo columnStatsInfo1;
    private static ColumnStatsInfo columnStatsInfo2;

    @BeforeAll
    static void beforeAll() {
        columnStatsInfo1 = new ColumnStatsInfo("V");
        columnStatsInfo1.setMaxValue(1000);
        columnStatsInfo1.setMinValue(-1);

        columnStatsInfo2 = new ColumnStatsInfo("B");
        columnStatsInfo2.setMaxValue(2000);
        columnStatsInfo2.setMinValue(-1200);

        List<ColumnStatsInfo> columnStatsInfoList = new ArrayList<>();
        columnStatsInfoList.add(columnStatsInfo1);
        columnStatsInfoList.add(columnStatsInfo2);

        tableStatsInfo = new TableStatsInfo(columnStatsInfoList, "JessIsc00L");
        tableStatsInfo.setNumberOfTuples(500);
    }

    @Test
    void getColumnStatsInfoList() {
        assertEquals(tableStatsInfo.getColumnStatsInfoList().get(0), columnStatsInfo1);
        assertEquals(tableStatsInfo.getColumnStatsInfoList().get(1), columnStatsInfo2);
    }

    @Test
    void testToString() {
        assertEquals("JessIsc00L 500 V,-1,1000 B,-1200,2000", tableStatsInfo.toString());
    }
}