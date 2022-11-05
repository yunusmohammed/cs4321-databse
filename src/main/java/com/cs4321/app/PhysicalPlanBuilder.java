package com.cs4321.app;

import com.cs4321.logicaloperators.*;
import com.cs4321.physicaloperators.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.io.File;
import java.util.*;

/**
 * The PhysicalPlanBuilder is a singleton that provides information about how to
 * create a physical tree from a logical tree.
 *
 * @author Lenhard Thomas
 */
public class PhysicalPlanBuilder {

    private static BuilderConfig config;
    static Map<String, IndexInfo> indexInfoConfigMap;
    private static PhysicalPlanBuilder instance;
    private static boolean humanReadable;
    private static Map<String, Integer> tableOrder;

    /**
     * Private constructor to follow the singleton pattern.
     */
    private PhysicalPlanBuilder() {
    }

    /**
     * Reads the config file to determine how to construct physical tree.
     *
     * @param fileName The name of the config file
     */
    public static void setConfigs(String fileName) {
        if (config == null) {
            String filePath = DatabaseCatalog.getInputdir() + File.separator + fileName;
            config = new BuilderConfig(filePath);
            if (config.shouldUseIndexForSelection()) {
                // TODO Lenhard, Yunus
                IndexInfoConfig indexInfoConfig = new IndexInfoConfig(DatabaseCatalog.getInputdir()
                        + File.separator + "db" + File.separator + "index_info.txt");
                indexInfoConfigMap = indexInfoConfig.getIndexInfoMap();
            }
        }
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
     * Creates an index scan on `indexColumn` with the range from low to high as specified by lowHigh.
     *
     * @param operator    The selection operator being used to generate an index scan operator
     * @param indexColumn The column to create the index on
     * @param lowHigh     The low and high value for the index
     * @return A new index scan on `indexColumn` with the range from low to high as specified by lowHigh.
     */
    public IndexScanOperator generateIndexScan(LogicalSelectionOperator operator, Column indexColumn, List<Integer> lowHigh) {
        String baseTableName = operator.getAliasMap().getBaseTable(indexColumn.getTable().getName());
        String wholeBaseColumnName = String.format("%s.%s", baseTableName, indexColumn.getColumnName());
        String indexPath = DatabaseCatalog.getInputdir() + File.separator + "indexes" + File.separator + wholeBaseColumnName;
        boolean isClustered = indexInfoConfigMap.get(wholeBaseColumnName).isClustered();
        return new IndexScanOperator(indexColumn.getTable(), operator.getAliasMap(),
                indexPath, indexColumn.getColumnName(), lowHigh.get(0), lowHigh.get(1), isClustered);
    }

    /**
     * Creates a physical selection operator from the logical selection operator.
     *
     * @param operator The logical selection operator acting as a blueprint.
     * @return The physical selection operator corresponding to the logical
     * selection operator.
     */
    public Operator visit(LogicalSelectionOperator operator) {
        if (config.shouldUseIndexForSelection()) {
            IndexSelectionVisitor visitor = operator.getIndexVisitor();
            visitor.splitExpression(operator.getSelectCondition(), operator.getAliasMap(), DatabaseCatalog.getInstance());
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
            // Construct a selection using the reduced expression with the child being an index scan
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
     * Creates a physical join operator from the logical join operator. It also
     * utilizes the config file
     * to choose which kind of join operator to create.
     *
     * @param operator The logical join operator acting as a blueprint.
     * @return The physical join operator corresponding to the logical join
     * operator.
     */
    public Operator visit(LogicalJoinOperator operator) {
        Operator leftChild = constructPhysical(operator.getLeftChild());
        Operator rightChild = constructPhysical(operator.getRightChild());
        switch (config.getJoinType()) {
            case TNLJ:
                return new TNLJoinOperator(leftChild, rightChild, operator.getJoinCondition(),
                        operator.getJoinExpressionVisitor());
            case BNLJ:
                return new BNLJoinOperator(leftChild, rightChild, operator.getJoinCondition(),
                        operator.getJoinExpressionVisitor(), config.getJoinBufferSize());
            case SMJ:
                List<List<OrderByElement>> orders = getOrders(operator.getJoinCondition());
                List<OrderByElement> leftOrder = orders.get(0);
                List<OrderByElement> rightOrder = orders.get(1);
                leftChild = generateSort(leftChild, leftOrder);
                rightChild = generateSort(rightChild, rightOrder);
                SMJOperator smjOperator = new SMJOperator((SortOperator) leftChild, (SortOperator) rightChild,
                        operator.getJoinCondition(), operator.getJoinExpressionVisitor());
                smjOperator.setLeftSortOrder(leftOrder);
                smjOperator.setRightSortOrder(rightOrder);
                return smjOperator;
        }
        // This scenario should never happen
        return null;
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
    public List<List<OrderByElement>> getOrders(Expression joinCondition) {
        ArrayList<List<OrderByElement>> list = new ArrayList<>();
        List<OrderByElement> leftList = new ArrayList<>();
        List<OrderByElement> rightList = new ArrayList<>();
        list.add(leftList);
        list.add(rightList);
        Stack<Expression> stackExpression = new Stack<>();
        stackExpression.push(joinCondition);
        while (stackExpression.size() != 0) {
            Expression exp = stackExpression.pop();
            if (exp instanceof EqualsTo) {
                Column leftColumn = (Column) ((EqualsTo) exp).getLeftExpression();
                Column rightColumn = (Column) ((EqualsTo) exp).getRightExpression();
                String leftTableName = leftColumn.getWholeColumnName().split("\\.")[0];
                String rightTableName = rightColumn.getWholeColumnName().split("\\.")[0];

                if (tableOrder.get(leftTableName) < tableOrder.get(rightTableName)) {
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
        return generateSort(child, operator.getOrderByElements());
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
    private Operator generateSort(Operator child, List<OrderByElement> order) {
        switch (config.getSortType()) {
            case MEMORY:
                return new SortOperator(child, child.getColumnMap(), order);
            case EXTERNAL:
                return new ExternalSortOperator(child, child.getColumnMap(), order, Interpreter.getTempdir(),
                        config.getSortBufferSize());
        }
        // This scenario should never happen
        return null;
    }
}
