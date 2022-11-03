package com.cs4321.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IndexInfoConfigTest {
    private static final String sep = File.separator;
    private final String basePath = System.getProperty("user.dir") + sep
            + "src" + sep + "test" + sep + "resources" + sep + "sampleIndexInfoConfigs";

    @Test
    void getIndexInfoList() {
        IndexInfoConfig indexInfoConfig = new IndexInfoConfig(basePath + sep + "config1.txt");
        Map<String, IndexInfo> indexInfoMap = new HashMap<>();
        indexInfoMap.put("Boats.E", new IndexInfo("Boats", "E", false, 10));
        indexInfoMap.put("Sailors.A", new IndexInfo("Sailors", "A", true, 15));
        Map<String, IndexInfo> expectedIndexInfoMap = indexInfoConfig.getIndexInfoMap();
        for (String fullColumnName : expectedIndexInfoMap.keySet()) {
            assertDoesNotThrow(() -> indexInfoMap.get(fullColumnName));
            IndexInfo indexInfo = indexInfoMap.get(fullColumnName);
            IndexInfo expectedIndexInfo = expectedIndexInfoMap.get(fullColumnName);
            assertEquals(indexInfo.getRelationName(), expectedIndexInfo.getRelationName());
            assertEquals(indexInfo.getAttributeName(), expectedIndexInfo.getAttributeName());
            assertEquals(indexInfo.getOrder(), expectedIndexInfo.getOrder());
            assertEquals(indexInfo.isClustered(), expectedIndexInfo.isClustered());
        }
    }
}