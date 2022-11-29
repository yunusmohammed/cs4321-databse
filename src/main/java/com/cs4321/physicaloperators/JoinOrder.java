package com.cs4321.physicaloperators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import com.cs4321.app.AliasMap;
import com.cs4321.app.ColumnStatsInfo;
import com.cs4321.app.DSUExpressionVisitor;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Logger;
import com.cs4321.app.TableStatsInfo;
import com.cs4321.app.UnionFind;
import com.cs4321.app.UnionFindElement;
import com.cs4321.logicaloperators.LogicalOperator;
import com.cs4321.logicaloperators.LogicalScanOperator;
import com.cs4321.logicaloperators.LogicalSelectionOperator;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import utils.LogicalQueryPlanUtils;

public class JoinOrder {

    private static DatabaseCatalog dbc = DatabaseCatalog.getInstance();
    private static Map<String, TableStatsInfo> tableStatsMap = dbc.getTableStatsMap();
    private static Logger logger = Logger.getInstance();
    private static DSUExpressionVisitor selectionVisitor;
    // valuesMap maps table names (alias if present) to a map which maps column
    // names to their associated V-Value.
    // valuesMap.get(table).get(column) = V(table, column)
    private static HashMap<String, HashMap<String, Double>> valuesMap;
    private static AliasMap aliasMap;

    /**
     * Returns an optimal order of LogicalOperators for a join
     * 
     * @param joinChildren    - the children of the logical join operator
     * @param whereExpression - the where clause from the select body
     * @param aliasMap        - map from alias names to table names
     * @return a list of logical operators in the order which they should be joined
     */
    public static List<LogicalOperator> getJoinOrder(List<LogicalOperator> joinChildren,
            Expression whereExpression, AliasMap aliasMap) {
        // each subset stores the indexes of the children in joinChildren
        List<HashSet<Integer>> subsets = generateSubsets(joinChildren.size());
        // sort in increasing order of subset size for our dp algorithm
        Collections.sort(subsets, (a, b) -> a.size() - b.size());
        // each subset is mapped to a pair of an Integer array and an optimal ordering
        // of the indices in the subset.
        // the Integer array contains exactly two elements: the first index stores the
        // cost of the join,
        // and the second stores the size of the joined relation
        HashMap<HashSet<Integer>, Map.Entry<Integer[], List<Integer>>> dpMap = new HashMap<>();
        // union find will help us compute V-Values for selection operators and joins
        // later
        JoinOrder.aliasMap = aliasMap;
        Expression selectionExpression = getSelectionExpression(whereExpression);
        selectionVisitor = new DSUExpressionVisitor();
        selectionVisitor.processExpression(selectionExpression, aliasMap);
        valuesMap = new HashMap<>();

        List<Table> tables = new ArrayList<>();
        for (LogicalOperator operator : joinChildren) {
            LogicalScanOperator scan = getLogicalScanOperator(operator);
            tables.add(scan.getTable());
        }

        // initialize V-Values for every table column while factoring in selections
        for (Table t : tables) {
            String name = t.getAlias();
            if (name == null)
                name = t.getName();
            String baseTableName = t.getName();
            String[] tableSchema = dbc.tableSchema(baseTableName);
            for (int i = 1; i < tableSchema.length; i++) {
                Column col = new Column(t, name + "." + tableSchema[i]);
                setColumnValue(col);
            }
        }
        // for every table, the V-Values of every column are set to the minimum V-Value
        // of all the columns in the table
        for (String table : valuesMap.keySet()) {
            Double min = null;
            HashMap<String, Double> tableValues = valuesMap.get(table);
            Collection<Double> vValues = tableValues.values();
            for (Double d : vValues) {
                min = min == null ? d : Math.min(min, d);
            }
            for (String column : tableValues.keySet()) {
                tableValues.put(column, min);
            }
        }

        // build dpMap
        for (int i = 0; i < subsets.size(); i++) {
            HashSet<Integer> set = subsets.get(i);
            List<Integer> ordering = new ArrayList<>();
            List<List<Column>> equalityConditions = equalityConditions(whereExpression);
            if (set.size() == 1) {
                ordering.addAll(set);
                dpMap.put(set, new SimpleEntry<>(new Integer[] { 0, 0 }, ordering));
            } else if (set.size() == 2) {
                Iterator<Integer> iterator = set.iterator();
                // first and second are two indices in joinChildren
                int first = iterator.next();
                int second = iterator.next();
                int firstSize = getTableSize(joinChildren.get(first));
                int secondSize = getTableSize(joinChildren.get(second));
                int relationSize;
                // choose the smaller relation to be the outer
                if (firstSize < secondSize) {
                    ordering.addAll(Arrays.asList(first, second));
                    relationSize = computeRelationSize(equalityConditions, joinChildren, Arrays.asList(first),
                            firstSize, second, secondSize);
                } else {
                    ordering.addAll(Arrays.asList(second, first));
                    relationSize = computeRelationSize(equalityConditions, joinChildren, Arrays.asList(second),
                            secondSize, first, firstSize);
                }

                dpMap.put(set, new SimpleEntry<>(new Integer[] { 0, relationSize }, ordering));
            } else {
                HashSet<Integer> removeOneElement = new HashSet<>(set);
                // index gives us the index (of a logical operator in joinChildren) of the
                // rightmost relation for the join
                for (Integer index : set) {
                    removeOneElement.remove(index);
                    Map.Entry<Integer[], List<Integer>> subsetInfo = dpMap.get(removeOneElement);
                    int rightRelationSize = getTableSize(joinChildren.get(index));
                    List<Integer> leftRelation = subsetInfo.getValue();
                    int leftJoinCost = subsetInfo.getKey()[0];
                    int leftRelationSize = subsetInfo.getKey()[1];
                    int overallCost = leftJoinCost + leftRelationSize;
                    int relationSize = computeRelationSize(equalityConditions, joinChildren, leftRelation,
                            leftRelationSize, index, rightRelationSize);
                    // check if we have found a cheaper join order
                    if (!dpMap.containsKey(set) || dpMap.get(set).getKey()[0] > overallCost) {
                        List<Integer> optimalOrdering = new ArrayList<>(leftRelation);
                        optimalOrdering.add(index);
                        dpMap.put(set, new SimpleEntry<>(new Integer[] { overallCost, relationSize }, optimalOrdering));
                    }
                    removeOneElement.add(index);
                }
            }
        }

        // return final ordering
        HashSet<Integer> largestSubset = subsets.get(subsets.size() - 1);
        List<Integer> indices = dpMap.get(largestSubset).getValue();
        return indices.stream().map(index -> joinChildren.get(index)).collect(Collectors.toList());
    }

