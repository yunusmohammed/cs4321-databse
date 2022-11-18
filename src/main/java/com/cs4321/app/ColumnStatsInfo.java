package com.cs4321.app;

/**
 * Data class to represent the meta data of a relation
 */
public class ColumnStatsInfo {
    private final String columnName;
    private int minValue = Integer.MAX_VALUE;
    private int maxValue = Integer.MIN_VALUE;

    /**
     * Creates a new ColumnStatsInfo object
     *
     * @param columnName Name of the column
     */
    public ColumnStatsInfo(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the minimum value of a data unit in the table
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * @return the maximum value of a data unit in the table
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Set the minValue
     *
     * @param minValue the minimum value of a data unit in the table
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    /**
     * Set the maxValue
     *
     * @param maxValue the maximum value of a data unit in the table
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%d", columnName, minValue, maxValue);
    }
}
