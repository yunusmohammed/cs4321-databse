package com.cs4321.indexes;

import com.cs4321.app.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BPlusTreeSerializerTest {
    private static BPlusTreeSerializer bPlusTreeSerializer;
    private static final Logger logger = Logger.getInstance();
    FileInputStream fin;
    FileChannel fc;
    ByteBuffer buffer;
    Path fileToWrite;

    @BeforeEach
    void setUp() {
        try {
            fileToWrite = Files.createTempFile("temp", null);
            bPlusTreeSerializer = new BPlusTreeSerializer(fileToWrite.toString());
            this.fin = new FileInputStream(String.valueOf(fileToWrite));
            this.fc = fin.getChannel();
            int PAGE_SIZE = 4096;
            this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        } catch (IOException e) {
            logger.log(e.getMessage());
        }

    }

    @AfterEach
    void tearDown() throws IOException {
        fin.close();
        fc.close();
    }


    @Test
    void writeLeafNodeToPage() throws IOException {
        LeafNode leafNode = new LeafNode(9);
        DataEntry dataEntry = new DataEntry(5);
        dataEntry.addRid(new Rid(8, 282));
        leafNode.addDataEntry(dataEntry);

        bPlusTreeSerializer.writeLeafNodeToPage(leafNode);

        fc.read(buffer);
        buffer.clear();

        assertEquals(0, buffer.getInt());
        assertEquals(1, buffer.getInt());
        assertEquals(5, buffer.getInt());
        assertEquals(1, buffer.getInt());
        assertEquals(8, buffer.getInt());
        assertEquals(282, buffer.getInt());
    }

    @Test
    void writeIndexNodeToPage() throws IOException {
        IndexNode indexNode = new IndexNode(10);
        indexNode.addKey(1);
        indexNode.addKey(2);
        indexNode.addChild(new IndexNode(3));

        bPlusTreeSerializer.writeIndexNodeToPage(indexNode);

        fc.read(buffer);
        buffer.clear();

        assertEquals(1, buffer.getInt());
        assertEquals(2, buffer.getInt());
        assertEquals(1, buffer.getInt());
        assertEquals(2, buffer.getInt());
        assertEquals(3, buffer.getInt());
    }
}