package com.cs4321.indexes;

import com.cs4321.app.*;
import net.sf.jsqlparser.statement.create.table.Index;

import javax.swing.plaf.IconUIResource;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Comparator;

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
    private IndexNode root;
    private Logger logger = Logger.getInstance();
    private DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private final int PAGE_SIZE = 4096;
    private int leafCount = 0;

    /**
     * Initialises a new B+ Tree object
     * @param filename - path to the index
     * @param indexInfo - information on the index for the B+ Tree we are creating
     */
    public BPlusTree(String filename, IndexInfo indexInfo) {
        this.order = indexInfo.getOrder();
        this.relationName = indexInfo.getRelationName();
        this.attributeName = indexInfo.getAttributeName();
        this.isClustered = indexInfo.isClustered();
        if(isClustered) sortRelation(indexInfo);
        try {
            this.bPlusTreeSerializer = new BPlusTreeSerializer(filename);
        } catch (FileNotFoundException e) {
            Logger.getInstance().log(e.getMessage());
        }
        buildIndex();
    }

    //---------------- BULK LOADING ALGORITHM---------------------

    /**
     * Builds the index and sets the root
     */
    private void buildIndex() {
        try {
            TupleReader reader = new TupleReader(dbc.tablePath(relationName));
            Tuple cur = reader.readNextTuple();
            // can assume relation is never empty
            int tupleSize = cur.size(), pageId = 0, tupleId = 0;
            int attributeColumn = dbc.columnMap(relationName).get(attributeName);
            // map from keys to DataEntry objects for that key
            HashMap<Integer, DataEntry> entriesMap = new HashMap<>();
            // build data entries by adding the current tuple to the data entry which has the correct key
            while(cur != null) {
                if(tupleSize * 4 * (tupleId + 1) > PAGE_SIZE) {
                    pageId++;
                    tupleId = 0;
                }
                int key = cur.get(attributeColumn);
                DataEntry entry = entriesMap.getOrDefault(key, new DataEntry(key));
                entry.addRid(new Rid(pageId, tupleId));
                entriesMap.put(key, entry);
                tupleId++;
                cur = reader.readNextTuple();
            }
            // sort data entries by key
            PriorityQueue<DataEntry> entries = new PriorityQueue<>((a, b) -> a.getKey() - b.getKey());
            entries.addAll(entriesMap.values());
            // sort rids for each data entry
            for(DataEntry entry : entries) {
                Collections.sort(entry.rids, (a, b) -> compareRids(a, b));
            }
            int numberOfLeaves = (int) Math.ceil(entries.size() / (2.0 * order));
            // count number of index nodes
            int numberOfIndexNodes = 0;
            int lowerlevel = numberOfLeaves;
            while(lowerlevel > 0) {
                int nodesAtCurLevel = (int) Math.ceil(lowerlevel / (2.0 * order + 1));
                numberOfIndexNodes += nodesAtCurLevel;
                lowerlevel = nodesAtCurLevel == 1 ? 0 : nodesAtCurLevel;
            }
            bPlusTreeSerializer.writeHeaderPage(numberOfLeaves + numberOfIndexNodes, numberOfLeaves, order);
            Queue<Node> leaves = buildLeafLayer(entries);
            buildIndexLayer(leaves);

        } catch (IOException e) {
            logger.log("Unable to build index for table " + relationName);
            throw new Error();
        }
    }

    /**
     * Comparator used to sort the data entries for a given key. The entries are sorted first by pageid then by tupleid.
     * @param a - data entry being compared to b
     * @param b - data entry being compared to a
     * @return - a negative number if a should be first in sorted order, 0 if a and b have the same pageid and
     * tupleid, and a positive number otherwise.
     */
    private int compareRids(Rid a, Rid b) {
        if(a.getPageId() == b.getPageId()) return a.getTupleId() - b.getTupleId();
        return a.getPageId() - b.getPageId();
    }

    private void sortRelation(IndexInfo indexInfo) {
        String filename = dbc.tablePath(indexInfo.getRelationName());
        SortingUtilities.sortFile(filename, filename, indexInfo);
    }

    /**
     * Builds and serializes the leaf layer
     * @param entries - a queue containing all data entries in sorted order
     * @return - a queue of all leaf nodes added from left to right
     */
    private Queue<Node> buildLeafLayer(PriorityQueue<DataEntry> entries) {
        try {
            Queue<Node> leaves = new ArrayDeque<>();
            int address = 1;
            while(entries.size() > 0) {
                int size = entries.size();
                if(size >= 3 * order) {
                    LeafNode leaf = new LeafNode(address++);
                    for(int i=0; i<2 * order; i++) {
                        leaf.addDataEntry(entries.poll());
                    }
                    bPlusTreeSerializer.writeLeafNodeToPage(leaf);
                    leaves.add(leaf);
                    leafCount++;
                }
                else if(size > 2 * order && size < 3 * order) {
                    LeafNode secondLast = new LeafNode(address++);
                    LeafNode last = new LeafNode(address++);
                    for(int i=0; i<size / 2; i++) {
                        secondLast.addDataEntry(entries.poll());
                    }
                    while(entries.size() > 0) {
                        last.addDataEntry(entries.poll());
                    }
                    bPlusTreeSerializer.writeLeafNodeToPage(secondLast);
                    bPlusTreeSerializer.writeLeafNodeToPage(last);
                    leaves.add(secondLast);
                    leaves.add(last);
                    leafCount += 2;
                }
                else {
                    LeafNode last = new LeafNode(address++);
                    while(entries.size() > 0) {
                        last.addDataEntry(entries.poll());
                    }
                    bPlusTreeSerializer.writeLeafNodeToPage(last);
                    leaves.add(last);
                    leafCount++;
                }
            }
            return leaves;
        } catch (IOException e) {
            logger.log("Error writing leaf node to page while building index " + relationName);
            throw new Error();
        }
    }

    /**
     * Returns a key for an index node by using the smallest search key found in the leftmost leaf of the given subtree.
     * @param node - the subtree for which we are finding the leftmost leaf
     * @return an integer for the key of the index node
     */
    private Integer findKey(Node node) {
        if(node instanceof LeafNode) {
            return ((LeafNode) node).getDataEntries().get(0).getKey();
        }
        else {
            return findKey(((IndexNode) node).getChildren().get(0));
        }
    }

    /**
     * Builds and serializes an index node
     * @param children - the children of the index node
     * @param address - the address of the index node
     * @return - the index node
     * @throws IOException if there is an error serializing index node
     */
    private IndexNode buildIndexNode(List<Node> children, int address) throws IOException {
        IndexNode node = new IndexNode(address);
        node.addChildren(children);
        for(int i=1; i<children.size(); i++) {
            node.addKey(findKey(children.get(i)));
        }
        bPlusTreeSerializer.writeIndexNodeToPage(node);
        return node;
    }

    /**
     * Builds and serializes the index layers of the B+ tree and sets the root.
     * @param leaves - a queue of the leaves for the tree
     */
    private void buildIndexLayer(Queue<Node> leaves) {
        try {
            // check if tree has only one leaf node
            if(leaves.size() == 1) {
                root = new IndexNode(2);
                root.addChild(leaves.poll());
                return;
            }
            Queue<Node> curLayer = new ArrayDeque<>();
            Queue<Node> prevLayer = leaves;
            int address = leaves.size()+1;
            // iterating over each layer - will terminate after building the root
            while(prevLayer.size() > 1) {
                // iterating over every node in the previous layer to build current layer
                while(prevLayer.size() > 0) {
                    int size = prevLayer.size();
                    if(size >= 3 * order + 2) {
                        List<Node> children = new ArrayList<>();
                        for(int i=0; i<2 * order + 1; i++) {
                            children.add(prevLayer.poll());
                        }
                        IndexNode idxNode = buildIndexNode(children, address++);
                        curLayer.add(idxNode);
                    }
                    else if(size > 2 * order + 1 && size < 3 * order + 2) {
                        List<Node> secondLastChildren = new ArrayList<>(), lastChildren = new ArrayList<>();
                        for(int i=0; i< size / 2; i++) {
                            secondLastChildren.add(prevLayer.poll());
                        }
                        while(prevLayer.size() > 0) {
                            lastChildren.add(prevLayer.poll());
                        }
                        IndexNode secondLast = buildIndexNode(secondLastChildren, address++);
                        IndexNode last = buildIndexNode(lastChildren, address++);
                        curLayer.add(secondLast);
                        curLayer.add(last);
                    }
                    else {
                        List<Node> children = new ArrayList<>();
                        while(prevLayer.size() > 0) {
                            children.add(prevLayer.poll());
                        }
                        IndexNode last = buildIndexNode(children, address++);
                        curLayer.add(last);
                    }
                }
                prevLayer = curLayer;
                curLayer = new ArrayDeque<>();
            }
            root = (IndexNode) prevLayer.poll();
        } catch (IOException e) {
            logger.log("Error writing index node to page while building index " + relationName);
            throw new Error();
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
            Logger.getInstance().log(e.getMessage());
        }
    }

    /**
     * Prints out a string representation of this tree
     */
    public void printTree() {
        Node n = root;
        Queue<Node> bfs = new ArrayDeque<>();

        System.out.println("Header page info: tree has order " + order + ", a root at address " + root.getAddress() + " and " + leafCount + " leaf nodes\n");
        System.out.println("Root node is: " + root.toString() + "\n");


    }

    @Override
    public String toString() {
        return "BPlusTree{" +
                "order=" + order +
                ", isClustered=" + isClustered +
                ", relationName='" + relationName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", tree= " + root.toString() +
                '}';
    }
}
