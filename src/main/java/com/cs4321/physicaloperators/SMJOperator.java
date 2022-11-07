package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.List;

/**
 * Operator for handling Joins using Sort-Merge-Join algorithm.
 *
 * @author Lenhard Thomas
 */
public class SMJOperator extends JoinOperator {

    /**
     * The left sort operator of the SMJ Operator.
     */
    private final Operator leftSort;

    /**
     * The right sort operator of this SMJ Operator.
     */
    private final Operator rightSort;

    /**
     * The tuple corresponding to the left sort operator
     */
    private Tuple leftTuple;

    /**
     * The tuple corresponding to the right sort operator.
     */
    private Tuple rightTuple;

    /**
     * The current index of the inner operator.
     */
    private int index;

    /**
     * The index of the S-partition of the inner operator.
     */
    private int rightIndex;

    /**
     * Tracker to determine whether getNextTuple has already been called.
     */
    private boolean started;

    /**
     * The sort order corresponding to the left Sort operator.
     */
    private List<OrderByElement> leftSortOrder;

    /**
     * The sort order corresponding to the right Sort operator.
     */
    private List<OrderByElement> rightSortOrder;


    /**
     * Constructor for JoinOperator
     *
     * @param leftSort      the left sort operator of this join operator
     * @param rightSort     the right child operator of this join operator
     * @param joinCondition the condition to join rows on
     * @param visitor       the expression visitor of this join operator
     */
    public SMJOperator(Operator leftSort, Operator rightSort, Expression joinCondition,
                       JoinExpressionVisitor visitor) {
        super(leftSort, rightSort, joinCondition, visitor);
        this.leftSort = this.getLeftChild();
        this.rightSort = this.getRightChild();
        this.started = false;
        this.index = 0;
        this.rightIndex = 0;
    }

    /**
     * Sets the left sort order for this SMJ operator.
     *
     * @param leftSortOrder The sort order corresponding to the left sort operator.
     */
    public void setLeftSortOrder(List<OrderByElement> leftSortOrder) {
        this.leftSortOrder = leftSortOrder;
    }

    /**
     * Sets the right sort order for this SMJ operator.
     *
     * @param rightSortOrder The sort order corresponding to the right sort operator.
     */
    public void setRightSortOrder(List<OrderByElement> rightSortOrder) {
        this.rightSortOrder = rightSortOrder;
    }

    /**
     * Returns -1 if leftTuple is smaller than rightTuple, 0 if they're the same, and 1 if leftTuple is larger than rightTuple.
     * Comparison is done as a linear scan of the value of each tuple utilizing their corresponding sort order.
     *
     * @param leftTuple  The left tuple to compare
     * @param rightTuple The right tuple to compare
     * @param leftOrder  The order of elements for the left tuple
     * @param rightOrder The order of elements for the right tuple
     * @return -1 if leftTuple is smaller, 0 if leftTuple and rightTuple the same, and 1 if leftTuple is larger
     */
    private int compare(Tuple leftTuple, Tuple rightTuple, Operator leftOperator, Operator rightOperator, List<OrderByElement> leftOrder, List<OrderByElement> rightOrder) {
        int leftValue;
        int rightValue;
        int index;
        for (int i = 0; i < leftOrder.size(); i++) {
            // Compute value of left tuple
            Column leftColumn = (Column) leftOrder.get(i).getExpression();
            index = leftOperator.getColumnMap().get(leftColumn.getWholeColumnName());
            leftValue = leftTuple.get(index);

            // Compute value of right tuple
            Column rightColumn = (Column) rightOrder.get(i).getExpression();
            index = rightOperator.getColumnMap().get(rightColumn.getWholeColumnName());
            rightValue = rightTuple.get(index);
            if (leftValue < rightValue) {
                return -1;
            } else if (leftValue > rightValue) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    Tuple getNextTuple() {
        // Initialize values on initial call to getNextTuple
        if (!started) {
            started = true;
            leftTuple = this.leftSort.getNextTuple();
        }
        rightTuple = this.rightSort.getNextTuple();
        // Continue searching while an unscanned outer tuple exists and the two tuples are not equal (or the inner row finished scanning)
        while (leftTuple != null && (rightTuple == null || compare(leftTuple, rightTuple, leftSort, rightSort, leftSortOrder, rightSortOrder) != 0)) {
            // Case where inner row is done scanning or the inner tuple is larger
            if (rightTuple == null || compare(leftTuple, rightTuple, leftSort, rightSort, leftSortOrder, rightSortOrder) == -1) {
                Tuple nextLeftTuple = leftSort.getNextTuple();
                // Reset the inner row to the S-partition if next outer tuple matches current outer tuple
                if (nextLeftTuple != null && compare(leftTuple, nextLeftTuple, leftSort, leftSort, leftSortOrder, leftSortOrder) == 0) {
                    rightSort.reset(rightIndex);
                    rightTuple = rightSort.getNextTuple();
                } else {
                    rightIndex = index;
                }
                index = rightIndex;
                leftTuple = nextLeftTuple;
            }
            // Case where inner tuple is smaller
            else {
                rightTuple = rightSort.getNextTuple();
                index += 1;
            }
        }

        if (leftTuple == null || rightTuple == null) return null;

        return leftTuple.concat(rightTuple);
    }
}
