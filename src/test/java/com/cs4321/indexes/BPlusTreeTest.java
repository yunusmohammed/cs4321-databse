package com.cs4321.indexes;

import com.cs4321.app.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BPlusTreeTest {

    private static DatabaseCatalog dbc = Mockito.mock(DatabaseCatalog.class);
    private static String sep = File.separator;
    private static String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input_indexes";

    private static String pathToTable(String table) {
        return DatabaseCatalog.getInputdir() + sep + "db" + sep + "data" + sep + table;
    }

    @Test
    void testBPlusTree() throws IOException {
        try (MockedStatic<DatabaseCatalog> dbcMockedStatic = Mockito.mockStatic(DatabaseCatalog.class)) {
            dbcMockedStatic.when(DatabaseCatalog::getInputdir).thenReturn(inputdir);
            assertEquals(inputdir, DatabaseCatalog.getInputdir());
            dbcMockedStatic.when(DatabaseCatalog::getInstance).thenReturn(dbc);

            String indexInfoPath = DatabaseCatalog.getInputdir() + sep + "db" + sep + "index_info.txt";
            Mockito.when(dbc.readFile(indexInfoPath)).thenReturn(Files.readAllLines(Paths.get(indexInfoPath)));
            String boatsPath = pathToTable("Boats");
            String sailorsPath = pathToTable("Sailors");
            Mockito.when(dbc.tablePath("Boats")).thenReturn(boatsPath);
            Mockito.when(dbc.tablePath("Sailors")).thenReturn(sailorsPath);
            HashMap<String, Integer> boatsMap = new HashMap<>(), sailorsMap = new HashMap<>();
            boatsMap.put("E", 1);
            sailorsMap.put("A", 0);
            Mockito.when(dbc.columnMap("Boats")).thenReturn(boatsMap);
            Mockito.when(dbc.columnMap("Sailors")).thenReturn(sailorsMap);

            List<IndexInfo> indexInfos = Interpreter.buildIndexInfos();
            List<BPlusTree> trees = new ArrayList<>();
            String indexesPath = DatabaseCatalog.getInputdir() + sep + "db" + sep + "indexes";
            for(IndexInfo indexinfo : indexInfos) {
                trees.add(new BPlusTree(indexesPath + sep + indexinfo.getRelationName() + "." + indexinfo.getAttributeName(), indexinfo));
            }
            trees.get(0).printTree();
            File[] correctQueries = new File(System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "correctIndexes").listFiles();
            File[] outputQueries = new File(indexesPath).listFiles();
            if(correctQueries.length != outputQueries.length) throw new Error();
            for(int i=0; i<outputQueries.length; i++) {
                System.out.println(correctQueries[i].getName());
                System.out.println(outputQueries[i].getName());
                assertTrue(FileUtils.contentEquals(correctQueries[i], outputQueries[i]));
            }
        }
    }
}