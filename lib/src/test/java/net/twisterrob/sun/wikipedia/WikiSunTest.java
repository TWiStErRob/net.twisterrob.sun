package net.twisterrob.sun.wikipedia;

import org.junit.*;

import net.twisterrob.sun.test.SunTest;

@Ignore
public class WikiSunTest extends SunTest {
	@Before
	public void setUpSun() {
		super.sun = new WikiSun();
	}
}
