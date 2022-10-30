package com.cs4321.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IndexInfoConfig {
    List<IndexInfo> indexInfoList;

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

    public List<IndexInfo> getIndexInfoList() {
        return indexInfoList;
    }
}