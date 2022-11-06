package com.cs4321.indexes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeafNodeTest {
    private LeafNode leafNode = new LeafNode(9);
    private DataEntry dataEntry = new DataEntry(5);
    private List<DataEntry> dataEntryList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dataEntry.addRid(new Rid(1, 2));
        dataEntry.addRid(new Rid(3, 4));
        leafNode.addDataEntry(dataEntry);
        dataEntryList.add(dataEntry);
    }

    @Test
    void getNumberOfDataEntries() {
        assertEquals(1, leafNode.getNumberOfDataEntries());
    }

    @Test
    void getDataEntries() {
        assertEquals(dataEntryList, leafNode.getDataEntries());
    }

    @Test
    void addDataEntry() {
        assertEquals(leafNode.getDataEntries().size(), 1);
    }
}