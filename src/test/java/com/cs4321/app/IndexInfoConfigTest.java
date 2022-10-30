package com.cs4321.app;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IndexInfoConfigTest {
    private static final String sep = File.separator;
    private final String basePath = System.getProperty("user.dir") + sep
            + "src" + sep + "test" + sep + "resources" + sep + "sampleIndexInfoConfigs";

    @Test
    void getIndexInfoList() {
        IndexInfoConfig indexInfoConfig = new IndexInfoConfig(basePath + sep + "config1.txt");
        List<IndexInfo> indexInfoList = new ArrayList<>();
        indexInfoList.add(new IndexInfo("Boats", "E", false, 10));
        indexInfoList.add(new IndexInfo("Sailors", "A", true, 15));
        List<IndexInfo> expectedInfoList = indexInfoConfig.getIndexInfoList();
        for (int i = 0; i < indexInfoList.size(); i++) {
            assertEquals(indexInfoList.get(i).getRelationName(), expectedInfoList.get(i).getRelationName());
            assertEquals(indexInfoList.get(i).getAttributeName(), expectedInfoList.get(i).getAttributeName());
            assertEquals(indexInfoList.get(i).getOrder(), expectedInfoList.get(i).getOrder());
            assertEquals(indexInfoList.get(i).isClustered(), expectedInfoList.get(i).isClustered());

        }
    }
}