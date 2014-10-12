package net.twisterrob.sun.wikipedia;

import java.util.Calendar;

import static java.lang.Math.*;

import net.twisterrob.sun.SeasonFormula;

/**
 * http://en.wikipedia.org/wiki/Position_of_the_Sun#Calculations
 */
public class AccurateWikiFormula implements SeasonFormula {
	public double declination(Calendar time) {
		int d = time.get(Calendar.DAY_OF_YEAR); // d is the number of days since the start of the year
		return toDegrees(
				-asin(0.39779 * cos(toRadians(0.98565 * (d + 10) + 1.914 * sin(toRadians(0.98565 * (d - 2)))))));
	}
}
