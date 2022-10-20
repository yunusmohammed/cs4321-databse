package com.cs4321.logicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.physicaloperators.JoinExpressionVisitor;
import com.cs4321.physicaloperators.JoinExpressions;
import com.cs4321.physicaloperators.SelectExpressionVisitor;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import utils.LogicalQueryPlanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * The LogicalQueryPlan represents a relational algebra tree that represents the
 * query from which it is built at a mathematical level
 *
 * @author Yunus Mohammed (ymm26@cornell.edu)
 */
public class LogicalQueryPlan {
    private LogicalOperator root;
    private AliasMap aliasMap;

    /**
     * Constructs a LogicalQueryPlan from a given query
     *
     * @param queryStatement The query to construct the logical query plan from
     */
    public LogicalQueryPlan(Statement queryStatement) {this.generateLogicalQueryTree(queryStatement);}

    /**
     * Get the root of this LogicalQueryPlan
     *
     * @return The root of this LogicalQueryPlan
     */
    public LogicalOperator getRoot() {
        return this.root;
    }

    /**
     * Generates a logical query plan from a given query
     *
     * @param queryStatement The query to construct the logical query plan from
     */
    private void generateLogicalQueryTree(Statement queryStatement) {
        if (queryStatement != null) {
            Select select = (Select) queryStatement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();

            List<SelectItem> selectItemsList = selectBody.getSelectItems();
            SelectItem firstSelectItem = selectItemsList.get(0);
            FromItem fromItem = selectBody.getFromItem();
            List<Join> joinsList = selectBody.getJoins();
            List<OrderByElement> orderByElementsList = selectBody.getOrderByElements();
            Expression whereExpression = selectBody.getWhere();
            Distinct distinct = selectBody.getDistinct();

            this.aliasMap = new AliasMap(fromItem, joinsList);

            if ("*".equals(firstSelectItem.toString()) && joinsList == null && whereExpression == null) {
                this.root = generateLogicalScan(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns
                    && (joinsList == null || joinsList.size() == 0)
                    && whereExpression != null) {
                this.root = generateLogicalSelection(selectBody);
            } else if (selectItemsList.size() == 1 && firstSelectItem instanceof AllColumns
                    && joinsList != null && joinsList.size() > 0) {
                this.root = generateLogicalJoin(selectBody);
            } else {
                this.root = generateLogicalProjection(selectBody);
            }

            boolean ordered = false;
            if (orderByElementsList != null && orderByElementsList.size() > 0) {
                this.root = generateLogicalSort(selectBody);
                ordered = true;
            }
            if (distinct != null) {
                // DuplicateEliminationOperator expects sorted child
                if (!ordered)
                    this.root = generateLogicalSort(selectBody);
                this.root = generateLogicalDistinct();
            }
        }

    }

    /**
     * Generate a new logical scan operator
     *
     * @param selectBody The body of the Select statement
     * @return The logical scan operator that was just created
     */
    private LogicalScanOperator generateLogicalScan(PlainSelect selectBody) {
        Table table = (Table) selectBody.getFromItem();
        return new LogicalScanOperator(table, aliasMap);
    }

    /**
     * Generate a new logical select operator
     *
     * @param selectBody The body of the Select statement
     * @return The logical select operator that was just created
     */
    private LogicalSelectionOperator generateLogicalSelection(PlainSelect selectBody) {
        Expression whereExpression = selectBody.getWhere();
        return new LogicalSelectionOperator(whereExpression, generateLogicalScan(selectBody), new SelectExpressionVisitor(),
                this.aliasMap);
    }

    /**
     * Generate a new logical projection operator
     *
     * @param selectBody The body of the Select statement
     * @return The select operator that was just created
     */
    private LogicalProjectionOperator generateLogicalProjection(PlainSelect selectBody) {
        List<SelectItem> selectItemsList = selectBody.getSelectItems();
        Expression whereExpression = selectBody.getWhere();
        List<Join> joinsList = selectBody.getJoins();

        if (joinsList != null) {
            return new LogicalProjectionOperator(selectItemsList, generateLogicalJoin(selectBody), this.aliasMap);
        } else if (whereExpression == null) {
            return new LogicalProjectionOperator(selectItemsList, generateLogicalScan(selectBody), this.aliasMap);
        } else {
            return new LogicalProjectionOperator(selectItemsList, generateLogicalSelection(selectBody), this.aliasMap);
        }
    }

    /**
     * Generates a new logical sort operator.
     *
     * @param selectBody The body of the select statement.
     * @return A new logical sort operator.
     */
    private LogicalSortOperator generateLogicalSort(PlainSelect selectBody) {
        return new LogicalSortOperator(root, LogicalQueryPlanUtils.getColumnIndex(selectBody, this.aliasMap),
                selectBody.getOrderByElements());
    }

    /**
     * Generates a new logical duplicate elimination operator.
     *
     * @return A new logical duplicate elimination operator.
     */
    private LogicalOperator generateLogicalDistinct() {
        return new LogicalDuplicateEliminationOperator(root);
    }

    /**
     * Generates a logical join query plan
     *
     * @param selectBody The body of a select statement. The joins field must be non
     *                   null and not empty
     * @return the root of the logical join query plan
     */
    private LogicalJoinOperator generateLogicalJoin(PlainSelect selectBody) {
        LogicalJoinOperator root = new LogicalJoinOperator();
        LogicalJoinOperator currentParent = root;
        List<Join> joins = new ArrayList<>(selectBody.getJoins());
        Map<String, Integer> tableOffset = LogicalQueryPlanUtils.generateJoinTableOffsets(selectBody, this.aliasMap);
        Stack<BinaryExpression> expressions = LogicalQueryPlanUtils.getExpressions(selectBody.getWhere());
        while (joins.size() > 0) {
            Table rightChildTable = (Table) joins.remove(joins.size() - 1).getRightItem();

            String rightChildTableName = rightChildTable.getAlias();
            rightChildTableName = (rightChildTableName != null) ? rightChildTableName : rightChildTable.getName();

            JoinExpressions joinExpressions = LogicalQueryPlanUtils.getJoinExpressions(expressions, rightChildTableName);

            Stack<Expression> parentExpressions = joinExpressions.getParentExpressions();
            Stack<BinaryExpression> leftChildExpressions = joinExpressions.getLeftExpressions();
            Stack<Expression> rightChildExpressions = joinExpressions.getRightChildExpressions();
            expressions = leftChildExpressions;

            // Set Right Child of current Parent
            currentParent.setRightChild(getJoinChildOperator(rightChildExpressions, rightChildTable));

            // Set Join Condition of current parent
            currentParent.setJoinCondition(LogicalQueryPlanUtils.generateExpressionTree(parentExpressions));

            // Set ExpressionVisitor of current parent
            JoinExpressionVisitor visitor = new JoinExpressionVisitor(this.aliasMap, tableOffset,
                    rightChildTableName);
            currentParent.setJoinExpressionVisitor(visitor);

            // Set left child of current parent
            LogicalOperator leftOperator;
            if (joins.size() == 0) {
                currentParent
                        .setLeftChild(getJoinChildOperator((Stack) leftChildExpressions, selectBody.getFromItem()));
            } else {
                leftOperator = new LogicalJoinOperator();
                currentParent.setLeftChild(leftOperator);
                currentParent = (LogicalJoinOperator) leftOperator;
            }
        }
        return root;
    }

    /**
     * Generate a child operator evaluating childExpressions on childTable
     *
     * @param childExpressions stack of expressions to be evaluated by the child
     *                         operator
     * @param childTable       table corresponding to the child operator
     * @return the child operator
     */
    private LogicalOperator getJoinChildOperator(Stack<Expression> childExpressions, FromItem childTable) {
        LogicalOperator operator;
        PlainSelect selectBody = new PlainSelect();
        selectBody.setFromItem(childTable);
        if (childExpressions == null || childExpressions.size() == 0)
            operator = generateLogicalScan(selectBody);
        else {
            Expression rightChildExpression = LogicalQueryPlanUtils.generateExpressionTree(childExpressions);
            selectBody.setWhere(rightChildExpression);
            operator = generateLogicalSelection(selectBody);
        }
        return operator;
    }
}
