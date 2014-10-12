package net.twisterrob.sun;

import static java.lang.Math.*;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConversionTests {
	@Test
	public void testRadians() {
		assertEquals(3 * toRadians(45), toRadians(3 * 45), 1e-6);
		assertEquals(7 * toRadians(30), toRadians(7 * 30), 1e-6);
	}
}
