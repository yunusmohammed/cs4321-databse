/**
 * 
 */
package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Yohanes
 *
 */
class DatabaseCatalogTest {
	
	DatabaseCatalog dbc = DatabaseCatalog.getInstance();
	static String sep = File.separator;
	static String inputdir = System.getProperty("user.dir") + File.separator + "input";
	
	@BeforeAll
	static void setup() {
		DatabaseCatalog.setInputDir(inputdir);
	}

	@Test
	void tablePathTest() {
		assertEquals(inputdir + sep + "db" + sep + "data" + sep + "Sailors.txt", dbc.tablePath("Sailors"));
		assertEquals(inputdir + sep + "db" + sep + "data" + sep + "Boats.txt", dbc.tablePath("Boats"));
		assertEquals(inputdir + sep + "db" + sep + "data" + sep + "Reserves.txt", dbc.tablePath("Reserves"));
	}
	
	@Test
	void tableSchemaTest() {
		assertArrayEquals(new String[]{"Sailors", "A", "B", "C"}, dbc.tableSchema("Sailors"));
		assertArrayEquals(new String[]{"Boats", "D", "E", "F"}, dbc.tableSchema("Boats"));
		assertArrayEquals(new String[]{"Reserves", "G", "H"}, dbc.tableSchema("Reserves"));
	}

}
