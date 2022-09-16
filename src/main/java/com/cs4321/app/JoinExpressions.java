package com.cs4321.app;

import java.util.Stack;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;

public class JoinExpressions {
    private Stack<Expression> rightChildExpressions;
    private Stack<Expression> parentExpressions;
    private Stack<BinaryExpression> leftChildExpressions;

    public JoinExpressions(Stack<Expression> parentExpressions, Stack<Expression> rightChildExpressions,
            Stack<BinaryExpression> leftChildExpressions) {
        this.parentExpressions = parentExpressions;
        this.rightChildExpressions = rightChildExpressions;
        this.leftChildExpressions = leftChildExpressions;
    }

    public Stack<Expression> getParentExpressions() {
        return this.parentExpressions;
    }

    public Stack<BinaryExpression> getLeftExpressions() {
        return this.leftChildExpressions;
    }

    public Stack<Expression> getRightChildExpressions() {
        return this.rightChildExpressions;
    }

}
