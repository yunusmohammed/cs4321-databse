package com.cs4321.app;

import java.util.List;

/**
 * Data class to represent the meta data of a relation
 */
public class TableStatsInfo {
    private final List<ColumnStatsInfo> columnStatsInfoList;
    private int numberOfTuples = 0;
    private final String tableName;


    /**
     * Creates a new TableStatsInfo object
     *
     * @param columnStatsInfoList List of object
     * @param tableName           the name of the relation
     */
    public TableStatsInfo(List<ColumnStatsInfo> columnStatsInfoList, String tableName) {
        this.columnStatsInfoList = columnStatsInfoList;
        this.tableName = tableName;
    }


    /**
     * Set the total number of tuples in the table
     *
     * @param numberOfTuples total number of tuples in the table
     */
    public void setNumberOfTuples(int numberOfTuples) {
        this.numberOfTuples = numberOfTuples;
    }


    /**
     * Returns the meta data for all columns in a table
     *
     * @return the meta data for all columns in a table
     */
    public List<ColumnStatsInfo> getColumnStatsInfoList() {
        return columnStatsInfoList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(String.format("%s %d", tableName, numberOfTuples));
        for (ColumnStatsInfo columnStatsInfo : columnStatsInfoList) {
            stringBuilder.append(String.format(" %s", columnStatsInfo));
        }
        return stringBuilder.toString();
    }
}
