package com.cs4321.indexes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TreeSerializer {
    private FileOutputStream fout;
    private FileChannel fc;
    private ByteBuffer buffer;
    private final String filename;
    private final int PAGE_SIZE = 4096;

    public TreeSerializer(String filename) throws FileNotFoundException {
        this.fout = new FileOutputStream(filename);
        this.fc = fout.getChannel();
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        this.filename = filename;
    }

    public void writeLeafNodeToPage(LeafNode leafNode) {
        buffer.putInt(leafNode.getNodeFlag());
        buffer.putInt(leafNode.getNumberOfDataEntires());
        for (LeafNode.DataEntry dataEntry : leafNode.getDataEntries()) {
            buffer.putInt(dataEntry.getKey());
            buffer.putInt(dataEntry.getNumberOfRids());
            for (LeafNode.Rid rid: dataEntry.getRids()) {
                buffer.putInt(rid.pageId);
                buffer.putInt(rid.tupleId);
            }
        }
    }

}