    /**
     * Estimates the size of a relation after a join between rightRelation and the
     * elements of leftRelation
     * 
     * @param equalityConditions - join conditions
     * @param joinChildren       - list of children of logical join operator
     * @param leftRelation       - list of indexes of logical operators in
     *                           joinChildren that have already been joined
     * @param leftRelationSize   - estimated size of the join between the logical
     *                           operators in joinChildren
     * @param rightRelation      - the rightmost relation for the current join
     * @param rightRelationSize  - the size of the rightmost relation for the
     *                           current join
     * @return - the estimated size of a join
     */
    private static int computeRelationSize(List<List<Column>> equalityConditions, List<LogicalOperator> joinChildren,
            List<Integer> leftRelation, int leftRelationSize, int rightRelation, int rightRelationSize) {
        int numerator = leftRelationSize * rightRelationSize;
        double denominator = 1;

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
            String leftConditionTable = leftColumn.getWholeColumnName().split("\\.")[0];
            String rightConditionTable = rightColumn.getWholeColumnName().split("\\.")[0];

            // check if equality condition is relevant for this join
            if (leftTables.contains(leftConditionTable) && rightTable.equals(rightConditionTable)) {
                double leftValue = value(leftRelation, leftColumn, joinChildren);
                double rightValue = value(new ArrayList<>(rightRelation), rightColumn, joinChildren);
                denominator *= Math.max(leftValue, rightValue);
            } else if (leftTables.contains(rightConditionTable) && rightTable.equals(leftConditionTable)) {
                double leftValue = value(leftRelation, rightColumn, joinChildren);
                double rightValue = value(new ArrayList<>(rightRelation), leftColumn, joinChildren);
                denominator *= Math.max(leftValue, rightValue);
            }

        }

