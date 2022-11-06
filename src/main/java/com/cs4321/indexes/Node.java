package com.cs4321.indexes;

/**
 * Node class representing nodes in the B+ Tree
 */
public abstract class Node {
    private int nodeFlag;
    private int address;

    /**
     * Initialises a Node object
     *

     */

    /**
     * Initialises a Node object
     * @param nodeFlag flag to indicate what kind of node this is. 0 represents a leaf node, 1 represents an index node
     * @param address integer representing address of this node
     */
    Node(int nodeFlag, int address) {
        this.nodeFlag = nodeFlag;
        this.address = address;
    }

    /**
     * Returns the nodeFlag of this Node
     *
     * @return the nodeFlag of this Node. 0 represents a leaf node, 1 represents an index node
     */
    public int getNodeFlag() {
        return nodeFlag;
    }

    /**
     * Returns the address of this Node in its index
     * @return the address of this Node in its index
     */
    public int getAddress() { return address; }
}
