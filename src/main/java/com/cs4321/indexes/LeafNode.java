package com.cs4321.indexes;

import java.util.ArrayList;
import java.util.List;

public class LeafNode extends Node {
    List<DataEntry> dataEntries;
    int numberOfDataEntires;

    LeafNode() {
        super(0);
    }

    public int getNumberOfDataEntires() {
        return numberOfDataEntires;
    }

    public List<DataEntry> getDataEntries() {
        return dataEntries;
    }

    public void addDataEntry(DataEntry dataEntry) {
        dataEntries.add(dataEntry);
        numberOfDataEntires++;
    }


    public class DataEntry {
        List<Rid> rids = new ArrayList<>();
        int key;
        int numberOfRids;

        DataEntry(int key) {
            this.key = key;
        }

        DataEntry(int key, List<Rid> rids) {
            this(key);
            this.rids = rids;
        }

        public int getNumberOfRids() {
            return numberOfRids;
        }

        public void addRid(Rid rid) {
            this.rids.add(rid);
            numberOfRids++;
        }
    }

    public class Rid {
        int pageId;
        int tupleId;

        Rid(int pageId, int tupleId) {
            this.pageId = pageId;
            this.tupleId = tupleId;
        }
    }
}
