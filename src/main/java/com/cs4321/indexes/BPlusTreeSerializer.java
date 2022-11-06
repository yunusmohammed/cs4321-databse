package com.cs4321.indexes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Serialises B+ Tree to a file
 */
public class BPlusTreeSerializer {
    private FileOutputStream fout;
    private FileChannel fc;
    private ByteBuffer buffer;
    private final String filename;
    private final int PAGE_SIZE = 4096;
    private int numberOfPages = 0;

    /**
     * Initialises B+ Tree serializer
     *
     * @param filename is the name of the file that the B+ tree is serialised to
     * @throws FileNotFoundException
     */
    public BPlusTreeSerializer(String filename) throws FileNotFoundException {
        this.fout = new FileOutputStream(filename);
        this.fc = fout.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
    }

    /**
     * Writes the header page
     *
     * @param rootAddress          the address of the root, stored at offset 0 on
     *                             the header page.
     *                             The address of a node is the number of the page
     *                             it is serialized on.
     * @param numberOfLeavesInTree the number of leaves in the tree, at offset 4
     * @param order                the order of the tree, at offset 8
     * @throws IOException
     */
    public void writeHeaderPage(int rootAddress, int numberOfLeavesInTree, int order) throws IOException {
        buffer.putInt(rootAddress);
        buffer.putInt(numberOfLeavesInTree);
        buffer.putInt(order);
        writePageAndPrepForNewWrite();
        numberOfPages++;
    }

    /**
     * Writes a leaf node to a page.
     *
     * @param leafNode the leaf Node to be written to the page. We assume every node
     *                 will fit
     *                 in a 4096-byte page.
     * @throws IOException
     */
    public void writeLeafNodeToPage(LeafNode leafNode) throws IOException {
        buffer.putInt(leafNode.getNodeFlag());
        buffer.putInt(leafNode.getNumberOfDataEntries());
        for (DataEntry dataEntry : leafNode.getDataEntries()) {
            buffer.putInt(dataEntry.key);
            buffer.putInt(dataEntry.getNumberOfRids());
            for (Rid rid : dataEntry.rids) {
                buffer.putInt(rid.pageId);
                buffer.putInt(rid.tupleId);
            }
        }
        writePageAndPrepForNewWrite();
        numberOfPages++;
    }

    /**
     * Writes an index node to a page.
     *
     * @param indexNode the index Node to be written to the page. We assume every
     *                  node will fit
     *                  in a 4096-byte page.
     * @throws IOException
     */
    public void writeIndexNodeToPage(IndexNode indexNode) throws IOException {
        buffer.putInt(indexNode.getNodeFlag());
        buffer.putInt(indexNode.getNumberOfKeys());
        for (int key : indexNode.getKeysList()) {
            buffer.putInt(key);
        }
        for (Node child : indexNode.getChildren()) {
            buffer.putInt(child.getAddress());
        }
        writePageAndPrepForNewWrite();
        numberOfPages++;
    }

    /**
     * Cleans up buffer and clears it for a new write
     *
     * @throws IOException
     */
    public void writePageAndPrepForNewWrite() throws IOException {
        buffer.clear();
        fc.write(buffer);
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
    }

    /**
     * Removes all opened and unused resources
     *
     * @throws IOException
     */
    public void close() throws IOException {
        fout.close();
        fc.close();
    }

}
