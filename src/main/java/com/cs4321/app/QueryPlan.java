package com.cs4321.app;

/**
 * @author Jessica Tweneboah
 */
public class QueryPlan {
    private int childrenSize = 0;
    private QueryPlan leftChild;
    private QueryPlan rightChild;
    private Operator root;


    public QueryPlan(Operator oper) {
        setRoot(oper);
    }

    public QueryPlan(Operator oper, Operator leftChild) {
        this(oper);
        setLeftChild(leftChild);
    }

    public QueryPlan(Operator oper, Operator leftChild, Operator rightChild) {
        this(oper, leftChild);
        setRightChild(rightChild);
    }

    public void evaluate() {
    }

    public int getChildrenSize() {
        return childrenSize;
    }

    public void setChildrenSize(int childrenSize) {
        this.childrenSize = childrenSize;
    }

    public Operator getRoot() {
        return root;
    }

    public void setRoot(Operator root) {
        this.root = root;
    }

    public QueryPlan getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Operator leftChild) {
        this.leftChild = new QueryPlan(leftChild);
        this.childrenSize += 1;
    }

    public QueryPlan getRightChild() {
        return rightChild;
    }

    public void setRightChild(Operator rightChild) {
        this.rightChild = new QueryPlan(rightChild);
        this.childrenSize += 1;
    }
}
