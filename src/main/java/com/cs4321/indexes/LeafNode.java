package com.cs4321.indexes;

import java.util.ArrayList;
import java.util.List;

/**
 * LeafNode class representing leaf nodes in the B+ Tree
 */
public class LeafNode extends Node {
    List<DataEntry> dataEntries;
    int numberOfDataEntries;

    /**
     * Sets the node flag to 0 representing a leaf node
     */
    LeafNode() {
        super(0);
    }

    /**
     * Returns the number of data entries in the node
     *
     * @return the number of data entries in the node
     */
    public int getNumberOfDataEntries() {
        return numberOfDataEntries;
    }

    /**
     * Returns data entries in the node
     *
     * @return data entries in the node
     */
    public List<DataEntry> getDataEntries() {
        return dataEntries;
    }

    /**
     * Adds dataEntry to the list of dataEntries
     *
     * @param dataEntry of the format < k, [(p1, t1),(p2, t2), · · ·(pk, tk)] >
     */
    public void addDataEntry(DataEntry dataEntry) {
        dataEntries.add(dataEntry);
        numberOfDataEntries++;
    }

    /**
     * Returns the string representation of the LeafNode
     *
     * @return The string representation of the LeafNode
     */
    @Override
    public String toString() {
        return "LeafNode{" +
                "dataEntries=" + dataEntries +
                ", numberOfDataEntries=" + numberOfDataEntries +
                ", nodeFlag=" + nodeFlag +
                '}';
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

        /**
         * Initialises a new DataEntry object
         *
         * @param key is the (integer) search key for the index
         */
        DataEntry(int key) {
            this.key = key;
        }

        /**
         * Initialises a new DataEntry object
         *
         * @param key  is the (integer) search key for the index
         * @param rids is a list of record ids (rids)
         */
        DataEntry(int key, List<Rid> rids) {
            this(key);
            this.rids = rids;
        }

        /**
         * Returns the number of record ids in this DataEntry
         *
         * @return the number of record ids in this DataEntry
         */
        public int getNumberOfRids() {
            return numberOfRids;
        }

        /**
         * Adds rid to the list of record ids
         *
         * @param rid is a record id
         */
        public void addRid(Rid rid) {
            this.rids.add(rid);
            numberOfRids++;
        }

        /**
         * Returns the string representation of the DataEntry
         *
         * @return The string representation of the DataEntry
         */
        @Override
        public String toString() {
            return "DataEntry{" +
                    "rids=" + rids +
                    ", key=" + key +
                    ", numberOfRids=" + numberOfRids +
                    '}';
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

        /**
         * Initialises a new Rid object
         *
         * @param pageId  is the number of the page the tuple is on
         * @param tupleId is the number of the tuple on the page numbered pageid
         */
        Rid(int pageId, int tupleId) {
            this.pageId = pageId;
            this.tupleId = tupleId;
        }

        /**
         * Returns the string representation of the Rid
         *
         * @return The string representation of the Rid
         */
        @Override
        public String toString() {
            return "Rid{" +
                    "pageId=" + pageId +
                    ", tupleId=" + tupleId +
                    '}';
        }
    }
}
