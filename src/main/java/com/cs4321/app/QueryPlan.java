package com.cs4321.app;

/**
 * The QueryPlan is a tree of operators.  A QueryPlan is constructed for each Statement
 * and returned to the interpreter, so it can read the results of the QueryPlan
 *
 * @author Jessica Tweneboah
 */
public class QueryPlan {
    private QueryPlan leftChild;
    private QueryPlan rightChild;
    private Operator root;


    /**
     * Constructor that initialises a QueryPlan
     *
     * @param root The root operator of the QueryPlan
     */
    public QueryPlan(Operator root) {
        setRoot(root);
    }

    /**
     * Constructor that initialises a QueryPlan
     *
     * @param root      The root operator of the QueryPlan
     * @param leftChild The left Child of root.
     *                  Operators with just one child, eg. Selector, Project, have the leftChild field populated
     */
    public QueryPlan(Operator root, Operator leftChild) {
        this(root);
        setLeftChild(leftChild);
    }

    /**
     * Constructor that initialises a QueryPlan
     *
     * @param root       The root operator of the QueryPlan
     * @param leftChild  The left Child of root.
     *                   Operators with just one child, eg. Selector, Project, have the leftChild field populated
     * @param rightChild The right Child of root.
     *                   Operators with just two children, eg. Join, have the rightChild field populated
     */
    public QueryPlan(Operator root, Operator leftChild, Operator rightChild) {
        this(root, leftChild);
        setRightChild(rightChild);
    }

    /**
     * Evaluates the result of the QueryPlan
     */
    public void evaluate() {
    }

    /**
     * Returns the root of this QueryPlan
     *
     * @return The root operator of the QueryPlan
     */
    public Operator getRoot() {
        return root;
    }

    /**
     * Populates the root field
     *
     * @param root The root operator of the QueryPlan
     */
    public void setRoot(Operator root) {
        this.root = root;
    }

    /**
     * Returns the left child of this QueryPlan
     *
     * @return The left Child of the root of the QueryPlan
     */
    public QueryPlan getLeftChild() {
        return leftChild;
    }

    /**
     * Populates the leftChild field of this QueryPlan
     *
     * @param leftChild The left Child of the root of the QueryPlan
     */
    public void setLeftChild(Operator leftChild) {
        this.leftChild = new QueryPlan(leftChild);
    }

    /**
     * Returns the right child of this QueryPlan
     *
     * @return The right Child of the root of the QueryPlan
     */
    public QueryPlan getRightChild() {
        return rightChild;
    }

    /**
     * Populates the rightChild field of this QueryPlan
     *
     * @param rightChild The right Child of the root of the QueryPlan
     */
    public void setRightChild(Operator rightChild) {
        this.rightChild = new QueryPlan(rightChild);
    }
}
