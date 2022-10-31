package com.cs4321.indexes;

import java.util.List;

/**
 * IndexNode class representing index nodes in the B+ Tree
 */
public class IndexNode extends Node{
    int numberOfKeys;
    List<Integer> keysList;
    List<Integer> addressList;

    /**
     * Sets the node flag to 1 representing an index node
     */
    IndexNode () {
        super(1);
    }

    /** Returns the number of keys in the node
     * @return the number of keys in the node
     */
    public int getNumberOfKeys() {
        return numberOfKeys;
    }

    /**
     * Adds a child to this node
     * @param key
     * @param address
     */
    public void addChild(int key, int address) {
        keysList.add(key);
        addressList.add(address);
        numberOfKeys++;
    }
}
