/**
 * 
 */
package com.cs4321.app;

import java.nio.file.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Database Catalog gives information related to specific tables such as their file location and schema.
 * @author Yohanes
 *
 */
public class DatabaseCatalog {
	
	private static String inputdir;
	private static String sep = File.separator;
	private static DatabaseCatalog instance;
	
	/**
	 *  schemaMap maps table names to a pair where the key contains the table schema and the value is a map
	 *  from column names in the schema to their index.
	 */
	private static HashMap<String, Map.Entry<String[], HashMap<String, Integer>>> schemaMap;
	
	/** 
	 * Private constructor to follow the singleton pattern.
	 */
	private DatabaseCatalog() {
		DatabaseCatalog.schemaMap = new HashMap<>();
	}
	
	/**
	 * Sets the absolute path to the input directory. The input directory must be set before the database catalog is accessed.
	 * @param inputdirectory - The absolute path to the directory containing the queries, schema, and data.
	 */
	public static void setInputDir(String inputdirectory) {
		DatabaseCatalog.inputdir = inputdirectory;
	}
	
	/**
	 * Initializes and returns the database catalog singleton.
	 * @return- The database catalog singleton.
	 */
	public static DatabaseCatalog getInstance() {
		if(DatabaseCatalog.instance == null) DatabaseCatalog.instance = new DatabaseCatalog();
		return DatabaseCatalog.instance;
	}
	
	/**
	 * Returns the absolute path to the specified table. Does not verify if the given table exists.
	 * @param table - A string of the specified table.
	 * @return A string of the absolute path to the table.
	 */
	public String tablePath(String table) {
		return DatabaseCatalog.inputdir + sep + "db" + sep + "data" + sep + table;
	}
	
	/**
	 * Returns a list of Strings of each row in a given file.
	 * @param path- The absolute path to the file to be read from. Requires: the file exists in the given path.
	 * @return- A list of Strings containing the content in a given file. If the file does not exist, an empty list will be returned.
	 */
	private List<String> readFile(String path) {
		try {
			return Files.readAllLines(Paths.get(path));
		}
		catch (IOException e) {
			System.out.println("Unable to read file at " + path);
		}
		return new ArrayList<String>();
	}
	
	/**
	 * Returns a string array containing the schema for a given table. If not previously initialized, the function will
	 * add a record for every table listed in schema.txt. Additionally, a map from column names to their index in the tuple is stored
	 * with the schema for a table.
	 * @param table - A string with the name of the table for which we are returning the schema.
	 * @return A string array containing the name of the table followed by the columns- returns an empty array if the table does not exist.
	 */
	public String[] tableSchema(String table) {
		if(DatabaseCatalog.schemaMap.containsKey(table)) return DatabaseCatalog.schemaMap.get(table).getKey();
		List<String> tableSchemas = readFile(DatabaseCatalog.inputdir + sep + "db" + sep + "schema.txt");
		for(String tableSchema : tableSchemas) {
			String[] columns = tableSchema.split(" ");
			HashMap<String, Integer> columnIndex = new HashMap<>();
			for(int i=1; i<columns.length; i++) {
				columnIndex.put(columns[i], i-1);
			}
			DatabaseCatalog.schemaMap.put(columns[0], new SimpleEntry<>(columns, columnIndex));
		}
		if(DatabaseCatalog.schemaMap.containsKey(table)) return DatabaseCatalog.schemaMap.get(table).getKey();
		System.out.println("Table " + table + " does not exist.");
		return new String[0];
	}
}
