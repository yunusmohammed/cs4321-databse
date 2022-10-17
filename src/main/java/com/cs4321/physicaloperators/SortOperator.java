package com.cs4321.physicaloperators;

import com.cs4321.app.SortingUtilities;
import com.cs4321.app.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Operator for handling in-memory sorting for order by clauses
 * @author Yohanes
 */
public class SortOperator extends Operator {

    private Operator child;
    private Map<String, Integer> columnMap;
    private List<OrderByElement> orderByElementList;
    private PriorityQueue<Tuple> tuples;

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
            tuples = new PriorityQueue<>((a, b) -> SortingUtilities.compare(a, b, orderByElementList, columnMap));
            Tuple nextTuple = child.getNextTuple();
            while (nextTuple != null) {
                tuples.add(nextTuple);
                nextTuple = child.getNextTuple();
            }
        }
        if (tuples.size() == 0)
            return null;
        return tuples.poll();
    }

    /**
     * Resets the operator so that it can read tuples from the beginning.
     */
    @Override
    public void reset() {
        tuples = null;
        child.reset();
    }

    @Override
    public String toString() {
        return "";
    }

    /**
     * Closes the query so that there cannot be more calls to getNextTuple.
     */
    @Override
    public void finalize() {
        child.finalize();
    }

}
