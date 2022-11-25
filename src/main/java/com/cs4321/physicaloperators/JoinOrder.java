package com.cs4321.physicaloperators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.AbstractMap.SimpleEntry;

import com.cs4321.app.ColumnStatsInfo;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Logger;
import com.cs4321.app.TableStatsInfo;
import com.cs4321.logicaloperators.LogicalOperator;
import com.cs4321.logicaloperators.LogicalScanOperator;
import com.cs4321.logicaloperators.LogicalSelectionOperator;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import utils.LogicalQueryPlanUtils;

public class JoinOrder {

    private static DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private static Map<String, TableStatsInfo> tableStatsMap = dbc.getTableStatsMap();
    private static Logger logger = Logger.getInstance();

    /**
     * Selects a join order to optimize plan costs
     * 
     * @param joinChildren   - children of the join operator
     * @param joinExpression - the condition to join rows on
     * @return a list of operators in the order for the join
     */
    public static List<Operator> getJoinOrder(List<LogicalOperator> joinChildren, Expression joinExpression) {
        // each subset stores the indexes of the children in joinChildren
        List<HashSet<Integer>> subsets = generateSubsets(joinChildren.size());
        // sort in increasing order of subset size
        Collections.sort(subsets, (a, b) -> a.size() - b.size());
        // each subset is mapped to a pair of an Integer array and an optimal ordering
        // of the indices in the subset.
        // the Integer array contains exactly two elements: the first index stores the
        // cost of the join,
        // and the second stores the size of the joined relation
        HashMap<HashSet<Integer>, Map.Entry<Integer[], List<Integer>>> map = new HashMap<>();
        // subsets is sorted in increasing order of subset size
        for (int i = 0; i < subsets.size(); i++) {
            HashSet<Integer> set = subsets.get(i);
            List<Integer> ordering = new ArrayList<>();
            List<List<Column>> equalityConditions = equalityConditions(joinExpression);
            HashMap<String, HashMap<String, Integer>> valuesMap = new HashMap<>();
            if (set.size() == 1) {
                ordering.addAll(set);
                map.put(set, new SimpleEntry<>(new Integer[] { 0, 0 }, ordering));
            } else if (set.size() == 2) {
                Iterator<Integer> iterator = set.iterator();
                // first and second are two indices in joinChildren
                int first = iterator.next();
                int second = iterator.next();
                int firstSize = getTableSize(joinChildren.get(first));
                int secondSize = getTableSize(joinChildren.get(second));
                if (firstSize < secondSize)
                    ordering.addAll(Arrays.asList(first, second));
                else
                    ordering.addAll(Arrays.asList(second, first));
                int relationSize = computeRelationSize(equalityConditions, joinChildren,
                        new ArrayList<>(first), firstSize, second, secondSize, valuesMap);
                map.put(set, new SimpleEntry<>(new Integer[] { 0, relationSize }, ordering));
            } else {

            }
        }

    }

    private static int computeRelationSize(List<List<Column>> equalityConditions, List<LogicalOperator> joinChildren,
            List<Integer> leftRelation, int leftRelationSize, int rightRelation, int rightRelationSize,
            HashMap<String, HashMap<String, Integer>> valuesMap) {
        int numerator = leftRelationSize * rightRelationSize;
        int denominator = 1;

        // store the table (or alias) name from each table in the leftRelation in a set
        HashSet<String> leftTables = new HashSet<>();
        for (int operatorIndex : leftRelation) {
            LogicalScanOperator scan = getLogicalScanOperator(joinChildren.get(operatorIndex));
            leftTables.add(scan.getTableName());
        }
        String rightTable = getLogicalScanOperator(joinChildren.get(rightRelation)).getTableName();

        // iterate over each condition to see if they are relevant for the join
        for (int i = 0; i < equalityConditions.size(); i++) {

            // get information on table (or alias) name and attributes for equality
            // condition
            List<Column> condition = equalityConditions.get(i);
            Column leftColumn = condition.get(0);
            Column rightColumn = condition.get(1);
            String[] leftStrings = leftColumn.getWholeColumnName().split("\\.");
            String[] rightStrings = rightColumn.getWholeColumnName().split("\\.");
            String leftConditionTable = leftStrings[0];
            String rightConditionTable = rightStrings[0];
            String leftConditionAttribute = leftStrings[1];
            String rightConditionAttribute = rightStrings[1];

            // check if equality condition is relevant for this join
            if (leftTables.contains(leftConditionTable) && rightTable.equals(rightConditionTable)) {
                int leftValue = value(leftRelation, leftConditionTable, leftConditionAttribute, equalityConditions,
                        joinChildren, valuesMap);
                int rightValue = value(new ArrayList<>(rightRelation), rightConditionTable, rightConditionAttribute,
                        equalityConditions, joinChildren, valuesMap);
                denominator *= Math.max(leftValue, rightValue);
            } else if (leftTables.contains(rightConditionTable) && rightTable.equals(leftConditionTable)) {
                int leftValue = value(leftRelation, rightConditionTable, rightConditionAttribute, equalityConditions,
                        joinChildren, valuesMap);
                int rightValue = value(new ArrayList<>(rightRelation), leftConditionTable, leftConditionAttribute,
                        equalityConditions, joinChildren, valuesMap);
                denominator *= Math.max(leftValue, rightValue);
            }

        }

        return numerator / denominator;
    }

    private static int baseTableV(TableStatsInfo tableInfo, String attribute, String table) {
        List<ColumnStatsInfo> columnInfos = tableInfo.getColumnStatsInfoList();
        for (ColumnStatsInfo columnInfo : columnInfos) {
            if (columnInfo.getColumnName().equals(attribute)) {
                return columnInfo.getMaxValue() - columnInfo.getMinValue() + 1;
            }
        }
        logger.log("Unable to find column" + attribute + "in table " + table);
        throw new Error();
    }

