package com.cs4321.app;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for generating the stats.txt file
 */
public class Stats {
    private TupleReader tupleReader;
    private TableStatsInfo tableStatsInfo;

    /**
     * Initiliases a new Stats object
     *
     * @param table          the current relation
     * @param tableStatsInfo the table metadata
     */
    public Stats(String table, TableStatsInfo tableStatsInfo) {
        try {
            this.tupleReader = new TupleReader(DatabaseCatalog.getInstance().tablePath(table));
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
        this.tableStatsInfo = tableStatsInfo;
    }

    /**
     * Returns the next tuple in the table
     *
     * @return the next tuple in the table
     */
    private Tuple getNextTuple() {
        try {
            return tupleReader.readNextTuple();
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
        return null;
    }

    /**
     * Write stats about this table to the provided stats.txt file
     *
     * @param filename the stats.txt to write the table statistics to
     */
    public void generateStatistics(File filename) {
        Tuple nextTuple = getNextTuple();
        int numberOfTuples = 0;
        while (nextTuple != null) {
            numberOfTuples++;
            for (int i = 0; i < nextTuple.size(); i++) {
                int value = nextTuple.get(i);
                ColumnStatsInfo columnStatsInfo = tableStatsInfo.getColumnStatsInfoList().get(i);
                columnStatsInfo.setMaxValue(Math.max(value, columnStatsInfo.getMaxValue()));
                columnStatsInfo.setMinValue(Math.min(value, columnStatsInfo.getMinValue()));
            }
            nextTuple = getNextTuple();
        }
        tableStatsInfo.setNumberOfTuples(numberOfTuples);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true));
            bufferedWriter.write(String.valueOf(tableStatsInfo));
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
    }
}
