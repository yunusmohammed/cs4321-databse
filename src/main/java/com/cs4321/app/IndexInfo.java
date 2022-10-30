package com.cs4321.app;

/**
 * Data class that stores information that specifies how indexes are built
 *
 * @author Jessica Tweneboah
 */
public class IndexInfo {
    private String relationName;
    private String attributeName;
    private boolean clustered;
    private int order;

    /**
     * Constructs the IndexInfo data class
     *
     * @param relationName  The relation name
     * @param attributeName The relation attribute name
     * @param clustered     A flag true or false depending on whether the index is
     *                      unclustered (false) or clustered (true)
     * @param order         The order of the tree
     */
    public IndexInfo(String relationName, String attributeName, boolean clustered, int order) {
        this.relationName = relationName;
        this.attributeName = attributeName;
        this.clustered = clustered;
        this.order = order;
    }

    /**
     * Returns the relation name
     *
     * @return The relation name
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * Returns the relation attribute name
     *
     * @return The relation attribute name
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Returns a flag true or false depending on whether the index is
     * unclustered (false) or clustered (true)
     *
     * @return A flag true or false depending on whether the index is
     * unclustered (false) or clustered (true)
     */
    public boolean isClustered() {
        return clustered;
    }

    /**
     * Returns the order of the tree
     *
     * @return The order of the tree
     */
    public int getOrder() {
        return order;
    }
}