    private static Integer[] selectionMinMax(Expression selectCondition) {
        Stack<BinaryExpression> expressions = LogicalQueryPlanUtils.getExpressions(selectCondition);
        // store the minimum and maximum range after every selection condition
        Integer min, max;
        for (BinaryExpression binExp : expressions) {
            if (binExp instanceof GreaterThan) {
                Expression left = binExp.getLeftExpression();
                Expression right = binExp.getRightExpression();
                if (left instanceof Column && !(right instanceof Column)) {

                } else if (!(left instanceof Column) && right instanceof Column) {

                }
            } else if (binExp instanceof GreaterThanEquals) {

            } else if (binExp instanceof MinorThan) {

            } else if (binExp instanceof MinorThanEquals) {

            } else if (binExp instanceof EqualsTo) {
                
            }
        }
    }

    private static int value(List<Integer> relations, String table, String attribute,
            List<List<Column>> equalityConditions,
            List<LogicalOperator> joinChildren, HashMap<String, HashMap<String, Integer>> valuesMap) {
        if (relations.size() == 1) {
            // check if we have already computed v-Value
            if (valuesMap.containsKey(table) && valuesMap.get(table).containsKey(attribute)) {
                return valuesMap.get(table).get(attribute);
            }
            LogicalOperator operator = joinChildren.get(relations.get(0));
            LogicalScanOperator scan = getLogicalScanOperator(operator);
            String baseTable = scan.getBaseTableName();
            TableStatsInfo tableInfo = tableStatsMap.get(baseTable);
            int v = baseTableV(tableInfo, attribute, table);
            // update valuesMap with v-Value
            HashMap<String, Integer> tableMapping = valuesMap.getOrDefault(table, new HashMap<>());
            // relation is base table
            if (operator instanceof LogicalScanOperator) {
                v = Math.max(1, Math.min(tableInfo.getNumberOfTuples(), v));
                tableMapping.put(attribute, v);
                valuesMap.put(table, tableMapping);
                return v;
            }
            // relation is selection on a base table
            else {
                LogicalSelectionOperator selection = (LogicalSelectionOperator) operator;
                Expression selectCondition = selection.getSelectCondition();

            }
        }
        // relation is join
        else {

        }
    }

    /**
     * Returns a list of lists which contain the columns for each equality
     * expression.
     * 
     * @param joinExpression - the expression from join operator
     * @return a set of sets of columns
     */
    private static List<List<Column>> equalityConditions(Expression joinExpression) {
        // use a hash set to eliminate duplicate join conditions
        HashSet<HashSet<Column>> conditions = new HashSet<>();
        Stack<Expression> stackExpression = new Stack<>();
        if (joinExpression != null)
            stackExpression.push(joinExpression);
        while (stackExpression.size() > 0) {
            Expression exp = stackExpression.pop();
            if (exp instanceof EqualsTo) {
                Column leftColumn = (Column) ((EqualsTo) exp).getLeftExpression();
                Column rightColumn = (Column) ((EqualsTo) exp).getRightExpression();
                HashSet<Column> columns = new HashSet<>(Arrays.asList(leftColumn, rightColumn));
                conditions.add(columns);
            } else {
                AndExpression andExp = (AndExpression) exp;
                stackExpression.add(andExp.getRightExpression());
                stackExpression.add(andExp.getLeftExpression());
            }
        }
        List<List<Column>> conditionsList = new ArrayList<>();
        for (HashSet<Column> columns : conditions) {
            conditionsList.add(new ArrayList<>(columns));
        }
        return conditionsList;
    }

    /**
     * Returns a logical scan operator that is a child of the given operator.
     * Requires: operator is either a logical
     * selection or scan operator
     * 
     * @param operator - the logical selection or scan operator
     * @return a logical scan operator
     */
    private static LogicalScanOperator getLogicalScanOperator(LogicalOperator operator) {
        if (operator instanceof LogicalSelectionOperator) {
            return ((LogicalSelectionOperator) operator).getChild();
        } else if (operator instanceof LogicalScanOperator) {
            return (LogicalScanOperator) operator;
        } else {
            logger.log("Error finding logical scan operator in join order");
            throw new Error();
        }
    }

    /**
     * Returns the table size of the table associated with an operator
     * 
     * @param operator - the operator to find the table size for
     * @return an integer of the number of tuples in the relevant table
     */
    private static int getTableSize(LogicalOperator operator) {
        LogicalScanOperator scan = getLogicalScanOperator(operator);
        return tableStatsMap.get(scan.getBaseTableName()).getNumberOfTuples();
    }

    /**
     * Returns all subsets of the set {0, 1, ..., n-1} besides the empty set.
     * Requires: n >= 1
     * 
     * @param n - the size of the set to generate subsets from
     * @return a list of subsets
     */
    private static List<HashSet<Integer>> generateSubsets(int n) {
        List<HashSet<Integer>> subsets = new ArrayList<>(new HashSet<>());
        for (int i = 0; i < n; i++) {
            List<HashSet<Integer>> updatedSubsets = new ArrayList<>();
            for (HashSet<Integer> set : subsets) {
                HashSet<Integer> addI = new HashSet<>(set);
                addI.add(i);
                updatedSubsets.add(addI);
                updatedSubsets.add(set);
            }
            subsets = updatedSubsets;
        }
        // remove the empty set
        subsets.remove(subsets.size() - 1);
        return subsets;
    }
}
