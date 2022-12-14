/**
 * 
 */
package com.cs4321.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author Yohanes
 *
 */
class TupleTest {
	
	Tuple empty = new Tuple("");
	Tuple single = new Tuple("5");
	Tuple quadruple = new Tuple("9,-18,100,0");

	@Test
	void sizeTest() {
		// zero length tuple
		assertEquals(0, empty.size());
		// single length tuple
		assertEquals(1, single.size());
		// tuple of length 4
		assertEquals(4, quadruple.size());
	}
	
	@Test
	void getTest() {
		// first item in single
		assertEquals(5, single.get(0));
		// first item in quadruple
		assertEquals(9, quadruple.get(0));
		//second item in quadruple
		assertEquals(-18, quadruple.get(1));
		// third item in quadruple
		assertEquals(100, quadruple.get(2));
		// fourth item in quadruple
		assertEquals(0, quadruple.get(3));
	}
	
	@Test
	void equalsTest() {
		// empty tuple
		assertEquals(empty, new Tuple(""));
		// quadruple
		assertEquals(quadruple, new Tuple("9,-18,100,0"));
		// not equal
		assertNotEquals(quadruple, single);
		assertNotEquals(single, quadruple);
		assertNotEquals(empty, single);
	}
	
	@Test
	void toStringTest() {
		// empty tuple string
		assertEquals("", empty.toString());
		// single tuple string
		assertEquals("5", single.toString());
		// quadruple tuple string
		assertEquals("9,-18,100,0", quadruple.toString());
	}

	@Test
	void concatTest() {
		// empty concat empty
		assertEquals("", empty.concat(empty).toString());
		// concat empty should return equal tuple
		assertEquals(quadruple, quadruple.concat(empty));
		// empty concat tuple should equal tuple
		assertEquals(quadruple, empty.concat(quadruple));
		// concat quadruple with single
		assertEquals("9,-18,100,0,5", quadruple.concat(single).toString());
		// concat single with quadruple
		assertEquals("5,9,-18,100,0", single.concat(quadruple).toString());
		// concat quadruple with itself
		assertEquals("9,-18,100,0,9,-18,100,0", quadruple.concat(quadruple).toString());

	}

}
