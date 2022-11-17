package com.cs4321.app;

import java.util.List;

public class TableStatsInfo {
    private List<ColumnStatsInfo> columnStatsInfoList;
    private int numberOfTuples = 0;
    private String tableName;

    public TableStatsInfo(List<ColumnStatsInfo> columnStatsInfoList, String tableName) {
        this.columnStatsInfoList = columnStatsInfoList;
        this.tableName = tableName;
    }

    public void setNumberOfTuples(int numberOfTuples) {
        this.numberOfTuples = numberOfTuples;
    }

    public List<ColumnStatsInfo> getColumnStatsInfoList() {
        return columnStatsInfoList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(String.format("%s %d", tableName, numberOfTuples));
        for (ColumnStatsInfo columnStatsInfo: columnStatsInfoList) {
            stringBuilder.append(String.format(" %s", columnStatsInfo));
        }
        return stringBuilder.toString();
    }
}
