package com.cs4321.physicaloperators;

import com.cs4321.app.Tuple;
import com.cs4321.physicaloperators.Operator;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.*;

public class SortOperator extends Operator {

    private Operator child;
    private HashMap<String, Integer> columnMap;
    private List<OrderByElement> orderByElementList;
    private PriorityQueue<Tuple> tuples;

    /**
     * Creates an operator to represent an order by clause. This operator is at the root of the query plan unless there is a distinct clause.
     * @param child- the rest of the query plan, besides a potential DuplicateEliminationOperator.
     * @param columnMap- a map from column names in the table to their associated indexes.
     * @param orderByElementList- the list of elements for which our order by clause will sort.
     */
    public SortOperator(Operator child, HashMap<String, Integer> columnMap, List<OrderByElement> orderByElementList) {
        this.child = child;
        this.columnMap = columnMap;
        this.orderByElementList = orderByElementList;
    }

    /**
     * Returns the next tuple in ascending order determined by the columns in the order by clause. In order to ensure
     * the tuples are sorted, the SortOperator must read every tuple from its child.
     * @return- the next tuple in sorted order.
     */
    @Override
    public Tuple getNextTuple() {
        if(tuples == null) {
            tuples = new PriorityQueue<>((a, b) -> compare(a, b));
            Tuple nextTuple = child.getNextTuple();
            while(nextTuple != null) {
                tuples.add(nextTuple);
                nextTuple = child.getNextTuple();
            }
        }
        if(tuples.size() == 0) return null;
        return tuples.poll();
    }

    /**
     * Compares Tuples [a] and [b] and returns a negative integer if [a] should be placed before [b] in sorted order,
     * returns a positive integer if [b] should be placed before [a] in sorted order, and returns 0 if [a] and [b] are equal.
     * @param a- the Tuple to compare [b] with
     * @param b- the Tuple to compare [a] with
     * @return- an integer in accordance to the rules mentioned above
     */
    private int compare(Tuple a, Tuple b) {
        HashSet<Integer> seenColumns = new HashSet<>();
        if(orderByElementList != null) {
            for(OrderByElement o : orderByElementList) {
                int index = columnMap.get(o.toString());
                seenColumns.add(index);
                int aVal = a.get(index), bVal = b.get(index);
                if(aVal != bVal) return aVal - bVal;
            }
        }
        for(int i=0; i<a.size(); i++) {
            if(!seenColumns.contains(i)) {
                int aVal = a.get(i), bVal = b.get(i);
                if(aVal != bVal) return aVal - bVal;
            }
        }
        return 0;
    }

    /**
     * Resets the operator so that it can read tuples from the beginning.
     */
    @Override
    public void reset() {
        tuples = null;
        child.reset();
    }


    /**
     * Closes the query so that there cannot be more calls to getNextTuple.
     */
    @Override
    public void finalize() {
        child.finalize();
    }

}
