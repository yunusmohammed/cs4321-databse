package com.cs4321.indexes;

import java.util.List;

/**
 * IndexNode class representing index nodes in the B+ Tree
 */
public class IndexNode extends Node {
    int numberOfKeys;
    List<Integer> keysList;
    List<Integer> addressList;

    /**
     * Sets the node flag to 1 representing an index node
     */
    IndexNode() {
        super(1);
    }

    /**
     * Returns the number of keys in the node
     *
     * @return the number of keys in the node
     */
    public int getNumberOfKeys() {
        return numberOfKeys;
    }

    /**
     * Adds a key to this node
     *
     * @param key is the (integer) search key for the index
     */
    public void addKey(int key) {
        keysList.add(key);
        numberOfKeys++;
    }

    /**
     * Adds an address to this node
     *
     * @param address is the number of the page it is serialized on.
     */
    public void addAddress(int address) {
        addressList.add(address);
    }

    @Override
    public String toString() {
        return "IndexNode{" +
                "numberOfKeys=" + numberOfKeys +
                ", keysList=" + keysList +
                ", addressList=" + addressList +
                ", nodeFlag=" + nodeFlag +
                '}';
    }
}
