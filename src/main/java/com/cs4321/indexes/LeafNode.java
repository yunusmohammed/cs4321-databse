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

    public void setNumberOfDataEntires(int numberOfDataEntires) {
        this.numberOfDataEntires = numberOfDataEntires;
    }

    public List<DataEntry> getDataEntries() {
        return dataEntries;
    }

    public void setDataEntries(List<DataEntry> dataEntries) {
        this.dataEntries = dataEntries;
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

        void addRid(Rid rid) {
            this.rids.add(rid);
        }

        public int getKey() {
            return key;
        }

        public List<Rid> getRids() {
            return rids;
        }

        public int getNumberOfRids() {
            return numberOfRids;
        }

        public void setNumberOfRids(int numberOfRids) {
            this.numberOfRids = numberOfRids;
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
