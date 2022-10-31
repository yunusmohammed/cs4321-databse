package com.cs4321.indexes;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the B+ Tree for building indexes in the database. It is implemented
 * using the bulk loading algorithm
 */
public class BPlusTree {
    BPlusTreeSerializer bPlusTreeSerializer;
    int order;

    /**
     * Initialises a new B+ Tree object
     * @param filename The file in which the B+ tree is going to be serialized to
     */
    public BPlusTree(String filename) {
        try {
            this.bPlusTreeSerializer = new BPlusTreeSerializer(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //---------------- BULK LOADING ALGORITHM---------------------
    void scanTableFileAndSort() {
        //TODO Yohanes
    }

    void buildLeafLayer() {
        //TODO Yohanes
        try {
            bPlusTreeSerializer.writeLeafNodeToPage(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void buildIndexLayer() {
        //TODO Yohanes
        try {
            bPlusTreeSerializer.writeIndexNodeToPage(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cleanups any resources that need to be cleaned up such as BufferedReader,
     * etc.
     */
    @Override
    public void finalize() {
        try {
            bPlusTreeSerializer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
