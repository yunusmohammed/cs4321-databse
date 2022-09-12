package com.cs4321.app;
import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for handling Joins
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class JoinOperator extends Operator {

    /**
     * The left child of the Operator
     */
    private final Operator leftChild;

    /**
     * The right child of the Operator
     */
    private final Operator rightChild;

    /**
     * The join condition
     */
    private final Expression expression;

    /**
     * ExpressionVisitor for the JoinOperator
     */
    private final JoinExpressionVisitor visitor;

    /**
     * The current tuple of the left child the Operator is on
     */
    private Tuple leftTuple;

    /**
     * Concatenates two tuples into one
     * @param leftTuple The tuple on the left hand of the concatenation
     * @param rightTuple The tuple on the right hand of the concatenation
     * @return A tuple made by extending leftTuple with right
     */
    private Tuple joinTuples(Tuple leftTuple, Tuple rightTuple) {
        StringBuilder sb = new StringBuilder();
        sb.append(leftTuple.toString());
        sb.append(",");
        sb.append(rightTuple.toString());
        return new Tuple(sb.toString());
    }

    public JoinOperator(JoinExpressionVisitor visitor, Operator leftChild, Operator rightChild, Expression expression) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.expression = expression;
        this.visitor = visitor;
    }

    @Override
    public Tuple getNextTuple() {
        // TODO Auto-generated method stub
        if (this.leftTuple == null) this.leftChild.getNextTuple();
        while (this.leftTuple != null) {
            Tuple rightTuple = this.rightChild.getNextTuple();
            if (rightTuple == null) this.leftTuple =  this.leftChild.getNextTuple();
            else if (this.visitor.evalExpression(this.expression, leftTuple, rightTuple)) return joinTuples(this.leftTuple, rightTuple);
        }
        return null;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        this.leftChild.reset();
        this.rightChild.reset();
        this.leftTuple = null;
    }

    @Override
    public void finalize() {
        this.leftChild.finalize();
        this.rightChild.finalize();
    }

}
