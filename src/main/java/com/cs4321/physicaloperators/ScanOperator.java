package com.cs4321.physicaloperators;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.cs4321.app.AliasMap;
import com.cs4321.app.DatabaseCatalog;
import com.cs4321.app.Logger;
import com.cs4321.app.TupleReader;

import net.sf.jsqlparser.schema.Table;

/**
 * Base class for physical scan operators
 * 
 * @author Yunus (ymm26@cornell.edu)
 */
public abstract class ScanOperator extends Operator {
  protected final DatabaseCatalog dbc = DatabaseCatalog.getInstance();
  protected String baseTablePath;
  protected final String baseTableName;
  protected BufferedReader reader;
  protected TupleReader tupleReader;
  protected final Logger logger = Logger.getInstance();

  public ScanOperator(Table table, AliasMap aliasMap) {
    String tableName = table.getAlias();
    if (tableName == null)
      tableName = table.getName();
    String baseTable = aliasMap.getBaseTable(tableName);
    this.baseTableName = baseTable;
    setBaseTablePath(baseTable);
    Map<String, Integer> columnMap = new HashMap<>();
    for (Map.Entry<String, Integer> entry : DatabaseCatalog.getInstance().columnMap(baseTable).entrySet()) {
      String columnName = entry.getKey();
      Integer index = entry.getValue();
      columnMap.put(tableName + "." + columnName, index);
    }
    this.setColumnMap(columnMap);
    try {
      tupleReader = new TupleReader(getBaseTablePath());
    } catch (IOException e) {
      logger.log(e.getMessage());
    }
  }

  /**
   * Returns the baseTablePath
   *
   * @return The path to table in the database the ScanOperator is scanning
   */
  protected String getBaseTablePath() {
    return baseTablePath;
  }

  /**
   * Populates the baseTablePath field
   *
   * @param baseTable The table in the database the ScanOperator is scanning
   */
  protected void setBaseTablePath(String baseTable) {
    this.baseTablePath = dbc.tablePath(baseTable);
  }

  /**
   * Returns the string representation of the Scan Operator
   *
   * @return The string representation of the Scan Operator
   *         Eg:
   *         ScanOperator{baseTablePath='../src/test/resources/input_binary/db/data/Boats'}
   */
  @Override
  public String toString() {
    return "ScanOperator{" +
        "baseTablePath='" + baseTablePath + '\'' +
        '}';
  }

  /**
   * Closes the initialised BufferedReader
   */
  @Override
  public void finalize() {
    try {
      tupleReader.close();
      reader.close();
    } catch (IOException e) {
      logger.log(e.getMessage());
    }
  }

}
