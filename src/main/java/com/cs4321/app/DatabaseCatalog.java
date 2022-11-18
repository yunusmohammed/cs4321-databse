/**
 *
 */
package com.cs4321.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * The Database Catalog gives information related to specific tables such as
 * their file location and schema.
 *
 * @author Yohanes
 */
public class DatabaseCatalog {

    private static String inputdir;
    private static String sep = File.separator;
    private static DatabaseCatalog instance;

    /**
     * Maps tables to table metadata containing information about the
     * minimum and the maximum value that each column takes, and the
     * total number of tables. Example: <br>
     * { Eritrea :
     *  < numberOfTuples: 500, <br>
     *   [ ColumnStatsInfo< columnName: A, minValue: 0, maxValue: 3000 >,
     *     ColumnStatsInfo< columnName: B, minValue: 0, maxValue: 5000 > ]
     *  >
     * }
     */
    private static Map<String, TableStatsInfo> tableStatsMap;

    /**
     * schemaMap maps table names to a pair where the key contains the table schema
     * and the value is a map
     * from column names in the schema to their index.
     */
    private static HashMap<String, Map.Entry<String[], HashMap<String, Integer>>> schemaMap;

    /**
     * IndexColumns maps table names to a set of all the column names which belong
     * to an index in the database.
     */
    private static HashMap<String, HashSet<String>> indexColumns;

    /**
     * Private constructor to follow the singleton pattern.
     */
    private DatabaseCatalog() {
    }

    /**
     * Sets the absolute path to the input directory. The input directory must be
     * set before the database catalog is accessed.
     *
     * @param inputdirectory - The absolute path to the directory containing the
     *                       queries, schema, and data.
     */
    public static void setInputDir(String inputdirectory) {
        inputdir = inputdirectory;
    }

    /**
     * Initializes and returns the database catalog singleton.
     *
     * @return- The database catalog singleton.
     */
    public static DatabaseCatalog getInstance() {
        initSchemaMap();
        if (DatabaseCatalog.instance == null)
            DatabaseCatalog.instance = new DatabaseCatalog();
        return DatabaseCatalog.instance;
    }

    /**
     * Gets the input directory.
     *
     * @return The input directory.
     */
    public static String getInputdir() {
        return inputdir;
    }

    /**
     * Returns the absolute path to the specified table. Does not verify if the
     * given table exists.
     *
     * @param table - A string of the specified table.
     * @return A string of the absolute path to the table.
     */
    public String tablePath(String table) {
        return getInputdir() + sep + "db" + sep + "data" + sep + table;
    }

    /**
     * Returns a list of Strings of each row in a given file.
     *
     * @param path- The absolute path to the file to be read from. Requires: the
     *              file exists in the given path.
     * @return- A list of Strings containing the content in a given file. If the
     *          file does not exist, an empty list will be returned.
     */
    public static List<String> readFile(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Unable to read file at " + path);
        }
        return new ArrayList<String>();
    }

    /**
     * Initializes schemaMap if not previously done before.
     */
    private static void initSchemaMap() {
        if (schemaMap == null) {
            try {
                Files.deleteIfExists(Path.of(getInputdir() + sep + "db" + sep + "stats.txt"));
            } catch (IOException e) {
                Logger.getInstance().log(e.getMessage());
            }
            File statsFile = new File(getInputdir() + sep + "db" + sep + "stats.txt");
            schemaMap = new HashMap<>();
            tableStatsMap = new HashMap<>();
            List<String> tableSchemas = readFile(getInputdir() + sep + "db" + sep + "schema.txt");
            for (String tableSchema : tableSchemas) {
                String[] columns = tableSchema.split(" ");
                HashMap<String, Integer> columnIndex = new HashMap<>();
                List<ColumnStatsInfo> columnStatsInfoList = new ArrayList<>();
                for (int i = 1; i < columns.length; i++) {
                    columnIndex.put(columns[i], i - 1);
                    columnStatsInfoList.add(new ColumnStatsInfo(columns[i]));
                }
                tableStatsMap.put(columns[0],  new TableStatsInfo(columnStatsInfoList, columns[0]));
                DatabaseCatalog.schemaMap.put(columns[0], new SimpleEntry<>(columns, columnIndex));
                Stats stats = new Stats(columns[0], tableStatsMap.get(columns[0]));
                stats.generateStatistics(statsFile);
            }
        }
    }

    /**
     * Returns a string array containing the schema for a given table. The function
     * will initialize schemaMap if not done
     * previously.
     *
     * @param table - A string with the name of the table for which we are returning
     *              the schema.
     * @return A string array containing the name of the table followed by the
     *         columns- returns an empty array if the table doesn't exist.
     */
    public String[] tableSchema(String table) {
        if (DatabaseCatalog.schemaMap.containsKey(table))
            return DatabaseCatalog.schemaMap.get(table).getKey();
        System.out.println("Table " + table + " does not exist.");
        return new String[0];
    }

    /**
     * Returns a map which maps the columns of the given table to their index in a
     * tuple. The function will initialize schemaMap if not done
     * previously.
     *
     * @param table- A string with the name of the table for which we are returning
     *               the map.
     * @return- A HashMap which maps the name of columns in the table to their
     *          corresponding index- returns an empty map if the table doesn't
     *          exist.
     */
    public HashMap<String, Integer> columnMap(String table) {
        if (DatabaseCatalog.schemaMap.containsKey(table))
            return DatabaseCatalog.schemaMap.get(table).getValue();
        System.out.println("Table " + table + " does not exist.");
        return new HashMap<>();
    }

    /**
     * indexColumns maps table names to a set of all the column names which belong
     * to an index in the database.
     * 
     * @return - indexColumns
     */
    public HashMap<String, HashSet<String>> getIndexColumns() {
        if (indexColumns == null) {
            indexColumns = new HashMap<>();
            IndexInfoConfig indexInfoConfig = new IndexInfoConfig(DatabaseCatalog.getInputdir()
                    + File.separator + "db" + File.separator + "index_info.txt");
            Map<String, IndexInfo> indexInfoConfigMap = indexInfoConfig.getIndexInfoMap();
            for (IndexInfo indexInfo : indexInfoConfigMap.values()) {
                String table = indexInfo.getRelationName();
                HashSet<String> columns = indexColumns.getOrDefault(table, new HashSet<>());
                columns.add(indexInfo.getAttributeName());
                indexColumns.put(table, columns);
            }
        }
        return indexColumns;
    }

    /**
     * Returns the table stats of all tables in the database
     * @return the table stats of all tables in the database
     */
    public Map<String, TableStatsInfo> getTableStatsMap() {
        return tableStatsMap;
    }
}
