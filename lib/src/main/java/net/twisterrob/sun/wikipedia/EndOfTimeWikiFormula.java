package net.twisterrob.sun.wikipedia;

import java.util.Calendar;

import static java.lang.Math.*;

import net.twisterrob.sun.SeasonFormula;

/**
 * http://en.wikipedia.org/wiki/Equation_of_time#Addendum_about_solar_declination
 */
public class EndOfTimeWikiFormula implements SeasonFormula {
	/**
	 * W is the Earth's mean angular orbital velocity in degrees per day.
	 */
	private static final double EARTH_ANGULAR_VELOCITY = 360 / 365.24;

	public double declination(Calendar time) {
		double B = B(time);
		return toDegrees(-asin(sin(toRadians(WikiFormula.EARTH_TILT)) * cos(toRadians(B))));
	}

	private static double B(Calendar time) {
		int d = time.get(Calendar.DAY_OF_YEAR); // d is the number of days since the start of the year
		double A = EARTH_ANGULAR_VELOCITY * (d + 10);
		double WDm2 = EARTH_ANGULAR_VELOCITY * (d - 2);
		return A + 360 / Math.PI * 0.0167 * sin(toRadians(WDm2));
	}
}
