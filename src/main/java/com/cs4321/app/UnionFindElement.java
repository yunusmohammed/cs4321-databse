package com.cs4321.app;

import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * The UnionFindElement is a data class representing an element of the Selection Union Find algorithm.
 *
 * @author Lenhard Thomas
 */
public class UnionFindElement {
    private Integer lowerBound;
    private Integer upperBound;
    private Integer equalityConstraint;
    private final List<Column> attributes;
    private Column parent;


    /**
     * Creates a union find element for the corresponding attribute.
     *
     * @param attribute The attribute for which this element corresponds to
     */
    public UnionFindElement(Column attribute) {
        this.attributes = new ArrayList<>();
        this.attributes.add(attribute);
    }

    /**
     * The inclusive lower-bound of the union find element.
     *
     * @return The inclusive lower-bound of the union find element.
     */
    public Integer getLowerBound() {
        return lowerBound;
    }

    /**
     * Sets the inclusive lower-bound of the union find element; leaves it unchanged if the new lower bound is lower.
     *
     * @param lowerBound The inclusive lower-bound of the union find element.
     */
    public void setLowerBound(Integer lowerBound) {
        if (this.lowerBound == null) {
            this.lowerBound = lowerBound;
        } else {
            this.lowerBound = Math.max(this.lowerBound, lowerBound);
        }
    }

    /**
     * The inclusive upper-bound of the union find element.
     *
     * @return The inclusive upper-bound of the union find element.
     */
    public Integer getUpperBound() {
        return upperBound;
    }

    /**
     * Sets the inclusive upper-bound of the union find element.
     *
     * @param upperBound The inclusive upper-bound of the union find element; leaves it unchanged if the new upper bound is higher.
     */
    public void setUpperBound(Integer upperBound) {
        if (this.upperBound == null) {
            this.upperBound = upperBound;
        } else {
            this.upperBound = Math.min(this.upperBound, upperBound);
        }
    }

    /**
     * The equality constraint of the union find element.
     *
     * @return The equality constraint of the union find element.
     */
    public Integer getEqualityConstraint() {
        return equalityConstraint;
    }

    /**
     * Sets the equality constraint of the union find element.
     *
     * @param equalityConstraint the exact int value for the equality constraint.
     */
    public void setEqualityConstraint(Integer equalityConstraint) {
        this.lowerBound = equalityConstraint;
        this.upperBound = equalityConstraint;
        this.equalityConstraint = equalityConstraint;
    }

    /**
     * Gets the list of attributes corresponding to this union find element.
     *
     * @return The list of attributes corresponding to this union find element.
     */
    public List<Column> getAttributes() {return attributes;}

    /**
     * Adds an attribute to the list of attributes corresponding to this union find element.
     *
     * @param attribute The String attribute to add to the list of attributes for this union find element.
     */
    public void addAttribute(Column attribute) {this.attributes.add(attribute);}


    /**
     * Gets the parent of this element; null if none exists.
     *
     * @return The parent of this element.
     */
    public Column getParent() {
        return parent;
    }

    /**
     * Sets the parent of this element to [parent].
     *
     * @param parent The new parent of this element.
     */
    public void setParent(Column parent) {
        this.parent = parent;
    }

    /**
     * Whether this element has value constraints.
     *
     * @return True if there are value constraints and false otherwise.
     */
    public boolean hasConstraints() {
        return !(null == lowerBound && lowerBound == upperBound && upperBound == equalityConstraint);
    }

    /**
     * The string representation of the union find element.
     *
     * @return The string representation of the union find element.
     */
    @Override
    public String toString() {
        return "[" + attributes + ", equals " + equalityConstraint + ", min " + lowerBound + ", max " + upperBound + "]";
    }
}
