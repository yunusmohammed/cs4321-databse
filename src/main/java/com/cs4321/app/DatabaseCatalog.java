/**
 *
 */
package com.cs4321.app;

import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The Database Catalog gives information related to specific tables such as their file location and schema.
 * @author Yohanes
 *
 */
public class DatabaseCatalog {

	private static String inputdir;
	private static String sep = File.separator;
	private static DatabaseCatalog instance;
	private static HashMap<String, String[]> schemaMap;
	private static HashMap<String, List<Tuple>> tableMap;

	/**
	 * Private constructor to follow the singleton pattern.
	 */
	private DatabaseCatalog() {
		DatabaseCatalog.schemaMap = new HashMap<String, String[]>();
		DatabaseCatalog.tableMap = new HashMap<String, List<Tuple>>();
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
	 * Returns a list of tuples containing the data from a given table.
	 * @param table- The name of the table we want to read data from.
	 * @return- A list of tuples containing the data from the table.
	 */
	public List<Tuple> getTable(String table) {
		if(DatabaseCatalog.tableMap.containsKey(table)) return DatabaseCatalog.tableMap.get(table);
		List<String> tableContents = readFile(tablePath(table));
		List<Tuple> rows = new ArrayList<>();
		for(String row : tableContents) {
			rows.add(new Tuple(row));
		}
		DatabaseCatalog.tableMap.put(table, rows);
		return rows;
	}

	/**
	 * Returns a string array containing the schema for a given table.
	 * @param table - A string with the name of the table for which we are returning the schema.
	 * @return A string array containing the name of the table followed by the columns- returns an empty array if the table does not exist.
	 */
	public String[] tableSchema(String table) {
		if(DatabaseCatalog.schemaMap.containsKey(table)) return DatabaseCatalog.schemaMap.get(table);
		List<String> tableSchemas = readFile(DatabaseCatalog.inputdir + sep + "db" + sep + "schema.txt");
		for(String tableSchema : tableSchemas) {
			String[] columns = tableSchema.split(" ");
			DatabaseCatalog.schemaMap.put(columns[0], columns);
		}
		return DatabaseCatalog.schemaMap.getOrDefault(table, new String[0]);
	}
}