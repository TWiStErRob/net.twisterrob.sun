package net.twisterrob.sun.android.model;

/**
 * <table>
 * <tr><th>Operator</th><th>Range</th><th>Name</th></tr>
 * <tr><td>below<td><td>-18</td><td>night</td></tr>
 * <tr><td>around<td><td>-18</td><td>astronomical dawn</td></tr>
 * <tr><td>between<td><td>-18 &mdash; -12</td><td>astronomical twilight</td></tr>
 * <tr><td>around<td><td>-12</td><td>nautical dawn</td></tr>
 * <tr><td>between<td><td>-12 &mdash; -6</td><td>nautical twilight</td></tr>
 * <tr><td>around<td><td>-6</td><td>civil dawn</td></tr>
 * <tr><td>between<td><td>-6 &mdash; 0</td><td>civil twilight</td></tr>
 * <tr><td>around<td><td>0</td><td>sunrise</td></tr>
 * <tr><td>above<td><td>0</td><td>day</td></tr>
 * <tr><td>around<td><td>0</td><td>sunset</td></tr>
 * <tr><td>between<td><td>0 &mdash; -6</td><td>civil twilight</td></tr>
 * <tr><td>around<td><td>-6</td><td>civil dusk</td></tr>
 * <tr><td>between<td><td>-6 &mdash; -12</td><td>nautical twilight</td></tr>
 * <tr><td>around<td><td>-12</td><td>nautical dusk</td></tr>
 * <tr><td>between<td><td>-12 &mdash; -18</td><td>astronomical twilight</td></tr>
 * <tr><td>around<td><td>-18</td><td>astronomical dusk</td></tr>
 * <tr><td>below<td><td>-18</td><td>night</td></tr>
 * </table>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Twilight#Definitions">Twilight on Wikipedia</a>
 */
public enum LightState {
	/* The order if the constants is important in matching, the ranges overlap around thresholds,
	 * and the thresholds are more important then others. */

	/** Sunset and Sunrise */
	HORIZON_TRANSITION(-0.25, 1, false),
	/** Civil dawn and Civil dusk */
	CIVIL_THRESHOLD(-6, 0.5, true),
	/** Nautical dawn and Nautical dusk */
	NAUTICAL_THRESHOLD(-12, 0.5, true),
	/** Astronomical dawn and Astronomical dusk */
	ASTRONOMICAL_THRESHOLD(-18, 0.5, true),
	/** Civil twilight */
	CIVIL_TWILIGHT(-6, 0, false),
	/** Nautical twilight */
	NAUTICAL_TWILIGHT(-12, -6, false),
	/** Astronomical twilight */
	ASTRONOMICAL_TWILIGHT(-18, -12, false),
	/** Night-time */
	NIGHT(Double.NEGATIVE_INFINITY, -18, false),
	/** Day-time */
	DAY(0, Double.POSITIVE_INFINITY, false);

	private final double min, max;

	LightState(double a, double b, boolean delta) {
		if (delta) {
			min = a - b;
			max = a + b;
		} else {
			min = a;
			max = b;
		}
	}
	public static LightState from(double sunAngle) {
		for (LightState value : values()) {
			if (value.matches(sunAngle)) {
				return value;
			}
		}
		return DAY;
	}

	private boolean matches(double sunAngle) {
		return min <= sunAngle && sunAngle <= max;
	}
}
