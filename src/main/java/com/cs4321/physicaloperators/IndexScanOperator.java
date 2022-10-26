package com.cs4321.physicaloperators;

import com.cs4321.app.AliasMap;
import com.cs4321.app.Tuple;

import net.sf.jsqlparser.schema.Table;

public class IndexScanOperator extends ScanOperator {

  /**
   * Constructor that initialises a IndexScanOperator
   *
   * @param table    The table in the database the IndexScanOperator is scanning
   * @param aliasMap The mapping from table names to base table names
   */
  public IndexScanOperator(Table table, AliasMap aliasMap) {
    super(table, aliasMap);
  }

  /**
   * Gets the next tuple of the ScanOperator’s output
   *
   * @return The next tuple of the ScanOperator’s output
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
