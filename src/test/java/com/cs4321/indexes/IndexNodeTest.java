package com.cs4321.indexes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexNodeTest {
    IndexNode indexNode = new IndexNode(10);

    @BeforeEach
    void setUp() {
        LeafNode leafNode = new LeafNode(0);
        indexNode.addKey(100);
        indexNode.addKey(101);
        indexNode.addChild(leafNode);
    }

    @Test
    void getNumberOfKeys() {
        assertEquals(2, indexNode.getNumberOfKeys());
    }

    @Test
    void getKeysList() {
        assertEquals(List.of(100, 101), indexNode.getKeysList());
    }

    @Test
    void addKey() {
        assertEquals(2, indexNode.getKeysList().size());
    }

    @Test
    void addChild() {
        assertEquals(1, indexNode.getChildren().size());
    }

    @Test
    void getChildren() {
        IndexNode indexNode = new IndexNode(10);
        LeafNode leafNode1 = new LeafNode(0);
        LeafNode leafNode2 = new LeafNode(0);
        List<Node> children = List.of(leafNode1, leafNode2);
        indexNode.addChildren(children);
        assertEquals(children, indexNode.getChildren());
    }
}