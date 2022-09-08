package com.cs4321.app;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.Map;

public class SelectExpressionVisitor implements ExpressionVisitor {

    private Map<String, Integer> columnMap;
    private Tuple row;

    private double doubleLastValue;
    private boolean boolLastValue;


    /**
     * Evaluates an expression to either true or false.
     *
     * @param exp       The expression being evaluated. Requires: exp to be of the form [A op B] where A and B are either
     *                  longs or column references, and op is one of =, !=, <, <=, >, or >= or a conjunction of other
     *                  expressions in that form.
     * @param columnMap The mapping from a table's column name to the index that column represents in a row
     * @param row       The row for which the expression is being evaluated on.
     * @return Boolean result of evaluating the expression.
     */
    public boolean evalExpression(Expression exp, Tuple row, Map<String, Integer> columnMap) {
        this.row = row;
        this.columnMap = columnMap;
        exp.accept(this);
        return boolLastValue;
    }

    /**
     * Helper method for evaluating an expression to a double.
     *
     * @param exp The expression being evaluated. Requires: exp to be either a LongValue or Column.
     * @return Double result of evaluating the expression.
     */
    private double computeDouble(Expression exp) {
        exp.accept(this);
        return doubleLastValue;
    }

    @Override
    public void visit(AndExpression exp) {
        boolean left = evalExpression(exp.getLeftExpression(), this.row, this.columnMap);
        boolean right = evalExpression(exp.getRightExpression(), this.row, this.columnMap);
        boolLastValue = left && right;
    }

    @Override
    public void visit(Column exp) {
        int index = columnMap.get(exp.getColumnName());
        doubleLastValue = row.get(index);
    }

    @Override
    public void visit(LongValue exp) {
        doubleLastValue = exp.getValue();
    }

    @Override
    public void visit(EqualsTo exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        boolLastValue = left == right;
    }

    @Override
    public void visit(NotEqualsTo exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        boolLastValue = left != right;
    }

    @Override
    public void visit(GreaterThan exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        boolLastValue = left > right;
    }

    @Override
    public void visit(GreaterThanEquals exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        boolLastValue = left >= right;
    }

    @Override
    public void visit(MinorThan exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        boolLastValue = left < right;
    }

    @Override
    public void visit(MinorThanEquals exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        boolLastValue = left <= right;
    }

    @Override
    public void visit(NullValue exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Function exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(InverseExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JdbcParameter exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DoubleValue exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DateValue exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimeValue exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimestampValue exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Parenthesis exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(StringValue exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Addition exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Division exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Multiplication exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Subtraction exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(OrExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Between exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(InExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IsNullExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LikeExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SubSelect exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CaseExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(WhenClause exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ExistsExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AllComparisonExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AnyComparisonExpression exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Concat exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Matches exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseAnd exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseOr exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseXor exp) {
        // TODO Auto-generated method stub

    }

}
