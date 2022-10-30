package com.cs4321.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jessica Tweneboah
 */
public class IndexInfoConfig {
    List<IndexInfo> indexInfoList;

    /**
     * Initialises an IndexInfoConfig Object
     *
     * @param indexInfofilePath The path to the Index Info Config File
     */
    public IndexInfoConfig(String indexInfofilePath) {
        indexInfoList = new ArrayList<>();
        try {
            List<String> configLines = Files.readAllLines(Paths.get(indexInfofilePath));
            for (String configLine : configLines) {
                String[] indexConfig = configLine.split(" ");
                IndexInfo indexInfo = new IndexInfo(
                        indexConfig[0],
                        indexConfig[1],
                        Integer.parseInt(indexConfig[2]) == 1,
                        Integer.parseInt(indexConfig[3]));
                indexInfoList.add(indexInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of IndexInfo Objects that store information that specifies how indexes are built
     *
     * @return A list of IndexInfo Objects that store information that specifies how indexes are built
     */
    public List<IndexInfo> getIndexInfoList() {
        return indexInfoList;
    }
}