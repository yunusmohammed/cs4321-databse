package com.cs4321.indexes;

import com.cs4321.app.BuilderConfig;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.IndexInfo;
import com.cs4321.app.IndexInfoConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This is the B+ Tree for building indexes in the database. It is implemented
 * using the bulk loading algorithm
 */
public class BPlusTree {
    private BPlusTreeSerializer bPlusTreeSerializer;
    private int order;
    private boolean isClustered;
    private String relationName;
    private String attributeName;

    /**
     * Initialises a new B+ Tree object
     * @param filename The file in which the B+ tree is going to be serialized to
     */
    public BPlusTree(String filename, String relationName) {
        try {
            this.bPlusTreeSerializer = new BPlusTreeSerializer(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        IndexInfoConfig indexInfoConfig = new IndexInfoConfig(DatabaseCatalog.getInputdir()
                    + File.separator + "db" + File.separator + "index_info.txt");
        List<IndexInfo> indexInfoConfigList = indexInfoConfig.getIndexInfoList();
        for (IndexInfo indexInfo : indexInfoConfigList) {
            if (indexInfo.getRelationName().equals(relationName)) {
                this.order = indexInfo.getOrder();
                this.relationName = indexInfo.getRelationName();
                this.attributeName = indexInfo.getAttributeName();
                this.isClustered = indexInfo.isClustered();
            }
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

    @Override
    public String toString() {
        return "BPlusTree{" +
                "order=" + order +
                ", isClustered=" + isClustered +
                ", relationName='" + relationName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                '}';
    }
}
