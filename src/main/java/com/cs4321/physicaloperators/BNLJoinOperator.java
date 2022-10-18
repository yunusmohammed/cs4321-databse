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
  private final int tupleBufferSize;

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
    int leftChildTupleAttributeCount = leftChild.getNextTuple().size();
    leftChild.reset();

    this.tupleBufferSize = (bufferSize * SIZE_OF_A_PAGE) / (leftChildTupleAttributeCount * SIZE_OF_TUPLE_ATTRIBUTE);
  }

  @Override
  public Tuple getNextTuple() {
    if (this.tupleBuffer.size() == 0) {
      this.refillTupleBuffer();
      this.resetTupleBufferIterator();
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
      this.resetTupleBufferIterator();
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
    }
    this.resetTupleBufferIterator();
  }

  /**
   * Reset the tuple buffer iterator
   */
  private void resetTupleBufferIterator() {
    this.tupleBufferIterator = this.tupleBuffer.iterator();
  }

  @Override
  public void reset() {
    super.reset();
    this.tupleBuffer = new ArrayList<>();
    this.tupleBufferIterator = this.tupleBuffer.iterator();
  }

  @Override
  public String toString() {
    String joinConditionString = (this.getJoinCondition() == null) ? "null" : this.getJoinCondition().toString();
    return "BNLJoinOperator{" + this.getLeftChild().toString() + ", " + this.getRightChild().toString() + ", "
        + joinConditionString + "}";
  }
}