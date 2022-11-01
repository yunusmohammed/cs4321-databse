package com.cs4321.indexes;

/**
 * A rid is a tuple identifier and has the form (pageid, tupleid) where pageid
 * is the number of the page the tuple is on, and tupleid is the number of the
 * tuple on the page numbered pageid. For the purpose of rids, we number both
 * pages and tuples within pages starting at 0.
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
   * Get the pageId of this DataEntry
   * 
   * @return the pageId of this DataEntry
   */
  public int getPageId() {
    return this.pageId;
  }

  /**
   * Get the tupleId of this DataEntry
   * 
   * @return the tupleId of this DataEntry
   */
  public int getTupleId() {
    return this.tupleId;
  }

  /**
   * Returns the string representation of the Rid
   *
   * @return The string representation of the Rid
   */
  @Override
  public String toString() {
    return "Rid{" +
        "pageId=" + pageId +
        ", tupleId=" + tupleId +
        '}';
  }
}
