package net.twisterrob.sun.algo;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.annotation.NonNull;

import net.twisterrob.sun.Sun;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

public class SunCalculatorTest {

	private @NonNull Sun sun;

	@Before
	public void setUp() {
		sun = new PhotovoltaicSun(); // TODO externalize
	}

	/**
	 * http://aa.usno.navy.mil/data/docs/AltAz.php
	 * http://aa.usno.navy.mil/cgi-bin/aa_altazw.pl?FFX=2&obj=10&xxy=2014&xxm=6&xxd=21&xxi=1&place=%28no+name+given%29&xx0=-1&xx1=0&xx2=4&yy0=1&yy1=51&yy2=31&zz1=1&zz0=1&ZZZ=END
	 */
	@Test
	public void testSunnyDayInTheUK() {
		// Ramar House, London, UK
		double lat = 51.519586;
		double lon = -0.068586;
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

	@Test
	public void testTwilightInTheUK() {
		// Ramar House, London, UK
		double lat = 51.519586;
		double lon = -0.068586;
		SunSearchParams params = new SunSearchParams(lat, lon, at(9, 30), ThresholdRelation.BELOW, -12);

		SunSearchResults result = new SunCalculator(sun).find(params);

		assertEquals(-12, result.params.thresholdAngle, 1e-6);
		assertEquals(at(9, 30), result.current.time);
		assertEquals(40.9, result.current.angle, 0.5);
		assertNotNull(result.threshold);
		assertEquals(at(23, 24), result.threshold.start);
		assertEquals(nextDay(at(2, 40)), result.threshold.end);
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

	@Test
	public void testAboveMax() {
		// Ramar House, London, UK
		double lat = 51.519586;
		double lon = -0.068586;
		SunSearchParams params = new SunSearchParams(lat, lon, at(9, 30), ThresholdRelation.ABOVE, 63);

		SunSearchResults result = new SunCalculator(sun).find(params);

		assertEquals(63, result.params.thresholdAngle, 1e-6);
		assertEquals(at(9, 30), result.current.time);
		assertEquals(40.9, result.current.angle, 0.5);
		assertNotNull(result.threshold);
		assertNull(result.threshold.start);
		assertNull(result.threshold.end);
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

	@Test
	public void testAboveMin() {
		// Ramar House, London, UK
		double lat = 51.519586;
		double lon = -0.068586;
		SunSearchParams params = new SunSearchParams(lat, lon, at(9, 30), ThresholdRelation.ABOVE, -20);

		SunSearchResults result = new SunCalculator(sun).find(params);

		assertEquals(-20, result.params.thresholdAngle, 1e-6);
		assertEquals(at(9, 30), result.current.time);
		assertEquals(40.9, result.current.angle, 0.5);
		assertNotNull(result.threshold);
		assertNull(result.threshold.start);
		assertNull(result.threshold.end);
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

	@Test
	public void testBelowMax() {
		// Ramar House, London, UK
		double lat = 51.519586;
		double lon = -0.068586;
		SunSearchParams params = new SunSearchParams(lat, lon, at(9, 30), ThresholdRelation.BELOW, 64);

		SunSearchResults result = new SunCalculator(sun).find(params);

		assertEquals(64, result.params.thresholdAngle, 1e-6);
		assertEquals(at(9, 30), result.current.time);
		assertEquals(40.9, result.current.angle, 0.5);
		assertNotNull(result.threshold);
		assertNull(result.threshold.start);
		assertNull(result.threshold.end);
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

	@Test
	public void testBelowMin() {
		// Ramar House, London, UK
		double lat = 51.519586;
		double lon = -0.068586;
		SunSearchParams params = new SunSearchParams(lat, lon, at(9, 30), ThresholdRelation.BELOW, -16);

		SunSearchResults result = new SunCalculator(sun).find(params);

		assertEquals(-16, result.params.thresholdAngle, 1e-6);
		assertEquals(at(9, 30), result.current.time);
		assertEquals(40.9, result.current.angle, 0.5);
		assertNotNull(result.threshold);
		assertNull(result.threshold.start);
		assertNull(result.threshold.end);
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

	private @NonNull Calendar nextDay(@NonNull Calendar time) {
		time.add(Calendar.DATE, 1);
		return time;
	}

	private @NonNull Calendar at(int hour, int minute) {
		Calendar time = Calendar.getInstance();
		time.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		time.set(2014, Calendar.JUNE, 21, 0, 0, 0);
		time.set(Calendar.MILLISECOND, 0);
		time.add(Calendar.HOUR, hour);
		time.add(Calendar.MINUTE, minute);
		return time;
	}
}
