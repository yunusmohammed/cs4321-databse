package com.cs4321.indexes;

/**
 * A rid is a tuple identifier and has the form (pageid, tupleid) where pageid
 * number of the page the tuple is on, and tupleid is the number of the tuple on the
 * page numbered pageid. For the purpose of rids, we number both pages and tuples
 * ithin pages starting at 0.
 */
public class Rid {
    int pageId;
    int tupleId;

    /**
     * Initialises a new Rid object
     *
     * @param pageId  is the number of the page the tuple is on
     * @param tupleId is the number of the tuple on the page numbered pageid
     */
    public Rid(int pageId, int tupleId) {
        this.pageId = pageId;
        this.tupleId = tupleId;
    }

    /**
     * Returns pageId - the number of the page the tuple is on
     *
     * @return pageId - the number of the page the tuple is on
     */
    public int getPageId() {
        return pageId;
    }

    /**
     * Returns the tupleId - the number of the tuple on the page numbered pageid
     *
     * @return tupleId - the number of the tuple on the page numbered pageid
     */
    public int getTupleId() {
        return tupleId;
    }

    /**
     * Returns the string representation of the Rid
     *
     * @return The string representation of the Rid
     */
    @Override
    public String toString() {
        return "(" + pageId + "," + tupleId + ")";
    }
}

  
  
  
  

  