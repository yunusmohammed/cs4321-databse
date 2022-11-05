package com.cs4321.indexes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataEntryTest {
    private DataEntry dataEntry = new DataEntry(5);
    private List<Rid> ridList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Rid rid1 = new Rid(1, 2);
        Rid rid2 = new Rid(3, 4);
        ridList.add(rid1);
        ridList.add(rid2);
        dataEntry.addRid(rid1);
        dataEntry.addRid(rid2);
    }

    @Test
    void getNumberOfRids() {
        assertEquals(2, dataEntry.getNumberOfRids());
    }


    @Test
    void getRids() {
        assertEquals(ridList, dataEntry.getRids());
    }

    @Test
    void getKey() {
        assertEquals(5, dataEntry.getKey());
    }
}