package com.cs4321.indexes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * IndexNode class representing index nodes in the B+ Tree
 */
public class IndexNode extends Node {
    private int numberOfKeys;
    private List<Integer> keysList;
    private List<Node> children;

    /**
     * Sets the node flag to 1 representing an index node
     */
    IndexNode(int address) {
        super(1, address);
        keysList = new ArrayList<>();
        children = new ArrayList<>();
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
     * Returns the keys for this node
     * @return the keys for this node
     */
    public List<Integer> getKeysList() { return keysList; }

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
     * Adds the child to the current node
     * @param child - child to be added
     */
    public void addChild(Node child) {
        this.children.add(child);
    }

    /**
     * Adds a list of children to the node
     * @param children - the children of the node
     */
    public void addChildren(Collection<Node> children) {
        this.children.addAll(children);
    }

    /**
     * Returns the children of this node
     * @return - the children of this node
     */
    public List<Node> getChildren() { return this.children; }

    /**
     * Returns the string representation of the IndexNode
     *
     * @return The string representation of the IndexNode
     */
    @Override
    public String toString() {
        List<Integer> childAddresses = new ArrayList<>();
        for(Node n : children) {
            childAddresses.add(n.getAddress());
        }
        return "IndexNode with keys " + keysList + " and child addresses " + childAddresses;
    }
}
