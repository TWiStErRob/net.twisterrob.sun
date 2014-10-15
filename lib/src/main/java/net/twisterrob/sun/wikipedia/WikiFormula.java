package net.twisterrob.sun.wikipedia;

import java.util.Calendar;

import static java.lang.Math.*;

import net.twisterrob.sun.SeasonFormula;

/**
 * http://en.wikipedia.org/wiki/Position_of_the_Sun#Calculations
 */
public class WikiFormula implements SeasonFormula {
	/**
	 * The Earth is tilted by 23° 26' and the declination angle varies plus or minus this amount.
	 */
	private static final double EARTH_TILT = 23.44;

	public double declination(Calendar time) {
		double B = B(time);
		return -EARTH_TILT * cos(toRadians(B));
	}

	private static double B(Calendar time) {
		int d = time.get(Calendar.DAY_OF_YEAR); // d is the number of days since the start of the year
		return 360d / 365d * (d + 10);
	}
}
