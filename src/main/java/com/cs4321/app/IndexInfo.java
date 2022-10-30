package com.cs4321.app;

public class IndexInfo {
    private String relationName;
    private String attributeName;
    private boolean clustered;
    private int order;

    public IndexInfo(String relationName, String attributeName, boolean clustered, int order) {
        this.relationName = relationName;
        this.attributeName = attributeName;
        this.clustered = clustered;
        this.order = order;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isClustered() {
        return clustered;
    }

    public int getOrder() {
        return order;
    }
}