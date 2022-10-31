package com.cs4321.indexes;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This is the B+ Tree
 */
public class BPlusTree {
    BPlusTreeSerializer bPlusTreeSerializer;
    int order;

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
    }

    void buildIndexLayer() {
        //TODO Yohanes
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
