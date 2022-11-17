package com.cs4321.app;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Stats {
    private TupleReader tupleReader;
    private TableStatsInfo tableStatsInfo;

    public Stats(String table, TableStatsInfo tableStatsInfo) {
        try {
            this.tupleReader = new TupleReader(DatabaseCatalog.getInstance().tablePath(table));
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
        this.tableStatsInfo = tableStatsInfo;
    }

    public Tuple getNextTuple() {
        try {
            return tupleReader.readNextTuple();
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
        return null;
    }

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
