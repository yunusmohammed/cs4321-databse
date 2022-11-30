package com.cs4321.app;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSUExpressionVisitor implements ExpressionVisitor {

    private UnionFind unionFind;
    private List<Expression> unusable;
    private Map<String, Expression> loneExpressions;

    /**
     * Processes the expression `exp` by using a union find data structure such that equality constraints are grouped
     * together under one element.
     *
     * @param exp The expression to parse using union find.
     */
    public void processExpression(Expression exp) {
        unionFind = new UnionFind();
        this.unusable = new ArrayList<>();
        this.loneExpressions = new HashMap<>();
        exp.accept(this);
    }

    /**
     * Gets the union find data structure that's used for parsing expression.
     *
     * @return The union find data structure.
     */
    public UnionFind getUnionFind() {
        return unionFind;
    }

    /**
     * The expressions that couldn't fit into the union find data structure.
     *
     * @return The unusable parts of the parsed expression.
     */
    public Expression getUnusable() {
        if (this.unusable.size() == 0) return null;
        Expression result = this.unusable.get(0);
        for (int i = 1; i < this.unusable.size(); i++) {
            result = new AndExpression(result, this.unusable.get(i));
        }
        return result;
    }

    private void updateMap(String tableName, Map<String, Expression> map, Expression exp) {
        if (map.containsKey(tableName)) {
            Expression oldExp = map.get(tableName);
            AndExpression newExp = new AndExpression();
            newExp.setLeftExpression(oldExp);
            newExp.setRightExpression(exp);

            map.put(tableName, newExp);
        } else {
            map.put(tableName, exp);
        }
    }

    private void valueConstraints(UnionFindElement element, Map<String, Expression> map) {
        for (Column attribute : element.getAttributes()) {
            String tableName = attribute.getWholeColumnName().split("\\.")[0];
            if (element.getEqualityConstraint() == null) {
                if (element.getLowerBound() != null) {
                    GreaterThanEquals greaterEqualExp = new GreaterThanEquals();
                    LongValue longExp = new LongValue(element.getLowerBound());
                    greaterEqualExp.setLeftExpression(attribute);
                    greaterEqualExp.setRightExpression(longExp);
                    updateMap(tableName, map, greaterEqualExp);
                }
                if (element.getUpperBound() != null) {
                    MinorThanEquals lessEqualExp = new MinorThanEquals();
                    LongValue longExp = new LongValue(element.getUpperBound());
                    lessEqualExp.setLeftExpression(attribute);
                    lessEqualExp.setRightExpression(longExp);
                    updateMap(tableName, map, lessEqualExp);
                }
            } else {
                EqualsTo eqExp = new EqualsTo();
                LongValue longExp = new LongValue(element.getEqualityConstraint());
                eqExp.setLeftExpression(attribute);
                eqExp.setRightExpression(longExp);
                updateMap(tableName, map, eqExp);
            }
        }
    }

    public void noValueConstraints(UnionFindElement element, Map<String, Expression> selectionExpressions) {
        List<Column> attributes = element.getAttributes();
        for (int i = 0; i < attributes.size() - 1; i++) {
            Column column = attributes.get(i);
            String tableName = column.getWholeColumnName().split("\\.")[0];
            Column nextColumn = attributes.get(i + 1);
            String nextColumnTableName = nextColumn.getWholeColumnName().split("\\.")[0];
            EqualsTo eqExp = new EqualsTo();
            eqExp.setLeftExpression(column);
            eqExp.setRightExpression(nextColumn);
            if (tableName.equals(nextColumnTableName)) {
                updateMap(tableName, selectionExpressions, eqExp);
            }
        }
    }

    /**
     * Creates a mapping from base table to selection expressions corresponding to that table. These selection expressions
     * are based on the previously processed expression.
     *
     * @return a mapping from base table to selection expressions.
     */
    public Map<String, Expression> getExpressions() {
        Map<String, Expression> selectionExpressions = (loneExpressions == null) ? new HashMap<>() : new HashMap<>(loneExpressions);
        for (UnionFindElement element : unionFind.getCollections()) {
            if (element.hasConstraints()) {
                this.valueConstraints(element, selectionExpressions);
            } else {
                this.noValueConstraints(element, selectionExpressions);
            }
        }
        return selectionExpressions;
    }

    @Override
    public void visit(AndExpression exp) {
        exp.getLeftExpression().accept(this);
        exp.getRightExpression().accept(this);
    }

    @Override
    public void visit(EqualsTo exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();
        if (left instanceof Column && right instanceof Column) {
            unionFind.union((Column) left, (Column) right);
        } else if (left instanceof Column && right instanceof LongValue) {
            UnionFindElement e = unionFind.find((Column) left);
            int value = (int) ((LongValue) right).getValue();
            e.setLowerBound(value);
            e.setUpperBound(value);
            e.setEqualityConstraint(value);
        } else if (left instanceof LongValue && right instanceof Column) {
            UnionFindElement e = unionFind.find((Column) right);
            int value = (int) ((LongValue) left).getValue();
            e.setLowerBound(value);
            e.setUpperBound(value);
            e.setEqualityConstraint(value);
        }
    }

    @Override
    public void visit(NotEqualsTo exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();
        if (left instanceof Column && right instanceof LongValue) {
            String tableName = ((Column) left).getWholeColumnName().split("\\.")[0];
            loneExpressions.put(tableName, exp);
        } else if (left instanceof LongValue && right instanceof Column) {
            String tableName = ((Column) right).getWholeColumnName().split("\\.")[0];
            loneExpressions.put(tableName, exp);
        } else {
            unusable.add(exp);
        }
    }

    @Override
    public void visit(GreaterThan exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();
        if (left instanceof Column && right instanceof LongValue) {
            UnionFindElement e = unionFind.find((Column) left);
            int value = (int) ((LongValue) right).getValue();
            e.setLowerBound(value + 1);
        } else if (left instanceof LongValue && right instanceof Column) {
            UnionFindElement e = unionFind.find((Column) right);
            int value = (int) ((LongValue) left).getValue();
            e.setUpperBound(value - 1);
        } else {
            unusable.add(exp);
        }
    }

    @Override
    public void visit(GreaterThanEquals exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();
        if (left instanceof Column && right instanceof LongValue) {
            UnionFindElement e = unionFind.find((Column) left);
            int value = (int) ((LongValue) right).getValue();
            e.setLowerBound(value);
        } else if (left instanceof LongValue && right instanceof Column) {
            UnionFindElement e = unionFind.find((Column) right);
            int value = (int) ((LongValue) left).getValue();
            e.setUpperBound(value);
        } else {
            unusable.add(exp);
        }
    }

    @Override
    public void visit(MinorThan exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();
        if (left instanceof Column && right instanceof LongValue) {
            UnionFindElement e = unionFind.find((Column) left);
            int value = (int) ((LongValue) right).getValue();
            e.setUpperBound(value - 1);
        } else if (left instanceof LongValue && right instanceof Column) {
            UnionFindElement e = unionFind.find((Column) right);
            int value = (int) ((LongValue) left).getValue();
            e.setLowerBound(value + 1);
        } else {
            unusable.add(exp);
        }
    }

    @Override
    public void visit(MinorThanEquals exp) {
        Expression left = exp.getLeftExpression();
        Expression right = exp.getRightExpression();
        if (left instanceof Column && right instanceof LongValue) {
            UnionFindElement e = unionFind.find((Column) left);
            int value = (int) ((LongValue) right).getValue();
            e.setUpperBound(value);
        } else if (left instanceof LongValue && right instanceof Column) {
            UnionFindElement e = unionFind.find((Column) right);
            int value = (int) ((LongValue) left).getValue();
            e.setLowerBound(value);
        } else {
            unusable.add(exp);
        }
    }

    @Override
    public void visit(Column exp) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LongValue exp) {
        // TODO Auto-generated method stub

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