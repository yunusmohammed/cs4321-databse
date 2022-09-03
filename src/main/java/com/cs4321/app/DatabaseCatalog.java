/**
 * 
 */
package com.cs4321.app;

import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Yohanes
 *
 */
public class DatabaseCatalog {
	
	private static String inputdir;
	private static String sep = File.separator;
	private static DatabaseCatalog instance;
	
	/** 
	 * Private constructor to follow the singleton pattern.
	 */
	private DatabaseCatalog() {}
	
	/**
	 * Sets the absolute path to the input directory. The input directory must be set before the database catalog is accessed.
	 * @param inputdirectory - the absolute path to the directory containing the queries, schema, and data
	 */
	public static void setInputDir(String inputdirectory) {
		inputdir = inputdirectory;
	}
	
	/**
	 * Initializes and returns the database catalog singleton.
	 * @return the database catalog singleton
	 */
	public static DatabaseCatalog getInstance() {
		if(instance == null) instance = new DatabaseCatalog();
		return instance;
	}
	
	/**
	 * Returns the absolute path to the specified table. Does not verify if the given table exists.
	 * @param table - a string of the specified table
	 * @return a string of the absolute path to the table
	 */
	public String tablePath(String table) {
		return inputdir + sep + "db" + sep + "data" + sep + table + ".txt";
	}
	
	/**
	 * Returns a string array containing the schema for a given table.
	 * @param table - a string with the name of the table for which we are returning the schema
	 * @return a string array containing the name of the table followed by the columns- throws an error if the table does not exist
	 */
	public String[] tableSchema(String table) {
		try {
			List<String> tableSchemas = Files.readAllLines(Paths.get(inputdir + sep + "db" + sep + "schema.txt"));
			for(String tableSchema : tableSchemas) {
				String[] columns = tableSchema.split(" ");
				if(columns[0].equals(table)) return columns;
			}
		}
		catch (Exception e) {
			System.out.println("Unable to read from table" + table);
		}
		throw new Error("Table not found");
	}
}
