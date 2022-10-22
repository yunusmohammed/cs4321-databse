package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.List;

public class SMJOperator extends JoinOperator {

    /**
     * The left sort operator of the SMJ Operator
     */
    private final SortOperator leftSort;

    /**
     * The right sort operator of this SMJ Operator
     */
    private final SortOperator rightSort;

    private Tuple leftTup;
    private List<OrderByElement> leftSortOrder;
    private List<OrderByElement> rightSortOrder;


    /**
     * Constructor for JoinOperator
     *
     * @param leftSort      the left sort operator of this join operator
     * @param rightSort     the right child operator of this join operator
     * @param joinCondition the condition to join rows on
     * @param visitor       the expression visitor of this join operator
     */
    public SMJOperator(SortOperator leftSort, SortOperator rightSort, Expression joinCondition,
                       JoinExpressionVisitor visitor) {
        super(leftSort, rightSort, joinCondition, visitor);
        this.leftSort = (SortOperator) this.getLeftChild();
        this.rightSort = (SortOperator) this.getRightChild();
    }

    public void setLeftSortOrder(List<OrderByElement> leftSortOrder) {
        this.leftSortOrder = leftSortOrder;
    }

    public void setRightSortOrder(List<OrderByElement> rightSortOrder) {
        this.rightSortOrder = rightSortOrder;
    }

    /**
     * Returns -1 if leftTup is smaller than rightTup, 0 if they're the same, and 1 if leftTup is larger than rightTup.
     *
     * @param leftTup    The left tuple to compare
     * @param rightTup   The right tuple to compare
     * @param leftOrder  The order of elements for the left tuple
     * @param rightOrder The order of elements for the right tuple
     * @return
     */
    private int compare(Tuple leftTup, Tuple rightTup, List<OrderByElement> leftOrder, List<OrderByElement> rightOrder) {
        int leftValue;
        int rightValue;
        int index;
        for (int i = 0; i < leftOrder.size(); i++) {
            // Compute value of left tuple
            Column leftColumn = (Column) leftOrder.get(i).getExpression();
            index = leftSort.getColumnMap().get(leftColumn.getWholeColumnName());
            leftValue = leftTup.get(index);

            // Compute value of right tuple
            Column rightColumn = (Column) rightOrder.get(i).getExpression();
            index = rightSort.getColumnMap().get(rightColumn.getWholeColumnName());
            rightValue = rightTup.get(index);
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
        if (leftTup == null) leftTup = this.leftSort.getNextTuple();
        while (leftTup != null) {
            Tuple rightTup = this.rightSort.getNextTuple();
            if (rightTup == null) {
                rightSort.reset();
                leftTup = this.leftSort.getNextTuple();
            } else {
                int comp = compare(leftTup, rightTup, leftSortOrder, rightSortOrder);
                if (comp == -1) {
                    rightSort.reset();
                    leftTup = this.leftSort.getNextTuple();
                } else if (comp == 0) {
                    return leftTup.concat(rightTup);
                }
            }
        }
        return null;
    }
}
