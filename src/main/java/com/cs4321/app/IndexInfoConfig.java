package com.cs4321.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jessica Tweneboah
 */
public class IndexInfoConfig {
    Map<String, IndexInfo> indexInfoMap;

    /**
     * Initialises an IndexInfoConfig Object
     *
     * @param indexInfofilePath The path to the Index Info Config File
     */
    public IndexInfoConfig(String indexInfofilePath) {
        indexInfoMap = new HashMap<>();
        try {
            List<String> configLines = Files.readAllLines(Paths.get(indexInfofilePath));
            for (String configLine : configLines) {
                String[] indexConfig = configLine.split(" ");
                IndexInfo indexInfo = new IndexInfo(indexConfig[0], indexConfig[1], Integer.parseInt(indexConfig[2]) == 1, Integer.parseInt(indexConfig[3]));
                indexInfoMap.put(String.format("%s.%s", indexInfo.getRelationName(), indexInfo.getAttributeName()), indexInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns a map that maps full column name to IndexInfo Objects that store information that
     * specifies how indexes are built
     *
     * @return A map that maps full column name to IndexInfo Objects that store information that
     * specifies how indexes are built
     * Eg: Boats.E -> IndexInfo< RelationName: Boats, AttributeName: E, Clustered: 0, Order: 10 >
     */
    public Map<String, IndexInfo> getIndexInfoMap() {
        return indexInfoMap;
    }
}