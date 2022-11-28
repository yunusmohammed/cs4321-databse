package com.cs4321.app;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The AliasMap is an abstraction that allows for Operators to easily determine the index value of a column
 * regardless of whether that columns is from a base table or an alias.
 *
 * @author Lenhard Thomas
 */
public class AliasMap {
    // Maps the name of every table to its corresponding base table
    public Map<String, String> aliasMap;

    /**
     * Creates a AliasMap that is designed to map any column to the index that column represents in a row.
     *
     * @param item      The FromItem that is listed in the 'FROM' portion of the SELECT statement
     * @param joinsList The list of joins in the SELECT statement
     */
    public AliasMap(FromItem item, List<Join> joinsList) {
        this.aliasMap = new HashMap<>();
        this.addToMap(item);
        // Place remaining tables in the alias map
        if (joinsList == null) return;
        for (Join j : joinsList) {
            this.addToMap(j.getRightItem());
        }
    }

    /**
     * Adds the item to the aliasMap as a mapping from the table alias (or table name if no alias exists) to the base table's name.
     *
     * @param item The FromItem that will be added onto aliasMap.
     */
    private void addToMap(FromItem item) {
        Table t = (Table) item;
        String itemAlias = item.getAlias();
        this.aliasMap.put(t.getName(), t.getName());
        if (itemAlias != null) {
            this.aliasMap.put(t.getAlias(), t.getName());
        }
    }

    /**
     * Returns the index that the column represents in its row.
     *
     * @param col The column
     */
    public int get(Column col) {
        String tableName = col.getTable().getName();
        String baseTableName = this.aliasMap.get(tableName);
        String columnName = col.getColumnName();
        return DatabaseCatalog.getInstance().columnMap(baseTableName).get(columnName);
    }

    /**
     * Returns the name of the base table for the corresponding table name.
     *
     * @param tableName The table name/alias to look for
     */
    public String getBaseTable(String tableName) {
        return this.aliasMap.get(tableName);
    }

    /**
     * The string representation of the column using the base table.
     *
     * @param col The column that will be represented as a string/
     * @return The string representation of the column using the base table.
     */
    public String columnWithBaseTable(Column col){
        String tableName = col.getTable().getName();
        String baseTableName = this.aliasMap.get(tableName);
        return baseTableName + "." + col.getColumnName();
    }
}
