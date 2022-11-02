/**
 * 
 */
package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Yohanes
 *
 */
class DatabaseCatalogTest {
	
	DatabaseCatalog dbc = DatabaseCatalog.getInstance();
	static String sep = File.separator;
	static String inputdir = System.getProperty("user.dir") + sep + "src" + sep + "test" + sep + "resources"+ sep + "input";
	
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
	
	@Test
	void columnMapTest() {
		HashMap<String, Integer> sailorMap = dbc.columnMap("Sailors");
		HashMap<String, Integer> ReservesMap = dbc.columnMap("Reserves");
		
		assertEquals(0, sailorMap.get("A"));
		assertEquals(1, sailorMap.get("B"));
		assertEquals(2, sailorMap.get("C"));
		
		assertEquals(0, ReservesMap.get("G"));
		assertEquals(1, ReservesMap.get("H"));
		
	}

	@Test
	void getColumnIndexMapTest() {
		HashMap<String, HashSet<String>> indexColumns = dbc.getIndexColumns();
		assertEquals(2, indexColumns.keySet().size());
		assertEquals(new HashSet<>(Arrays.asList("E")), indexColumns.get("Boats"));
		assertEquals(new HashSet<>(Arrays.asList("A")), indexColumns.get("Sailors"));
	}

}
