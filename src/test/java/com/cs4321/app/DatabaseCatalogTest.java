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
	static String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources" + sep + "input";
	
	@BeforeAll
	static void setup() {
		DatabaseCatalog.setInputDir(inputdir);
	}

	@Test
	void tablePathTest() {
		// sailors path
		assertEquals(inputdir + sep + "db" + sep + "data" + sep + "Sailors", dbc.tablePath("Sailors"));
		// boats path
		assertEquals(inputdir + sep + "db" + sep + "data" + sep + "Boats", dbc.tablePath("Boats"));
		// reserves path
		assertEquals(inputdir + sep + "db" + sep + "data" + sep + "Reserves", dbc.tablePath("Reserves"));
	}
	
	@Test
	void tableSchemaTest() {
		// sailors schema
		assertArrayEquals(new String[]{"Sailors", "A", "B", "C"}, dbc.tableSchema("Sailors"));
		// boats schema
		assertArrayEquals(new String[]{"Boats", "D", "E", "F"}, dbc.tableSchema("Boats"));
		// reserves schema
		assertArrayEquals(new String[]{"Reserves", "G", "H"}, dbc.tableSchema("Reserves"));
	}

}
