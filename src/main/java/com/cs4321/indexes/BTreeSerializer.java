package com.cs4321.indexes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class BTreeSerializer {
    private FileOutputStream fout;
    private FileChannel fc;
    private ByteBuffer buffer;
    private final String filename;
    private final int PAGE_SIZE = 4096;
    private int numberOfPages = 0;

    public BTreeSerializer(String filename) throws FileNotFoundException {
        this.fout = new FileOutputStream(filename);
        this.fc = fout.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
    }

    public void writeHeaderPage(int rootAddress, int numberOfLeavesInTree, int order) throws IOException {
        buffer.putInt(rootAddress);
        buffer.putInt(numberOfLeavesInTree);
        buffer.putInt(order);
        writePageAndPrepForNewWrite();
        numberOfPages++;
    }

    public void writeLeafNodeToPage(LeafNode leafNode) throws IOException {
        buffer.putInt(leafNode.getNodeFlag());
        buffer.putInt(leafNode.getNumberOfDataEntires());
        for (LeafNode.DataEntry dataEntry : leafNode.getDataEntries()) {
            buffer.putInt(dataEntry.key);
            buffer.putInt(dataEntry.getNumberOfRids());
            for (LeafNode.Rid rid : dataEntry.rids) {
                buffer.putInt(rid.pageId);
                buffer.putInt(rid.tupleId);
            }
        }
        writePageAndPrepForNewWrite();
        numberOfPages++;
    }

    public void writeIndexNodeToPage(IndexNode indexNode) throws IOException {
        buffer.putInt(indexNode.getNodeFlag());
        buffer.putInt(indexNode.getNumberOfKeys());
        for (int key : indexNode.keysList) {
            buffer.putInt(key);
        }
        for (int address : indexNode.addressList) {
            buffer.putInt(address);
        }
        writePageAndPrepForNewWrite();
        numberOfPages++;
    }

    public void writePageAndPrepForNewWrite() throws IOException {
        buffer.clear();
        fc.write(buffer);
        buffer.clear();
        buffer.put(new byte[PAGE_SIZE]);
        buffer.clear();
    }

}
