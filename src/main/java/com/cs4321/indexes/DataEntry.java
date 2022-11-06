package com.cs4321.indexes;

import java.util.ArrayList;
import java.util.List;

/**
 * Leaf nodes contain data entries of the form < key, list >
 * where key is the (integer) search key for the index and
 * list is a list of record ids (rids).
 */
public class DataEntry {
  List<Rid> rids = new ArrayList<>();
  int key;
  int numberOfRids;

  /**
   * Initialises a new DataEntry object
   *
   * @param key is the (integer) search key for the index
   */
  DataEntry(int key) {
    this.key = key;
  }

  /**
   * Initialises a new DataEntry object
   *
   * @param key  is the (integer) search key for the index
   * @param rids is a list of record ids (rids)
   */
  public DataEntry(int key, List<Rid> rids) {
    this(key);
    this.rids = rids;
  }

  /**
   * Returns the number of record ids in this DataEntry
   *
   * @return the number of record ids in this DataEntry
   */
  public int getNumberOfRids() {
    return numberOfRids;
  }

  /**
   * Adds rid to the list of record ids
   *
   * @param rid is a record id
   */
  public void addRid(Rid rid) {
    this.rids.add(rid);
    numberOfRids++;
  }

  /**
   * Returns the list of record ids
   *
   * @return the list of record ids
   */
  public List<Rid> getRids() {
    return rids;
  }

  /**
   * Returns the search key for the index
   *
   * @return the search key for the index
   */
  public int getKey() {
    return key;
  }

  /**
   * Returns the string representation of the DataEntry
   *
   * @return The string representation of the DataEntry
   */
  @Override
  public String toString() {
    return "<[" + key + ":" + rids.toString() + "\n";
  }
}
