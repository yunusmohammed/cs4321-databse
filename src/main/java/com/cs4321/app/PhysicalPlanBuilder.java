package com.cs4321.app;

import com.cs4321.indexes.BPlusTree;
import com.cs4321.logicaloperators.*;
import com.cs4321.physicaloperators.*;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import utils.LogicalQueryPlanUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The PhysicalPlanBuilder is a singleton that provides information about how to
 * create a physical tree from a logical tree.
 *
 * @author Lenhard Thomas
 */
public class PhysicalPlanBuilder {

    static Map<String, IndexInfo> indexInfoConfigMap;
    private static PhysicalPlanBuilder instance;
    private static boolean humanReadable;
    private static Map<String, Integer> tableOrder;
    private static Map<String, Integer> pagePerIndex;

    private static int BUFFER_SIZE = 10;

    /**
     * Private constructor to follow the singleton pattern.
     */
    private PhysicalPlanBuilder() {
    }

    /**
     * Reads the config file to determine how to construct physical tree.
     */
    public static void setConfigs() {
        IndexInfoConfig indexInfoConfig = new IndexInfoConfig(DatabaseCatalog.getInputdir()
                + File.separator + "db" + File.separator + "index_info.txt");
        indexInfoConfigMap = indexInfoConfig.getIndexInfoMap();
    }

    /**
     * Creates a mapping from name of a column, using the base table, to the number
     * of leaf pages.
     *
     * @param indexes The list of indexes.
     */
    public static void createPagePerIndex(List<BPlusTree> indexes) {
        Map<String, Integer> map = new HashMap<>();
        for (BPlusTree tree : indexes) {
            map.put(tree.getWholeColumnName(), tree.getLeafCount());
        }
        pagePerIndex = map;
    }

    /**
     * Sets the value of humanReadable
     *
     * @param humanReadableFormat true if the project is set to read/write human
     *                            readable files
     */
    public static void setHumanReadable(boolean humanReadableFormat) {
        humanReadable = humanReadableFormat;
    }

    /**
     * Initializes and returns the PhysicalPlanBuilder singleton.
     *
     * @return The PhysicalPlanBuilder singleton.
     */
    public static PhysicalPlanBuilder getInstance() {
        if (instance == null)
            instance = new PhysicalPlanBuilder();
        return PhysicalPlanBuilder.instance;
    }

    /**
     * Creates a physical query plan from a logical query plan.
     *
     * @param logicalTree The logical query plan acting as a blueprint.
     * @return The physical query plan corresponding to the logical query plan.
     */
    public Operator constructPhysical(LogicalOperator logicalTree) {
        return logicalTree.accept(this);
    }

    /**
     * Sets the mapping from table name to position in `FROM` clause.
     *
     * @param from  First item in the `FROM` clause
     * @param joins Potential list of tables being joined onto.
     */
    public static void setTableOrder(FromItem from, List<Join> joins) {
        tableOrder = new HashMap<>();
        Table fromTable = (Table) from;
        String tableName = fromTable.getAlias();
        if (tableName == null)
            tableName = fromTable.getName();
        tableOrder.put(tableName, 0);
        if (joins == null)
            return;
        for (int i = 0; i < joins.size(); i++) {
            Join join = joins.get(i);
            Table joinTable = (Table) join.getRightItem();
            tableName = joinTable.getAlias();
            if (tableName == null)
                tableName = joinTable.getName();
            tableOrder.put(tableName, i + 1);
        }
    }

    /**
     * Creates a physical scan operator from the logical scan operator.
     *
     * @param operator The logical scan operator acting as a blueprint.
     * @return The physical scan operator corresponding to the logical scan
     * operator.
     */
    public Operator visit(LogicalScanOperator operator) {
        return new FullScanOperator(operator.getTable(), operator.getAliasMap(), humanReadable);
    }

    /**
     * Creates an index scan on `indexColumn` with the range from low to high as
     * specified by lowHigh.
     *
     * @param operator    The selection operator being used to generate an index
     *                    scan operator
     * @param indexColumn The column to create the index on
     * @param lowHigh     The low and high value for the index
     * @return A new index scan on `indexColumn` with the range from low to high as
     * specified by lowHigh.
     */
    public IndexScanOperator generateIndexScan(LogicalSelectionOperator operator, Column indexColumn,
                                               List<Integer> lowHigh) {
        String baseTableName = operator.getAliasMap().getBaseTable(indexColumn.getTable().getName());
        String wholeBaseColumnName = String.format("%s.%s", baseTableName, indexColumn.getColumnName());
        String dbPath = DatabaseCatalog.getInputdir() + File.separator + "db";
        String indexPath = dbPath + File.separator + "indexes" + File.separator + wholeBaseColumnName;
        boolean isClustered = indexInfoConfigMap.get(wholeBaseColumnName).isClustered();
        try {
            return new IndexScanOperator(indexColumn.getTable(), operator.getAliasMap(),
                    indexPath, indexColumn.getColumnName(), lowHigh.get(0), lowHigh.get(1), isClustered);
        } catch (IOException e) {
            Logger.getInstance().log(e.getMessage());
        }
        return null;
    }

