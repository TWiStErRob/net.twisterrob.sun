package net.twisterrob.sun.test;

import java.util.Calendar;

import static java.lang.Math.*;

import org.junit.Test;

import static org.junit.Assert.*;

import net.twisterrob.sun.SeasonFormula;

/**
 * The declination is zero at the equinoxes (March 22 and September 22),
 * positive during the northern hemisphere summer and negative during the northern hemisphere winter.
 * The declination reaches a maximum of 23.45° on June 22 (summer solstice in the northern hemisphere)
 * and a minimum of -23.45° on December 22 (winter solstice in the northern hemisphere).
 */
public abstract class SeasonFormulaTest {
	private static final Calendar SPRING_EQUINOX = date(Calendar.MARCH, 22); // 0
	private static final Calendar SUMMER_SOLSTICE = date(Calendar.JUNE, 22); // 23.45
	private static final Calendar AUTUMN_EQUINOX = date(Calendar.SEPTEMBER, 22); // 0
	private static final Calendar WINTER_SOLSTICE = date(Calendar.DECEMBER, 22); // -23.45

	/**
	 * The Earth is tilted by 23.45° and the declination angle varies plus or minus this amount.
	 */
	protected static final double EARTH_TILT = 23.45;
	protected static final double EARTH_TILT_MIDWAY = 23.45 * sqrt(2) / 2; // approximate

	protected SeasonFormula formula;

	protected double allowedTiltDeltaDegrees = 1;

	@Test
	public void testDeclinationEquinox() {
		Calendar springEquinox = (Calendar)SPRING_EQUINOX.clone();
		Calendar autumnEquinox = (Calendar)AUTUMN_EQUINOX.clone();

		double springDeclination = formula.declination(springEquinox);
		double autumnDeclination = formula.declination(autumnEquinox);

		assertEquals(0, springDeclination, allowedTiltDeltaDegrees);
		assertEquals(0, autumnDeclination, allowedTiltDeltaDegrees);
	}

	@Test
	public void testDeclinationSolstice() {
		Calendar summerSolstice = (Calendar)SUMMER_SOLSTICE.clone();
		Calendar winterSolstice = (Calendar)WINTER_SOLSTICE.clone();

		double summerDeclination = formula.declination(summerSolstice);
		double winterDeclination = formula.declination(winterSolstice);

		assertEquals(EARTH_TILT, summerDeclination, allowedTiltDeltaDegrees);
		assertEquals(-EARTH_TILT, winterDeclination, allowedTiltDeltaDegrees);
	}

	@Test
	public void testDeclinationEquinoxToSolstice() {
		Calendar mayMidway = middle(SPRING_EQUINOX, SUMMER_SOLSTICE);
		Calendar novemberMidway = middle(AUTUMN_EQUINOX, WINTER_SOLSTICE);

		double mayDeclination = formula.declination(mayMidway);
		double novemberDeclination = formula.declination(novemberMidway);

		assertEquals(EARTH_TILT_MIDWAY, mayDeclination, allowedTiltDeltaDegrees);
		assertEquals(-EARTH_TILT_MIDWAY, novemberDeclination, allowedTiltDeltaDegrees);
	}

	@Test
	public void testDeclinationSolsticeToEquinox() {
		Calendar augustMidway = middle(SUMMER_SOLSTICE, AUTUMN_EQUINOX);
		Calendar februaryMidway = middle(WINTER_SOLSTICE, SPRING_EQUINOX);

		double augustDeclination = formula.declination(augustMidway);
		double februaryDeclination = formula.declination(februaryMidway);

		assertEquals(EARTH_TILT_MIDWAY, augustDeclination, allowedTiltDeltaDegrees);
		assertEquals(-EARTH_TILT_MIDWAY, februaryDeclination, allowedTiltDeltaDegrees);
	}

	protected static Calendar date(int month, int day) {
		Calendar result = Calendar.getInstance();
		result.set(result.get(Calendar.YEAR), month, day, 0, 0, 0);
		return result;
	}

	private static Calendar middle(Calendar date1, Calendar date2) {
		date1 = (Calendar)date1.clone();
		date2 = (Calendar)date2.clone();
		assertEquals(date1.get(Calendar.YEAR), date2.get(Calendar.YEAR));
		if (date1.compareTo(date2) > 0) {
			date2.add(Calendar.YEAR, 1);
		}
		Calendar result = Calendar.getInstance();
		result.setTimeInMillis((date1.getTimeInMillis() + date2.getTimeInMillis()) / 2);
		return result;
	}
}
