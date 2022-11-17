package com.cs4321.app;

public class ColumnStatsInfo {
    private String columnName;
    private int minValue = Integer.MAX_VALUE;
    private int maxValue = Integer.MIN_VALUE;

    public ColumnStatsInfo(String columnName) {
        this.columnName = columnName;
    }

    public ColumnStatsInfo(String columnName, int minValue, int maxValue) {
        this(columnName);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%d", columnName, minValue, maxValue);
    }
}