    public String chooseIndex(LogicalSelectionOperator operator) {
        String baseTableName = operator.getBaseTableName();
        TableStatsInfo stats = DatabaseCatalog.getInstance().getTableStatsMap().get(baseTableName);
        String index = null;
        double tupSize = 4 * (DatabaseCatalog.getInstance().tableSchema(baseTableName).length - 1);
        double numBytes = 4096;
        double cost = (long) stats.getNumberOfTuples() * tupSize / numBytes;
        Set<String> possibleIndexes = DatabaseCatalog.getInstance().getIndexColumns().get(baseTableName);
        if (possibleIndexes == null)
            return index;
        for (String candidate : possibleIndexes) {
            IndexSelectionVisitor visitor = operator.getIndexVisitor();
            visitor.splitExpression(operator.getSelectCondition(), candidate);
            ColumnStatsInfo info = stats.getColumnStatsInfoMap().get(candidate);
            double tableVals = info.getMaxValue() - info.getMinValue() + 1;
            double numVals = 0;
            if (!visitor.getLowHigh().isEmpty()) {
                int low = Math.max(visitor.getLowHigh().get(0), info.getMinValue());
                int high = Math.min(visitor.getLowHigh().get(1), info.getMaxValue());
                numVals = high - low + 1;
            }
            double reduction_factor = numVals / tableVals;
            String baseColumnName = baseTableName + "." + candidate;
            boolean isClustered = indexInfoConfigMap.get(baseColumnName).isClustered();
            if (isClustered) {
                double new_cost = 3 + reduction_factor * (stats.getNumberOfTuples() * tupSize / numBytes);
                if (new_cost < cost) {
                    index = candidate;
                    cost = new_cost;
                }
            } else {
                int l = pagePerIndex.get(baseColumnName);
                double new_cost = 3 + l * reduction_factor + stats.getNumberOfTuples() * reduction_factor;
                if (new_cost < cost) {
                    index = candidate;
                    cost = new_cost;
                }
            }
        }
        return index;
    }

    /**
     * Creates a physical selection operator from the logical selection operator.
     *
     * @param operator The logical selection operator acting as a blueprint.
     * @return The physical selection operator corresponding to the logical
     * selection operator.
     */
    public Operator visit(LogicalSelectionOperator operator) {
        String indexColumnName = chooseIndex(operator);
        if (indexColumnName != null) {
            IndexSelectionVisitor visitor = operator.getIndexVisitor();
            visitor.splitExpression(operator.getSelectCondition(), indexColumnName);
            Column indexColumn = visitor.getIndexColumn();
            List<Integer> lowHigh = visitor.getLowHigh();
            Expression noIndexExpression = visitor.getNoIndexExpression();
            // Construct just a full scan if there's no way to use an index
            if (indexColumn == null) {
                FullScanOperator child = (FullScanOperator) constructPhysical(operator.getChild());
                return new SelectionOperator(operator.getSelectExpressionVisitor(), operator.getAliasMap(),
                        operator.getSelectCondition(), child);
            }
            // Construct just an index scan if every sub-expression can be indexed
            else if (noIndexExpression == null) {
                return generateIndexScan(operator, indexColumn, lowHigh);
            }
            // Construct a selection using the reduced expression with the child being an
            // index scan
            else {
                ScanOperator indexScan = generateIndexScan(operator, indexColumn, lowHigh);
                return new SelectionOperator(operator.getSelectExpressionVisitor(), operator.getAliasMap(),
                        noIndexExpression, indexScan);
            }
        } else {
            FullScanOperator child = (FullScanOperator) constructPhysical(operator.getChild());
            return new SelectionOperator(operator.getSelectExpressionVisitor(), operator.getAliasMap(),
                    operator.getSelectCondition(), child);
        }
    }

    /**
     * Creates a physical projection operator from the logical projection operator.
     *
     * @param operator The logical projection operator acting as a blueprint.
     * @return The physical projection operator corresponding to the logical
     * projection operator.
     */
    public Operator visit(LogicalProjectionOperator operator) {
        Operator child = constructPhysical(operator.getChild());
        return new ProjectionOperator(operator.getAliasMap(), operator.getSelectItems(), child);
    }

