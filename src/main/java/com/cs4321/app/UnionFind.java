package com.cs4321.app;

import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The UnionFind class is built upon the union find algorithm to allow for
 * effectively combining constraints such that
 * there's improved performance regarding the pushing of selections.
 *
 * @author Lenhard Thomas
 */
public class UnionFind {

    /**
     * Mapping from attribute (using the table name and favoring aliases) to union
     * find element.
     */
    private final Map<String, UnionFindElement> elements;

    /**
     * Creates the union find data structure.
     */
    public UnionFind() {
        elements = new HashMap<>();
    }

    /**
     * Returns the union find element corresponding to this element, and if none
     * exist, it creates a new one to return.
     *
     * @param attribute The attribute to search for within the data structure
     * @return The union find element corresponding to this attribute.
     */
    public UnionFindElement find(Column attribute) {
        String attributeName = attribute.getWholeColumnName();
        if (!elements.containsKey(attributeName)) {
            UnionFindElement element = new UnionFindElement(attribute);
            elements.put(attributeName, element);
            return element;
        }
        UnionFindElement element = elements.get(attributeName);
        if (element.getParent() == null) {
            return element;
        }
        return find(element.getParent());
    }

    /**
     * Merges the union find elements corresponding to attribute1 and attribute2;
     * favoring the one with more elements and
     * in the event of a tie, it favors attribute1.
     *
     * @param attribute1 The first attribute being merged.
     * @param attribute2 The second attribute being merged.
     */
    public void union(Column attribute1, Column attribute2) {
        UnionFindElement element1 = find(attribute1);
        UnionFindElement element2 = find(attribute2);

        if (element2.getAttributes().size() > element1.getAttributes().size()) {
            element1.setParent(attribute2);
            for (Column attribute : element1.getAttributes()) {
                element2.addAttribute(attribute);
            }
            if (element1.getLowerBound() != null)
                element2.setLowerBound(element1.getLowerBound());
            if (element1.getUpperBound() != null)
                element2.setUpperBound(element1.getUpperBound());
        } else {
            element2.setParent(attribute1);
            for (Column attribute : element2.getAttributes()) {
                element1.addAttribute(attribute);
            }
            if (element2.getLowerBound() != null)
                element1.setLowerBound(element2.getLowerBound());
            if (element2.getUpperBound() != null)
                element1.setUpperBound(element2.getUpperBound());
        }
    }

    /**
     * Gets the distinct union find elements.
     *
     * @return The distinct union find elements.
     */
    public List<UnionFindElement> getCollections() {
        List<UnionFindElement> lst = new ArrayList<>();
        for (UnionFindElement element : elements.values()) {
            if (element.getParent() == null) {
                lst.add(element);
            }
        }
        return lst;
    }

    @Override
    public String toString() {
        List<UnionFindElement> collections = getCollections();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < collections.size(); i++) {
            builder.append(collections.get(i).toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
