package com.cs4321.app;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomDataGeneratorTest {

    Logger logger = Logger.getInstance();

    // creates a table with 30 columns and rows with values of 30
    @Test
    void constructorTest() {
        RandomDataGenerator randomData = new RandomDataGenerator("dataTable", 30, 30, 30, 30, 30, 30);
        assertEquals(30, randomData.getNumColumns());
        assertEquals(30, randomData.getNumTuples());
        TupleReader reader;
        List<Tuple> allRows = new ArrayList<>();
        try {
            reader = new TupleReader(randomData.getTablePath());
            Tuple tuple = reader.readNextTuple();
            while(tuple != null) {
                allRows.add(tuple);
                tuple = reader.readNextTuple();
            }
        } catch (IOException e) {
            logger.log("Error creating data for RandomDataGeneratorTest.");
            throw new Error();
        }
        for(int i=0; i<allRows.size(); i++) {
            for(int j=0; j<allRows.get(i).size(); j++) {
                assertEquals(30, allRows.get(i).get(j));
            }
        }
        assertEquals(30, allRows.size());
    }

}