package com.cs4321.app;

import java.util.Stack;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;

/**
 * Container class for holding distributions of expressions among a JoinOperator
 * and its children
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class JoinExpressions {
    private Stack<Expression> rightChildExpressions;
    private Stack<Expression> parentExpressions;
    private Stack<BinaryExpression> leftChildExpressions;

    /**
     * Constructor of JoinExpressions
     * 
     * @param parentExpressions     a stack expressions belonging to the parent
     *                              JoinOperator
     * @param rightChildExpressions a stack expressions belonging to the right child
     *                              of the parent
     *                              JoinOperator
     * @param leftChildExpressions  a stack expressions belonging to the left child
     *                              of the parent
     *                              JoinOperator
     */
    public JoinExpressions(Stack<Expression> parentExpressions, Stack<Expression> rightChildExpressions,
            Stack<BinaryExpression> leftChildExpressions) {
        this.parentExpressions = parentExpressions;
        this.rightChildExpressions = rightChildExpressions;
        this.leftChildExpressions = leftChildExpressions;
    }

    /**
     * Gets the expressions belonging to the parent JoinOperator
     * 
     * @return a stack expressions belonging to the parent JoinOperator
     */
    public Stack<Expression> getParentExpressions() {
        return this.parentExpressions;
    }

    /**
     * Gets the expressions belonging to the left child of the parent JoinOperator
     * 
     * @return a stack expressions belonging to the left child of the parent
     *         JoinOperator
     */
    public Stack<BinaryExpression> getLeftExpressions() {
        return this.leftChildExpressions;
    }

    /**
     * Gets the expressions belonging to the right child of the parent JoinOperator
     * 
     * @return a stack expressions belonging to the right child of the parent
     *         JoinOperator
     */
    public Stack<Expression> getRightChildExpressions() {
        return this.rightChildExpressions;
    }

}
