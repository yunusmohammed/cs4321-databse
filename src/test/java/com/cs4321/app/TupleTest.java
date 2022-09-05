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
		assertEquals(true, empty.equals(new Tuple("")));
		// quadruple
		assertEquals(true, quadruple.equals(new Tuple("9,-18,100,0")));
		// not equal
		assertEquals(false, quadruple.equals(single));
		assertEquals(false, single.equals(quadruple));
		assertEquals(false, empty.equals(single));
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

}
