/**
 *
 */
package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

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
	void getTableTest() {

		List<Tuple> boats = dbc.getTable("Boats");
		List<Tuple> reserves = dbc.getTable("Reserves");
		List<Tuple> empty = dbc.getTable("Empty");

		// boats table rows
		assertEquals(new Tuple("101,2,3"), boats.get(0));
		assertEquals(new Tuple("102,3,4"), boats.get(1));
		assertEquals(new Tuple("104,104,2"), boats.get(2));
		assertEquals(new Tuple("107,2,8"), boats.get(4));

		// reserves table rows
		assertEquals(new Tuple("1,101"), reserves.get(0));
		assertEquals(new Tuple("4,104"), reserves.get(5));

		// empty table
		for(int i=0; i<3; i++) {
			assertEquals(new Tuple(""), empty.get(i));
		}

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