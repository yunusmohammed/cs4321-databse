package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.Map;

/**
 * ExpressionVisitor for the Join Operator
 *
 * @author Yunus (ymm26@cornell.edu)
 */
public class JoinExpressionVisitor implements ExpressionVisitor {

    /**
     * Map of tableName, offset value pairs to be used to correctly index tuples for
     * table columns
     */
    final private AliasMap aliasMap;

    /**
     * Map of tableName, offset value pairs to be used to correctly index tuples for
     * table columns
     */
    final private Map<String, Integer> tableOffset;

    /**
     * Name of the table referenced by the right child of the Join Operator
     */
    final private String rightTableName;

    /**
     * Latest double value after expression evaluation
     */
    private double doubleLastValue;

    /**
     * Latest boolean value after expression evaluation
     */
    private boolean boolLastValue;

    /**
     * Current tuple from evaluating the left child of the parent Join Operator
     */
    private Tuple leftRow;

    /**
     * Current tuple from evaluating the right child of the parent Join Operator
     */
    private Tuple rightRow;

    /**
     * Initializes the JoinExpressionVisitor
     *
     * @param dbCatalog   The database catalog
     * @param tableOffset A map of offsets to correctly index tuples for table
     *                    columns
     * @param rightTable  The name of the table referenced by the right child of the
     *                    Join Operator
     */
    public JoinExpressionVisitor(AliasMap aliasMap, Map<String, Integer> tableOffset, String rightTable) {
        this.tableOffset = tableOffset;
        this.rightTableName = rightTable;
        this.aliasMap = aliasMap;
    }

    /**
     * Evaluates the expression exp
     *
     * @param exp      The expression to be evaluated, a boolean expression
     * @param leftRow  The row corresponding to the left expression of exp
     * @param rightRow The row corresponding to the right expression of exp
     * @return The result of evaluating exp
     */
    public boolean evalExpression(Expression exp, Tuple leftRow, Tuple rightRow) {
        this.leftRow = leftRow;
        this.rightRow = rightRow;
        exp.accept(this);
        return boolLastValue;
    }

    /**
     * Gets a map of offsets to be applied to column indices of table columns in
     * order to correctly index columns in joined rows
     *
     * @return the table column index offsets
     */
    public Map<String, Integer> getTableOffsets() {
        return this.tableOffset;
    }

    /**
     * Return the Alias Map for this visitor
     *
     * @return the alias map for this visitor
     */
    public AliasMap getAliasMap() {
        return aliasMap;
    }

    /**
     * *
     *
     * @param exp The expression to be evaluated, a boolean expression
     * @return The result of evaluating exp
     */
    private boolean computeBool(Expression exp) {
        exp.accept(this);
        return boolLastValue;
    }

    /**
     * Evaluates the expression exp
     *
     * @param exp The expression to be evaluated, a double expression
     * @return The result of evaluating exp
     */
    private double computeDouble(Expression exp) {
        exp.accept(this);
        return doubleLastValue;
    }

    @Override
    public void visit(AndExpression exp) {
        boolean left = computeBool(exp.getLeftExpression());
        boolean right = computeBool(exp.getRightExpression());
        boolLastValue = left && right;
    }

    @Override
    public void visit(Column exp) {
        String tableName = exp.getTable().getName();
        Tuple row = (tableName.equals(this.rightTableName)) ? this.rightRow : this.leftRow;
        int offSet = (tableName.equals(this.rightTableName)) ? 0 : tableOffset.get(tableName);
        int index = offSet + this.aliasMap.get(exp);
        this.doubleLastValue = row.get(index);
    }

    @Override
    public void visit(LongValue exp) {
        this.doubleLastValue = exp.getValue();
    }

    @Override
    public void visit(EqualsTo exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        this.boolLastValue = left == right;
    }

    @Override
    public void visit(NotEqualsTo exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        this.boolLastValue = left != right;
    }

    @Override
    public void visit(GreaterThan exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        this.boolLastValue = left > right;
    }

    @Override
    public void visit(GreaterThanEquals exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        this.boolLastValue = left >= right;
    }

    @Override
    public void visit(MinorThan exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        this.boolLastValue = left < right;
    }

    @Override
    public void visit(MinorThanEquals exp) {
        double left = computeDouble(exp.getLeftExpression());
        double right = computeDouble(exp.getRightExpression());
        this.boolLastValue = left <= right;
    }

    @Override
    public void visit(NullValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Function arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(InverseExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(JdbcParameter arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DoubleValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(DateValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimeValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TimestampValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Parenthesis arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(StringValue arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Addition arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Division arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Multiplication arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Subtraction arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(OrExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Between arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(InExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(IsNullExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LikeExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SubSelect arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(CaseExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(WhenClause arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ExistsExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AllComparisonExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AnyComparisonExpression arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Concat arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Matches arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseAnd arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseOr arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(BitwiseXor arg0) {
        // TODO Auto-generated method stub

    }

}
