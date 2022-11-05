package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.DatabaseCatalog;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.*;

/**
 * Visitor for determining which parts of an expression can be accessed via index
 *
 * @author Lenhard
 */
public class IndexSelectionVisitor implements ExpressionVisitor {

    // Database Catalogue
    private DatabaseCatalog dbc;

    // The column that can utilize an index
    private Column indexColumn;

    // The low/high value for the column that can utilize an index
    private List<Integer> lowHigh;

    // List of expressions that can't be evaluated with index
    private List<Expression> noIndexExpressionList;

    // Mapping from table name to base table name
    private AliasMap aliasMap;

    // Tracks whether the last seen column can be indexed
    private boolean canIndex;

    // Tracks the last value seen
    private int intLastValue;


    /**
     * Returns column that can be indexed.
     *
     * @return The column that can be indexed.
     */
    public Column getIndexColumn() {
        return indexColumn;
    }

    /**
     * Returns the low and high value of the index column.
     *
     * @return The low and high value of the index column.
     */
    public List<Integer> getLowHigh() {
        return lowHigh;
    }


    /**
     * Returns the expression that can't be evaluated with index. If no such expression exists, returns null.
     *
     * @return The index-less expression
     */
    public Expression getNoIndexExpression() {

        if (this.noIndexExpressionList.size() == 0) return null;
        Expression result = this.noIndexExpressionList.get(0);
        for (int i = 1; i < this.noIndexExpressionList.size(); i++) {
            result = new AndExpression(result, this.noIndexExpressionList.get(i));
        }
        return result;
    }

    /**
     * Splits the expression into those that benefit from a scan-index and those that don't.
     * <p>
     * The columns that can be indexed are can be retrived by `getIndexMapping` which is a mapping from the column to a
     * two element list that contains the lowerbound as the first element and the upperbound as the second element
     * (both inclusive). Expressions that can't be indexed can be retrieved by `getNoIndexExpression` after calling
     * this function.
     *
     * @param exp      The expression to split up into two parts.
     * @param aliasMap The aliasMap that keeps track of base table names.
     * @param dbc      The DatabaseCatalog that holds general information about the database.
     */
    public void splitExpression(Expression exp, AliasMap aliasMap, DatabaseCatalog dbc) {
        this.indexColumn = null;
        this.lowHigh = new ArrayList<>();
        this.noIndexExpressionList = new ArrayList<>();
        this.aliasMap = aliasMap;
        this.dbc = dbc;

        // Move columns to the left side of each binary expression (if possible)
        Stack<Expression> stack = new Stack<>();
        stack.push(exp);
        while (!stack.isEmpty()) {
            Expression e = stack.pop();
            if (e instanceof AndExpression) {
                stack.push(((AndExpression) e).getLeftExpression());
                stack.push(((AndExpression) e).getRightExpression());
            } else if (e instanceof BinaryExpression) {
                Expression left = ((BinaryExpression) e).getLeftExpression();
                Expression right = ((BinaryExpression) e).getRightExpression();
                if (left instanceof LongValue && right instanceof Column) {
                    ((BinaryExpression) e).setLeftExpression(right);
                    ((BinaryExpression) e).setRightExpression(left);
                }
            }
        }
        exp.accept(this);
    }

    public void checkCanIndex(Expression exp) {
        exp.accept(this);
    }

    @Override
    public void visit(AndExpression exp) {
        exp.getLeftExpression().accept(this);
        exp.getRightExpression().accept(this);
    }

    @Override
    public void visit(Column exp) {
        String[] wholeColumnName = exp.getWholeColumnName().split("\\.");
        String baseTableName = aliasMap.getBaseTable(wholeColumnName[0]);
        String columnName = wholeColumnName[1];
        HashMap<String, HashSet<String>> indexMap = dbc.getIndexColumns();
        boolean left = indexMap.containsKey(baseTableName);
        boolean right = (indexMap.get(baseTableName).contains(columnName));
        canIndex = left && right;
    }

    @Override
    public void visit(LongValue exp) {
        intLastValue = (int) exp.getValue();
    }

    @Override
    public void visit(EqualsTo exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();

        canIndex = false;
        checkCanIndex(left);
        checkCanIndex(right);

        if (canIndex && left instanceof Column && right instanceof LongValue) {
            List<Integer> lst = new ArrayList<>();
            lst.add(intLastValue);
            lst.add(intLastValue);
            lowHigh = lst;
            indexColumn = (Column) left;
        } else {
            this.noIndexExpressionList.add(exp);
        }

    }

    @Override
    public void visit(NotEqualsTo exp) {
        this.noIndexExpressionList.add(exp);
    }

    @Override
    public void visit(GreaterThan exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();

        canIndex = false;
        checkCanIndex(left);
        checkCanIndex(right);

        if (canIndex && left instanceof Column && right instanceof LongValue) {
            // Update if column already in mapping; otherwise add a new value
            if (indexColumn != null) {
                // Potentially update lower bound and increase by one because index-scan is inclusive equality
                lowHigh.set(0, Math.max(lowHigh.get(0), intLastValue + 1));
            } else {
                List<Integer> lst = new ArrayList<>();
                lst.add(intLastValue + 1);
                lst.add(Integer.MAX_VALUE);
                lowHigh = lst;
                indexColumn = (Column) left;
            }
        } else {
            this.noIndexExpressionList.add(exp);
        }
    }

    @Override
    public void visit(GreaterThanEquals exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();

        canIndex = false;
        checkCanIndex(left);
        checkCanIndex(right);

        if (canIndex && left instanceof Column && right instanceof LongValue) {
            // Update if column already in mapping; otherwise add a new value
            if (indexColumn != null) {
                // Potentially update lower bound
                lowHigh.set(0, Math.max(lowHigh.get(0), intLastValue));
            } else {
                List<Integer> lst = new ArrayList<>();
                lst.add(intLastValue);
                lst.add(Integer.MAX_VALUE);
                lowHigh = lst;
                indexColumn = (Column) left;
            }
        } else {
            this.noIndexExpressionList.add(exp);
        }
    }

    @Override
    public void visit(MinorThan exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();

        canIndex = false;
        checkCanIndex(left);
        checkCanIndex(right);

        if (canIndex && left instanceof Column && right instanceof LongValue) {
            // Update if column already in mapping; otherwise add a new value
            if (indexColumn != null) {
                // Potentially update upper bound and decrease by one because index-scan is inclusive equality
                lowHigh.set(1, Math.min(lowHigh.get(1), intLastValue - 1));
            } else {
                List<Integer> lst = new ArrayList<>();
                lst.add(Integer.MIN_VALUE);
                lst.add(intLastValue - 1);
                lowHigh = lst;
                indexColumn = (Column) left;
            }
        } else {
            this.noIndexExpressionList.add(exp);
        }
    }

    @Override
    public void visit(MinorThanEquals exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();

        canIndex = false;
        checkCanIndex(left);
        checkCanIndex(right);

        if (canIndex && left instanceof Column && right instanceof LongValue) {
            // Update if column already in mapping; otherwise add a new value
            if (indexColumn != null) {
                // Potentially update upper bound and decrease by one because index-scan is inclusive equality
                lowHigh.set(1, Math.min(lowHigh.get(1), intLastValue));
            } else {
                List<Integer> lst = new ArrayList<>();
                lst.add(Integer.MIN_VALUE);
                lst.add(intLastValue);
                lowHigh = lst;
                indexColumn = (Column) left;
            }
        } else {
            this.noIndexExpressionList.add(exp);
        }
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
