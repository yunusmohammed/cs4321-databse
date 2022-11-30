package com.cs4321.physicaloperators;

import java.util.ArrayList;
import java.util.Iterator;

import com.cs4321.app.Tuple;

import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for handling Joins using Block Nested Loop Join
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class BNLJoinOperator extends JoinOperator {

  private ArrayList<Tuple> tupleBuffer = new ArrayList<>();

  private Iterator<Tuple> tupleBufferIterator = this.tupleBuffer.iterator();

  private Tuple rightTuple;

  private final int SIZE_OF_TUPLE_ATTRIBUTE = 4;

  private final int SIZE_OF_A_PAGE = 4096;

  // Maximum number tuples that can be held in tupleBuffer at any time
  private int tupleBufferSize;

  // /**
  // * Base constructor of the JoinOperator
  // */
  // public BNLJoinOperator() {
  // super();
  // }

  /**
   * Constructor for BNLJoinOperator
   *
   * @param leftChild     the left child operator of this join operator
   * @param rightChild    the right child operator of this join operator
   * @param joinCondition the condition to join rows on
   * @param visitor       the expression visitor of this join operator
   * @param bufferSize    the maximum size of buffer in units of pages
   */
  public BNLJoinOperator(Operator leftChild, Operator rightChild, Expression joinCondition,
      JoinExpressionVisitor visitor, int bufferSize) {
    super(leftChild, rightChild, joinCondition, visitor);

    Tuple leftTuple = leftChild.getNextTuple();
    int leftChildTupleAttributeCount = (leftTuple == null) ? 0 : leftTuple.size();
    leftChild.reset();
    this.tupleBufferSize = (leftChildTupleAttributeCount == 0) ? 0
        : (bufferSize * SIZE_OF_A_PAGE) / (leftChildTupleAttributeCount * SIZE_OF_TUPLE_ATTRIBUTE);
  }

  @Override
  public Tuple getNextTuple() {
    if (this.tupleBuffer.size() == 0) {
      this.refillTupleBuffer();
    }

    while (this.tupleBuffer.size() != 0) {
      if (this.rightTuple == null) {
        this.rightTuple = this.getRightChild().getNextTuple();
      }
      while (this.rightTuple != null) {
        if (!this.tupleBufferIterator.hasNext()) {
          this.rightTuple = this.getRightChild().getNextTuple();
          this.resetTupleBufferIterator();
        } else {
          Tuple leftTuple = this.tupleBufferIterator.next();
          if (this.getJoinCondition() == null
              || this.getVisitor().evalExpression(this.getJoinCondition(), leftTuple, this.rightTuple)) {
            return leftTuple.concat(this.rightTuple);
          }
        }
      }
      this.refillTupleBuffer();
      this.getRightChild().reset();
    }
    return null;
  }

  /**
   * Loads the next tupleBufferSize tuples from the leftChild into the
   * tupleBuffer. Loads less that tupleBufferSize into tupleBuffer if we have less
   * than tupleBufferSize tuples left in leftChild. Resets tuple buffer iterator.
   */
  private void refillTupleBuffer() {
    this.tupleBuffer = new ArrayList<>();
    int i = 0;

    Tuple nextTuple;
    while (i < tupleBufferSize && (nextTuple = this.getLeftChild().getNextTuple()) != null) {
      this.tupleBuffer.add(nextTuple);
      i += 1;
    }
    this.resetTupleBufferIterator();
  }

  /**
   * Reset the tuple buffer iterator
   */
  private void resetTupleBufferIterator() {
    this.tupleBufferIterator = this.tupleBuffer.iterator();
  }

  /**
   * Gets the maximum number of tuples held in the buffer of the BNLJ operator at
   * a time
   * 
   * @return The maximum number of tuples held in the buffer of the BNLJ operator
   *         at a time
   */
  public int getTupleBufferSize() {
    return this.tupleBufferSize;
  }

  /**
   * Sets the maximum number of tuples held in the buffer of the BNLJ operator at
   * a time
   * 
   * @param tupleBufferSize The maximum number of tuples held in the buffer of the
   *                        BNLJ operator at a time
   */
  public void setTupleBufferSize(int tupleBufferSize) {
    this.tupleBufferSize = tupleBufferSize;
  }

  @Override
  public void reset() {
    super.reset();
    this.tupleBuffer = new ArrayList<>();
    this.tupleBufferIterator = this.tupleBuffer.iterator();
    this.rightTuple = null;
  }

  @Override
  public String toString() {
    String joinConditionString = (this.getJoinCondition() == null) ? "null" : this.getJoinCondition().toString();
    return "BNLJoinOperator{" + this.getLeftChild().toString() + ", " + this.getRightChild().toString() + ", "
        + joinConditionString + "}";
  }

  @Override
  public String toString(int level) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < level; i++) {
      builder.append("-");
    }
    builder.append("BNLJ[" + this.getJoinCondition().toString() + "]");
    builder.append("\n");
    builder.append(this.getLeftChild().toString(level + 1));
    builder.append(this.getRightChild().toString(level + 1));
    return builder.toString();
  }
}
