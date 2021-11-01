package net.twisterrob.sun.test;

import java.util.*;

import org.junit.*;

import static org.junit.Assert.*;

import net.twisterrob.sun.Sun;

/** http://www.susdesign.com/sunangle/index.php */
public abstract class SunTest {
	/** Max 1 minute difference */
	private static final double DELTA_HOURS = 1. / 60;
	/** Max half a degree difference */
	private static final double DELTA_DEGREES = 0.5;
	/** Max 2 minutes difference */
	private static final double DELTA_MINUTES = 2;

	public Sun sun;

	private double lat;
	private double lon;
	private Calendar time;

	@Before
	public void setUp() {
		lat = 51.519586;
		lon = -0.068586;
		time = Calendar.getInstance();
		time.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		time.set(2014, Calendar.JUNE, 20, 21, 47, 21);
	}

	@Test
	public void declination() {
		assertEquals(23.44, sun.declination(lat, lon, time), DELTA_DEGREES);
	}
	@Test
	public void altitudeAngle() {
		assertEquals(-3.74, sun.altitudeAngle(lat, lon, time), DELTA_DEGREES);
	}
	@Test
	public void azimuthAngle() {
		assertEquals(136.29, sun.azimuthAngle(lat, lon, time) - 180, DELTA_DEGREES);
	}
	@Test
	public void equationOfTime() {
		assertEquals(-0.03, sun.equationOfTime(lat, lon, time), DELTA_MINUTES);
	}
	@Test
	public void clockTime() {
		assertEquals(hours(21, 47), sun.clockTime(lat, lon, time), DELTA_HOURS);
	}
	@Test
	public void solarTime() {
		assertEquals(hours(20, 45), sun.solarTime(lat, lon, time), DELTA_HOURS);
	}
	@Test
	public void hourAngle() {
		assertEquals(131.27, sun.hourAngle(lat, lon, time), DELTA_DEGREES);
	}

	/** http://en.wikipedia.org/wiki/Equation_of_time#Secular_effects */
	@Test
	public void equationOfTime_SecularEffects() {
		double delta = 0.5;
		assertEquals(-minutes(14, 15), sun.equationOfTime(lat, lon, date(Calendar.FEBRUARY, 11)), delta);
		assertEquals(minutes(0, 0), sun.equationOfTime(lat, lon, date(Calendar.APRIL, 15)), delta);
		assertEquals(minutes(3, 41), sun.equationOfTime(lat, lon, date(Calendar.MAY, 14)), delta);
		assertEquals(minutes(0, 0), sun.equationOfTime(lat, lon, date(Calendar.JUNE, 13)), delta);
		assertEquals(-minutes(6, 30), sun.equationOfTime(lat, lon, date(Calendar.JULY, 26)), delta);
		assertEquals(minutes(0, 0), sun.equationOfTime(lat, lon, date(Calendar.SEPTEMBER, 1)), delta);
		assertEquals(minutes(16, 25), sun.equationOfTime(lat, lon, date(Calendar.NOVEMBER, 8)), delta);
		assertEquals(minutes(0, 0), sun.equationOfTime(lat, lon, date(Calendar.DECEMBER, 25)), delta);
	}

	protected static Calendar date(int month, int day) {
		Calendar result = Calendar.getInstance();
		result.set(result.get(Calendar.YEAR), month, day, 0, 0, 0);
		return result;
	}
	private static double hours(double hours, double minutes) {
		return hours + minutes / 60;
	}
	private static double minutes(double minutes, double seconds) {
		return minutes + seconds / 60;
	}
}
