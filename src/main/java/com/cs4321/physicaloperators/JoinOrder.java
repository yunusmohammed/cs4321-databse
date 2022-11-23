package com.cs4321.physicaloperators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public class JoinOrder {
    
    /**
     * Selects a join order to optimize plan costs
     * @param joinChildren - children of the join operator
     * @return a list of operators in the order for the join
     */
    public static List<Operator> getJoinOrder(List<Operator> joinChildren) {
        // each subset stores the indexes of the children in joinChildren
        List<HashSet<Integer>> subsets = generateSubsets(joinChildren.size());
        HashMap<HashSet<Integer>, Map.Entry<Integer, List<Integer>>> map = new HashMap<>();
        for(int i=0; i<subsets.size(); i++) {
            HashSet<Integer> set = subsets.get(i);
            if(set.size() == 1) {
                List<Integer> ordering = new ArrayList<>(set);
                map.put(set, new SimpleEntry<>( 0, ordering));
            }
        }
        
    }

    /**
     * Returns all subsets of the set {0, 1, ..., n-1} besides the empty set in increasing order of subset size. Requires: n >= 1
     * @param n - the size of the set to generate subsets from
     * @return a list of subsets
     */
    private static List<HashSet<Integer>> generateSubsets(int n) {
        List<HashSet<Integer>> subsets = new ArrayList<>(new HashSet<>());
        for(int i=0; i<n; i++) {
            List<HashSet<Integer>> updatedSubsets = new ArrayList<>();
            for(HashSet<Integer> set : subsets) {
                HashSet<Integer> addI = new HashSet<>(set);
                addI.add(i);
                updatedSubsets.add(addI);
                updatedSubsets.add(set);
            }
            subsets = updatedSubsets;
        }
        // remove the empty set
        subsets.remove(subsets.size()-1);

        // sort in increasing order of subset size
        Collections.sort(subsets, (a, b) -> a.size() - b.size());
        return subsets;
    }
}
