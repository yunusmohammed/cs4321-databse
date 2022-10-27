package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;

import net.sf.jsqlparser.schema.Table;

public class IndexScanOperator extends ScanOperator {

  /**
   * Constructor that initialises a IndexScanOperator to get tuples from lowKey to
   * highKey from a prebuilt B+ tree index. The tuples produced by this operator
   * are those whose key attribute values >= lowKey and <= highKey
   *
   * @param table            The table in the database the IndexScanOperator is
   *                         scanning
   * @param aliasMap         The mapping from table names to base table names
   * @param indexFileName    The filename of a serialized B+ tree index on some
   *                         key
   *                         (single attribute)
   * @param lowKey           The lowest key value such that all produced tuples
   *                         have
   *                         key attribute value of at least this key
   * @param highKey          The highest key value such that all produced tuples
   *                         have
   *                         key attribute value of at most this key
   * @param indexIsClustered True if and only if the index pointed to by
   *                         indexFileName is clustered
   */
  public IndexScanOperator(Table table, AliasMap aliasMap, String indexFileName, Integer lowKey, Integer highKey,
      boolean indexIsClustered) {
    super(table, aliasMap);
  }

  /**
   * Gets the next tuple of the IndexScanOperator’s output
   *
   * @return The next tuple of the IndexScanOperator’s output
   */
  @Override
  public Tuple getNextTuple() {
    return null;
  }

  /**
   * Resets the Table Index of the next tuple of the
   * ScanOperator’s output to the beginning of the table
   */
  @Override
  public void reset() {
  }

  /**
   * Closes the initialised BufferedReader
   */
  @Override
  public void finalize() {
    super.finalize();
  }

  /**
   * Returns the string representation of the Index Scan Operator
   *
   * @return The string representation of the Index Scan Operator
   *         Eg:
   *         IndexScanOperator{baseTablePath='../src/test/resources/input_binary/db/data/Boats'}
   */
  @Override
  public String toString() {
    return "IndexScanOperator{" +
        "baseTablePath='" + baseTablePath + '\'' +
        '}';
  }
}
