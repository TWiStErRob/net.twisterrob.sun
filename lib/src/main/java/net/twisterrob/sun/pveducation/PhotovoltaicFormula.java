package net.twisterrob.sun.pveducation;

import java.util.Calendar;

import static java.lang.Math.*;

import net.twisterrob.sun.SeasonFormula;

/**
 * http://www.pveducation.org/pvcdrom/properties-of-sunlight/declination-angle
 */
public class PhotovoltaicFormula implements SeasonFormula {
	/**
	 * The Earth is tilted by 23.45° and the declination angle varies plus or minus this amount.
	 */
	protected static final double EARTH_TILT = 23.45;

	/**
	 * The declination angle, denoted by δ, varies seasonally due to the tilt of the Earth on its axis of rotation
	 * and the rotation of the Earth around the sun.
	 * If the Earth were not tilted on its axis of rotation, the declination would always be 0°.
	 * However, the Earth is tilted by 23.45° and the declination angle varies plus or minus this amount.
	 * Only at the spring and fall equinoxes is the declination angle equal to 0°.
	 */
	public double declination(Calendar time) {
		double B = toRadians(B(time));
		return EARTH_TILT * sin(B);
	}

	private static double B(Calendar time) {
		int d = time.get(Calendar.DAY_OF_YEAR); // d is the number of days since the start of the year
		return 360d / 365d * (d - 81d);
	}
}
