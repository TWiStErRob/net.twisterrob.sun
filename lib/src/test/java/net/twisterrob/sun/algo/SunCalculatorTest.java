package net.twisterrob.sun.algo;

import java.util.*;

import org.junit.*;

import static org.junit.Assert.*;

import net.twisterrob.sun.Sun;
import net.twisterrob.sun.algo.SunSearchResults.*;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

/**
 * http://aa.usno.navy.mil/data/docs/AltAz.php
 * http://aa.usno.navy.mil/cgi-bin/aa_altazw.pl?FFX=2&obj=10&xxy=2014&xxm=6&xxd=21&xxi=1&place=%28no+name+given%29&xx0=-1&xx1=0&xx2=4&yy0=1&yy1=51&yy2=31&zz1=1&zz0=1&ZZZ=END
 */
public class SunCalculatorTest {
	protected Sun sun = new PhotovoltaicSun(); // TODO externalize

	private double lat;
	private double lon;
	private Calendar time;

	@Before
	public void setUp() {
		lat = 51.519586;
		lon = -0.068586;
		time = Calendar.getInstance();
		time.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		time.set(2014, Calendar.JUNE, 21, 0, 0, 0);
	}

	@Test
	public void test() {
		SunSearchParams params = new SunSearchParams(lat, lon, at(9, 30), ThresholdRelation.ABOVE, 50);

		SunSearchResults result = new SunCalculator(sun).find(params);

		assertEquals(50, result.params.thresholdAngle, 1e-6);
		assertEquals(at(9, 30), result.current.time);
		assertEquals(40.9, result.current.angle, 0.5);
		assertNotNull(result.threshold);
		assertEquals(at(10, 33), result.threshold.start);
		assertEquals(at(15, 31), result.threshold.end);
		assertNotNull(result.horizon);
		assertEquals(at(4, 50), result.horizon.start);
		assertEquals(at(21, 15), result.horizon.end);
		assertNotNull(result.minimum);
		assertEquals(-15, result.minimum.angle, 0.5);
		assertEquals(at(1, 2), result.minimum.time);
		assertNotNull(result.maximum);
		assertEquals(62, result.maximum.angle, 0.5);
		assertEquals(at(13, 2), result.maximum.time);
	}

	private Calendar at(int hour, int minute) {
		Calendar time = (Calendar)this.time.clone();
		time.add(Calendar.HOUR, hour);
		time.add(Calendar.MINUTE, minute);
		return time;
	}
}
