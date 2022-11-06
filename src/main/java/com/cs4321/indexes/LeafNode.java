package com.cs4321.indexes;

import java.util.ArrayList;
import java.util.List;

/**
 * LeafNode class representing leaf nodes in the B+ Tree
 */
public class LeafNode extends Node {
    private List<DataEntry> dataEntries;
    private int numberOfDataEntries;

    /**
     * Sets the node flag to 0 representing a leaf node
     */
    LeafNode(int address) {
        super(0, address);
        dataEntries = new ArrayList<>();
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
                ", nodeFlag=" + this.getNodeFlag() +
                '}';
    }
}
