package com.cs4321.app;

import net.sf.jsqlparser.statement.select.OrderByElement;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatsTest {
    private static String tablePath;
    private static Stats stats;
    private static final String sep = File.separator;

    @BeforeAll
    static void beforeAll() {
        DatabaseCatalog.setInputDir(System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_binary");
        try {
            tablePath = Files.createTempFile("stats", "txt").toString();
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
        ColumnStatsInfo columnStatsInfo1 = new ColumnStatsInfo("A");
        columnStatsInfo1.setMaxValue(1000);
        columnStatsInfo1.setMinValue(-1);

        ColumnStatsInfo columnStatsInfo2 = new ColumnStatsInfo("B");
        columnStatsInfo2.setMaxValue(2000);
        columnStatsInfo2.setMinValue(-1200);

        List<ColumnStatsInfo> columnStatsInfoList = new ArrayList<>();
        columnStatsInfoList.add(columnStatsInfo1);
        columnStatsInfoList.add(columnStatsInfo2);

        TableStatsInfo tableStatsInfo = new TableStatsInfo(columnStatsInfoList, "Reserves");
        tableStatsInfo.setNumberOfTuples(500);
        stats = new Stats("Reserves", tableStatsInfo);
        stats.generateStatistics(new File(tablePath));
    }

    @Test
    void generateStatistics() {
        try {
            List<String> readLines = Files.readAllLines(Paths.get(tablePath));
            assertEquals("Reserves 1000 A,-1,1000 B,-1200,2000", readLines.get(0));
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
    }
}