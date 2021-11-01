package net.twisterrob.sun.pveducation;

import org.junit.Before;

import net.twisterrob.sun.test.SunTest;

public class PhotovoltaicSunTest extends SunTest {
	@Before
	public void setUpSun() {
		super.sun = new PhotovoltaicSun();
	}
}
