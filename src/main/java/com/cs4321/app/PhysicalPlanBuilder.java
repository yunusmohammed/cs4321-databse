package com.cs4321.app;

import com.cs4321.logicaloperators.*;
import com.cs4321.physicaloperators.*;

import java.io.File;

/**
 * The PhysicalPlanBuilder is a singleton that provides information about how to
 * create a physical tree from a logical tree.
 *
 * @author Lenhard Thomas
 */
public class PhysicalPlanBuilder {

    private static BuilderConfig config;
    private static PhysicalPlanBuilder instance;
    private static boolean humanReadable;

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
     * Creates a physical scan operator from the logical scan operator.
     *
     * @param operator The logical scan operator acting as a blueprint.
     * @return The physical scan operator corresponding to the logical scan
     *         operator.
     */
    public Operator visit(LogicalScanOperator operator) {
        return new ScanOperator(operator.getBaseTable(), humanReadable);
    }

    /**
     * Creates a physical selection operator from the logical selection operator.
     *
     * @param operator The logical selection operator acting as a blueprint.
     * @return The physical selection operator corresponding to the logical
     *         selection operator.
     */
    public Operator visit(LogicalSelectionOperator operator) {
        ScanOperator child = (ScanOperator) constructPhysical(operator.getChild());
        return new SelectionOperator(operator.getSelectExpressionVisitor(), operator.getColumnMap(),
                operator.getSelectCondition(), child);
    }

    /**
     * Creates a physical projection operator from the logical projection operator.
     *
     * @param operator The logical projection operator acting as a blueprint.
     * @return The physical projection operator corresponding to the logical
     *         projection operator.
     */
    public Operator visit(LogicalProjectionOperator operator) {
        Operator child = constructPhysical(operator.getChild());
        return new ProjectionOperator(operator.getColumnMap(), operator.getSelectItems(), child);
    }

    /**
     * Creates a physical join operator from the logical join operator. It also
     * utilizes the config file
     * to choose which kind of join operator to create.
     *
     * @param operator The logical join operator acting as a blueprint.
     * @return The physical join operator corresponding to the logical join
     *         operator.
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
                // TODO: SMJ
                return null;
        }
        // This scenario should never happen
        return null;
    }

    /**
     * Creates a physical sort operator from the logical sort operator. It also
     * utilizes the config file
     * to choose which kind of sort operator to create.
     *
     * @param operator The logical sort operator acting as a blueprint.
     * @return The physical sort operator corresponding to the logical sort
     *         operator.
     */
    public Operator visit(LogicalSortOperator operator) {
        Operator child = constructPhysical(operator.getChild());
        switch (config.getSortType()) {
            case MEMORY:
                return new SortOperator(child, operator.getSortColumnMap(), operator.getOrderByElements());
            case EXTERNAL:
                // TODO: EXTERNAL SORT
                return null;
        }
        // This scenario should never happen
        return null;
    }

    /**
     * Creates a physical DuplicateElimination operator from the logical
     * DuplicateElimination operator.
     *
     * @param operator The logical DuplicateElimination operator acting as a
     *                 blueprint.
     * @return The physical DuplicateElimination operator corresponding to the
     *         logical DuplicateElimination operator.
     */
    public Operator visit(LogicalDuplicateEliminationOperator operator) {
        Operator child = constructPhysical(operator.getChild());
        return new DuplicateEliminationOperator(child);
    }
}
