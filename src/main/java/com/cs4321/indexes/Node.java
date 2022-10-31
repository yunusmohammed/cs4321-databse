package com.cs4321.indexes;

public abstract class Node {
    int nodeFlag;

    Node(int nodeFlag) {
        this.nodeFlag = nodeFlag;
    }

    public int getNodeFlag() {
        return nodeFlag;
    }
}
