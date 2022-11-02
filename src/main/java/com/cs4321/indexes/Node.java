package com.cs4321.indexes;

/**
 * Node class representing nodes in the B+ Tree
 */
public abstract class Node {
    int nodeFlag;

    /**
     * Initialises a Node object
     *
     * @param nodeFlag flag to indicate what kind of node this is.
     *                 0 represents a leaf node, 1 represents an index node
     */
    Node(int nodeFlag) {
        this.nodeFlag = nodeFlag;
    }

    /**
     * Returns the nodeFlag of this Node
     *
     * @return the nodeFlag of this Node. 0 represents a leaf node, 1 represents an index node
     */
    public int getNodeFlag() {
        return nodeFlag;
    }
}
