package com.cs4321.physicaloperators;

import com.cs4321.app.SortingUtilities;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.*;

/**
 * Operator for handling in-memory sorting for order by clauses
 * @author Yohanes
 */
public class SortOperator extends Operator {

    private Operator child;
    private Map<String, Integer> columnMap;
    private List<OrderByElement> orderByElementList;
    private List<Tuple> tuples;
    private int index;

    /**
     * Creates an operator to represent an order by clause. This operator is at the
     * root of the query plan unless there is a distinct clause.
     *
     * @param child-              the rest of the query plan, besides a potential
     *                            DuplicateEliminationOperator.
     * @param columnMap-          a map from column names in the table to their
     *                            associated indexes.
     * @param orderByElementList- the list of elements for which our order by clause
     *                            will sort.
     */
    public SortOperator(Operator child, Map<String, Integer> columnMap, List<OrderByElement> orderByElementList) {
        this.child = child;
        this.columnMap = columnMap;
        this.orderByElementList = orderByElementList;
    }

    /**
     * Returns the next tuple in ascending order determined by the columns in the
     * order by clause. In order to ensure
     * the tuples are sorted, the SortOperator must read every tuple from its child.
     *
     * @return- the next tuple in sorted order.
     */
    @Override
    public Tuple getNextTuple() {
        if (tuples == null) {
            tuples = new ArrayList<>();
            Tuple nextTuple = child.getNextTuple();
            while (nextTuple != null) {
                tuples.add(nextTuple);
                nextTuple = child.getNextTuple();
            }
            Collections.sort(tuples, (a, b) -> SortingUtilities.compare(a, b, orderByElementList, columnMap));
            index = 0;
        }
        if(index < tuples.size()) return tuples.get(index++);
        return null;
    }

    /**
     * Resets the operator so that it can read tuples from the beginning.
     */
    @Override
    public void reset() {
        index = 0;
        child.reset();
    }

    /**
     * Resets the operator so that the [index]'th tuple (0 based) is returned from the next call to getNextTuple
     * @param index - the index of the next tuple we want to read (the first tuple would be 0)
     */
    public void reset(int index) {
        this.index = index;
    }

    /**
     * Returns a string representation of the sort operator and its children. The orderBy elements are also printed.
     * @return - a string representation of the sort operator
     */
    @Override
    public String toString() {
        StringBuilder orderBy = new StringBuilder();
        orderBy.append("Order By : ");
        for(int i=0; i<orderByElementList.size(); i++) {
            orderBy.append(orderByElementList.get(i).toString());
            if(i < orderByElementList.size() - 1) orderBy.append(", ");
        }
        return "SortOperator{" + child.toString() + ", " + orderBy + "}";

    }

    /**
     * Closes the query so that there cannot be more calls to getNextTuple.
     */
    @Override
    public void finalize() {
        child.finalize();
    }

}