    /**
     * Returns a new list without duplicates in the list of order by elements.
     *
     * @param lst      List of order by elements.
     * @param aliasMap An AliasMap for handling aliases.
     * @return A copy of lst but with duplicates removed.
     */
    private List<OrderByElement> deduplicate(List<OrderByElement> lst, AliasMap aliasMap) {
        List<OrderByElement> dedup = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        for (OrderByElement orderByElement : lst) {
            Column c = (Column) orderByElement.getExpression();
            String baseTableName = c.getWholeColumnName().split("\\.")[0];
            String wholeName = baseTableName + "." + c.getColumnName();
            if (!seen.contains(wholeName)) {
                seen.add(wholeName);
                dedup.add(orderByElement);
            }
        }
        return dedup;
    }

    /**
     * Creates a physical join operator from the logical join operator. It also
     * utilizes the config file
     * to choose which kind of join operator to create.
     *
     * @param operator The logical join operator acting as a blueprint.
     * @return The physical join operator corresponding to the logical join
     * operator.
     */
    public Operator visit(OldLogicalJoinOperator operator) {
        Operator leftChild = constructPhysical(operator.getLeftChild());
        Operator rightChild = constructPhysical(operator.getRightChild());

        if (canUseSMJ(operator.getJoinCondition())) {
            String rightTableName;
            if (operator.getRightChild() instanceof LogicalScanOperator) {
                rightTableName = ((LogicalScanOperator) operator.getRightChild()).getTableName();
            } else {
                rightTableName = ((LogicalSelectionOperator) operator.getRightChild()).getTableName();
            }
            List<List<OrderByElement>> orders = getOrders(operator.getJoinCondition(), rightTableName);
            List<OrderByElement> leftOrder = orders.get(0);
            List<OrderByElement> rightOrder = orders.get(1);
            leftChild = generateSort(leftChild, leftOrder, leftChild.getColumnMap());
            rightChild = generateSort(rightChild, rightOrder, rightChild.getColumnMap());
            SMJOperator smjOperator = new SMJOperator(leftChild, rightChild, operator.getJoinCondition(),
                    operator.getJoinExpressionVisitor(), operator.getOriginalJoinOrder());
            smjOperator.setLeftSortOrder(leftOrder);
            smjOperator.setRightSortOrder(rightOrder);
            return smjOperator;
        } else {
            return new BNLJoinOperator(leftChild, rightChild, operator.getJoinCondition(),
                    operator.getJoinExpressionVisitor(), BUFFER_SIZE,
                    operator.getOriginalJoinOrder());
        }
    }

    /**
     * Creates a physical join operator from the logical join operator. It also
     * utilizes the config file
     * to choose which kind of join operator to create.
     *
     * @param operator The logical join operator acting as a blueprint.
     * @return The physical join operator corresponding to the logical join
     * operator.
     */
    public Operator visit(LogicalJoinOperator operator) {
        OldLogicalJoinOperator oldLogicalJoinOperator = LogicalQueryPlanUtils.generateOldLogicalJoinTree(operator,
                operator.getAliasMap());

        Operator leftChild = constructPhysical(oldLogicalJoinOperator.getLeftChild());
        Operator rightChild = constructPhysical(oldLogicalJoinOperator.getRightChild());
        if (canUseSMJ(oldLogicalJoinOperator.getJoinCondition())) {
            String rightTableName;
            if (oldLogicalJoinOperator.getRightChild() instanceof LogicalScanOperator) {
                rightTableName = ((LogicalScanOperator) oldLogicalJoinOperator.getRightChild()).getTableName();
            } else {
                rightTableName = ((LogicalSelectionOperator) oldLogicalJoinOperator.getRightChild()).getTableName();
            }
            List<List<OrderByElement>> orders = getOrders(oldLogicalJoinOperator.getJoinCondition(), rightTableName);
            List<OrderByElement> leftOrder = orders.get(0);
            List<OrderByElement> rightOrder = orders.get(1);
            leftChild = generateSort(leftChild, leftOrder, leftChild.getColumnMap());
            rightChild = generateSort(rightChild, rightOrder, rightChild.getColumnMap());
            SMJOperator smjOperator = new SMJOperator(leftChild, rightChild, oldLogicalJoinOperator.getJoinCondition(),
                    oldLogicalJoinOperator.getJoinExpressionVisitor(), oldLogicalJoinOperator.getOriginalJoinOrder());
            smjOperator.setLeftSortOrder(leftOrder);
            smjOperator.setRightSortOrder(rightOrder);
            return smjOperator;
        } else {
            return new BNLJoinOperator(leftChild, rightChild, operator.getJoinCondition(),
                    oldLogicalJoinOperator.getJoinExpressionVisitor(), BUFFER_SIZE,
                    oldLogicalJoinOperator.getOriginalJoinOrder());
        }
    }