        return (int) (numerator / denominator);
    }

    /**
     * Filters out unnecessary expressions for selection conditions
     * 
     * @param whereExpression - the where clause from the select body
     * @return - an expression with terms relevant for computing V-Values associated
     *         with selection
     */
    private static Expression getSelectionExpression(Expression whereExpression) {
        Stack<BinaryExpression> expressions = LogicalQueryPlanUtils.getExpressions(whereExpression);
        List<BinaryExpression> selectionExpressions = new ArrayList<>();
        for (BinaryExpression binExp : expressions) {
            if (!(binExp instanceof NotEqualsTo
                    || (!(binExp instanceof EqualsTo) && binExp.getLeftExpression() instanceof Column
                            && binExp.getRightExpression() instanceof Column))) {
                selectionExpressions.add(binExp);
            }
        }
        // build an expression to use for selectionVisitor
        Expression exp = null;
        if (selectionExpressions.size() == 0)
            return whereExpression;
        if (selectionExpressions.size() == 1)
            exp = selectionExpressions.get(0);
        else {
            exp = new AndExpression(selectionExpressions.get(0), selectionExpressions.get(1));
            for (int i = 2; i < selectionExpressions.size(); i++) {
                exp = new AndExpression(exp, selectionExpressions.get(i));
            }
        }
        return exp;
    }

    /**
     * Sets the V-Value for the given column in valuesMap
     * 
     * @param column - a column among the tables being joined
     */
    private static void setColumnValue(Column column) {
        String[] columnStrings = column.getWholeColumnName().split("\\.");
        String table = columnStrings[0];
        String baseTableName = aliasMap.getBaseTable(table);
        String attribute = columnStrings[1];
        TableStatsInfo tableInfo = tableStatsMap.get(baseTableName);
        List<ColumnStatsInfo> columnInfos = tableInfo.getColumnStatsInfoList();
        ColumnStatsInfo colInfo = null;
        Double v = null;
        // find base V-Value
        for (ColumnStatsInfo columnInfo : columnInfos) {
            if (columnInfo.getColumnName().equals(attribute)) {
                colInfo = columnInfo;
                v = columnInfo.getMaxValue() - columnInfo.getMinValue() + 1.0;
            }
        }
        HashMap<String, Double> tableValues = valuesMap.getOrDefault(table, new HashMap<>());
        // adjust V-Values if the column is associated with a selection
        UnionFindElement element = selectionVisitor.getUnionFind().find(column);
        Integer lowerbound = element.getLowerBound(), upperbound = element.getUpperBound();
        if (lowerbound == null)
            lowerbound = colInfo.getMinValue();
        if (upperbound == null)
            upperbound = colInfo.getMaxValue();
        if (lowerbound > upperbound)
            v = 1.0;
        else
            v *= (upperbound - lowerbound + 1.0) / v;
        tableValues.put(attribute, v);
        valuesMap.put(table, tableValues);

    }

    /**
     * Computes a V-Value for the given column in a relation
     * 
     * @param relation     - the list of indexes of logical operators from
     *                     joinChildren in the given relation (can be a join)
     * @param column       - the column relevant for the V-Value
     * @param joinChildren - the children of the logical join operator
     * @return - a V-Value
     */
    private static double value(List<Integer> relation, Column column,
            List<LogicalOperator> joinChildren) {
        String[] columnStrings = column.getWholeColumnName().split("\\.");
        String table = columnStrings[0];
        String attribute = columnStrings[1];
        Double minV = valuesMap.get(table).get(attribute);
        if (relation.size() > 1) {
            // make sure we only consider v-values within the join
            HashSet<String> joinTableNames = new HashSet<>();
            for (Integer operatorIndex : relation) {
                LogicalScanOperator scan = getLogicalScanOperator(joinChildren.get(operatorIndex));
                joinTableNames.add(scan.getTableName());
            }
            UnionFindElement union = selectionVisitor.getUnionFind().find(column);
            for (Column col : union.getAttributes()) {
                String[] colInfo = col.getWholeColumnName().split("\\.");
                String colTable = colInfo[0];
                String colAttribute = colInfo[1];
                Double attributeV = valuesMap.get(colTable).get(colAttribute);
                if (joinTableNames.contains(colTable))
                    minV = Math.min(minV, attributeV);
            }
        }
        return minV;

    }

    /**
     * Returns a list of lists which contain the columns for each equality
     * expression. Each list in the result has exactly two elements, which are the
     * columns in an
     * equalsTo expression.
     * 
     * @param whereExpression - the where clause from the select body
     * @return a list of lists of columns
     */
    private static List<List<Column>> equalityConditions(Expression whereExpression) {
        // use a hash set to eliminate duplicate join conditions which might affect
        // table size calculation
        HashSet<HashSet<Column>> conditions = new HashSet<>();
        Stack<Expression> stackExpression = new Stack<>();
        if (whereExpression != null)
            stackExpression.push(whereExpression);
        while (stackExpression.size() > 0) {
            Expression exp = stackExpression.pop();
            // check if expression is part of join conditions
            if (exp instanceof EqualsTo && ((EqualsTo) exp).getLeftExpression() instanceof Column
                    && ((EqualsTo) exp).getRightExpression() instanceof Column) {
                Column leftColumn = (Column) ((EqualsTo) exp).getLeftExpression();
                Column rightColumn = (Column) ((EqualsTo) exp).getRightExpression();
                HashSet<Column> columns = new HashSet<>(Arrays.asList(leftColumn, rightColumn));
                conditions.add(columns);
            } else if (exp instanceof AndExpression) {
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
        List<HashSet<Integer>> subsets = new ArrayList<>();
        subsets.add(new HashSet<>());
        // after the i'th iteration, all subsets of the set {0, 1, ..., i} are generated
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
