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


    /**
     * Leaf nodes contain data entries of the form < key, list >
     * where key is the (integer) search key for the index and
     * list is a list of record ids (rids).
     */
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

    /**
     * A rid is a tuple identifier and has the form (pageid, tupleid) where pageid is the
     * number of the page the tuple is on, and tupleid is the number of the tuple on the
     * page numbered pageid. For the purpose of rids, we number both pages and tuples
     * within pages starting at 0.
     */
    public class Rid {
        int pageId;
        int tupleId;

        Rid(int pageId, int tupleId) {
            this.pageId = pageId;
            this.tupleId = tupleId;
        }
    }
}