    /**
     * Returns a two element list; the second element is the list of columns as an
     * OrderByElement that represent
     * the rightmost column and the first element of the list is a list of all other
     * columns.
     *
     * @param joinCondition The expression for the join node
     * @return The two element list containing the columns as stated above
     */
    public List<List<OrderByElement>> getOrders(Expression joinCondition, String rightName) {
        ArrayList<List<OrderByElement>> list = new ArrayList<>();
        List<OrderByElement> leftList = new ArrayList<>();
        Set<String> leftSet = new HashSet<>();
        List<OrderByElement> rightList = new ArrayList<>();
        Set<String> rightSet = new HashSet<>();
        list.add(leftList);
        list.add(rightList);
        Stack<Expression> stackExpression = new Stack<>();
        stackExpression.push(joinCondition);
        while (stackExpression.size() != 0) {
            Expression exp = stackExpression.pop();
            if (exp instanceof EqualsTo) {
                Column leftColumn = (Column) ((EqualsTo) exp).getLeftExpression();
                Column rightColumn = (Column) ((EqualsTo) exp).getRightExpression();
                String rightTableName = rightColumn.getWholeColumnName().split("\\.")[0];

                if (rightTableName.equals(rightName)) {
                    OrderByElement leftOrder = new OrderByElement();
                    leftOrder.setExpression(((EqualsTo) exp).getLeftExpression());
                    OrderByElement rightOrder = new OrderByElement();
                    rightOrder.setExpression(((EqualsTo) exp).getRightExpression());
                    leftList.add(leftOrder);
                    rightList.add(rightOrder);
                } else {
                    OrderByElement leftOrder = new OrderByElement();
                    leftOrder.setExpression(((EqualsTo) exp).getLeftExpression());
                    OrderByElement rightOrder = new OrderByElement();
                    rightOrder.setExpression(((EqualsTo) exp).getRightExpression());
                    leftList.add(rightOrder);
                    rightList.add(leftOrder);
                }
            } else {
                AndExpression andExp = (AndExpression) exp;
                stackExpression.add(andExp.getRightExpression());
                stackExpression.add(andExp.getLeftExpression());
            }
        }
        return list;
    }

    /**
     * Creates a physical sort operator from the logical sort operator. It also
     * utilizes the config file
     * to choose which kind of sort operator to create.
     *
     * @param operator The logical sort operator acting as a blueprint.
     * @return The physical sort operator corresponding to the logical sort
     * operator.
     */
    public Operator visit(LogicalSortOperator operator) {
        Operator child = constructPhysical(operator.getChild());
        return generateSort(child, operator.getOrderByElements(), operator.getSortColumnMap());
    }

    /**
     * Creates a physical DuplicateElimination operator from the logical
     * DuplicateElimination operator.
     *
     * @param operator The logical DuplicateElimination operator acting as a
     *                 blueprint.
     * @return The physical DuplicateElimination operator corresponding to the
     * logical DuplicateElimination operator.
     */
    public Operator visit(LogicalDuplicateEliminationOperator operator) {
        Operator child = constructPhysical(operator.getChild());
        return new DuplicateEliminationOperator(child);
    }

    /**
     * Returns the sort operator based on the child and order of sorting.
     *
     * @param child The child node for the sort operator that's being created
     * @param order The order in which we will sort our operator
     * @return The new sort operator
     */
    private Operator generateSort(Operator child, List<OrderByElement> order, Map<String, Integer> sortColumnMap) {
        return new ExternalSortOperator(child, sortColumnMap, order, Interpreter.getTempdir(),
                BUFFER_SIZE);
    }

    /**
     * Return true if and only if the join condition is not empty and has purely
     * equality comparisons
     *
     * @param joinCondition
     * @return true if and only if the join condition is not empty and has purely
     * equality comparisons
     */
    private boolean canUseSMJ(Expression joinCondition) {
        Stack<BinaryExpression> expressions = LogicalQueryPlanUtils.getExpressions(joinCondition);
        if (expressions == null || expressions.size() == 0) {
            return false;
        }

        return expressions.stream().allMatch(exp -> (exp instanceof EqualsTo));
    }
}
