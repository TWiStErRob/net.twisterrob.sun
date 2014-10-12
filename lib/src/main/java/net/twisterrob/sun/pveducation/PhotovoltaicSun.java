package net.twisterrob.sun.pveducation;

import java.util.Calendar;

import static java.lang.Math.*;

import net.twisterrob.sun.*;

/**
 * http://www.pveducation.org/pvcdrom/properties-of-sunlight/elevation-angle
 * http://www.pveducation.org/pvcdrom/properties-of-sunlight/solar-time
 * http://www.pveducation.org/pvcdrom/properties-of-sunlight/azimuth-angle
 */
public class PhotovoltaicSun implements Sun {
	private final SeasonFormula formula;

	public PhotovoltaicSun() {
		this(new AccuratePhotovoltaicFormula());
	}

	public PhotovoltaicSun(SeasonFormula formula) {
		this.formula = formula;
	}

	/**
	 * The elevation angle (used interchangeably with altitude angle) is
	 * the angular height of the sun in the sky measured from the horizontal.
	 * Confusingly, both altitude and elevation are also used to describe the height in meters above sea level.
	 * The elevation is 0° at sunrise and 90° when the sun is directly overhead
	 * (which occurs for example at the equator on the spring and fall equinoxes).
	 * The elevation angle varies throughout the day.
	 * It also depends on the latitude of a particular location and the day of the year.
	 */
	public double altitudeAngle(double lat, double lon, Calendar time) {
		double phi = toRadians(lat);
		double delta = toRadians(declination(lat, lon, time));
		double HRA = toRadians(hourAngle(lat, lon, time));

		return toDegrees(asin(sin(delta) * sin(phi) + cos(delta) * cos(phi) * cos(HRA)));
	}

	/**
	 * The azimuth angle is the compass direction from which the sunlight is coming.
	 * At solar noon, the sun is always directly south in the northern hemisphere
	 * and directly north in the southern hemisphere.
	 * The azimuth angle varies throughout the day.
	 * At the equinoxes, the sun rises directly east and sets directly west regardless of the latitude,
	 * thus making the azimuth angles 90° at sunrise and 270° at sunset.
	 * In general however, the azimuth angle varies with the latitude and time of year.
	 *
	 * The azimuth angle is like a compass direction with North = 0° and South = 180°.
	 * Other authors use a variety of slightly different definitions (i.e., angles of ± 180° and South = 0°).
	 */
	public double azimuthAngle(double lat, double lon, Calendar time) {
		double phi = toRadians(lat);
		double delta = toRadians(declination(lat, lon, time));
		double HRA = toRadians(hourAngle(lat, lon, time));
		double alpha = toRadians(altitudeAngle(lat, lon, time));
		double azimuth = toDegrees(acos((sin(delta) * cos(phi) - cos(delta) * sin(phi) * cos(HRA)) / cos(alpha)));
		return HRA < 0? azimuth : 360 - azimuth;
	}

	/**
	 * Local time (LT) usually varies from LST because of the eccentricity of the Earth's orbit,
	 * and because of human adjustments such as time zones and daylight saving.
	 * @return hours
	 */
	public double clockTime(double lat, double lon, Calendar time) {
		return time.get(Calendar.HOUR_OF_DAY) + time.get(Calendar.MINUTE) / 60d + time.get(Calendar.SECOND) / 60d / 60d;
	}

	/**
	 * Local Solar Time (LST)
	 *
	 * Twelve noon local solar time (LST) is defined as when the sun is highest in the sky.
	 *
	 * The Local Solar Time (LST) can be found by using the previous two corrections to adjust the local time (LT).
	 * @return hours
	 */
	public double solarTime(double lat, double lon, Calendar time) {
		return clockTime(lat, lon, time) + TC(lat, lon, time) / 60;
	}

	/**
	 * Hour Angle (HRA)
	 *
	 * The Hour Angle converts the local solar time (LST) into the number of degrees which the sun moves across the sky.
	 * By definition, the Hour Angle is 0° at solar noon. Since the Earth rotates 15° per hour,
	 * each hour away from solar noon corresponds to an angular motion of the sun in the sky of 15°.
	 * In the morning the hour angle is negative, in the afternoon the hour angle is positive.
	 *
	 * @return degrees
	 */
	public double hourAngle(double lat, double lon, Calendar time) {
		return EARTH_ROTATION_PER_HOUR * (solarTime(lat, lon, time) - 12);
	}

	public double declination(double lat, double lon, Calendar time) {
		return formula.declination(time);
	}

	/**
	 * Equation of Time (EoT)
	 *
	 * The equation of time (EoT) (in minutes) is an empirical equation that
	 * corrects for the eccentricity of the Earth's orbit and the Earth's axial tilt.
	 * @return minutes
	 */
	public double equationOfTime(double lat, double lon, Calendar time) {
		double B = B(time);
		double Brad = toRadians(B);
		return 9.87 * sin(2 * Brad) - 7.53 * cos(Brad) - 1.5 * sin(Brad);
	}

	/** Earth rotates 15° per hour. */
	private static final int EARTH_ROTATION_PER_HOUR = 360 / 24;
	/** Earth rotates 1° every 4 minutes. */
	private static final int EARTH_ROTATION_ONE_DEGREE = 24 * 60 / 360;

	/**
	 * Time Correction Factor (TC)
	 *
	 * The net Time Correction Factor (in minutes) accounts for the variation of the Local Solar Time (LST)
	 * within a given time zone due to the longitude variations within the time zone and also incorporates the EoT.
	 *
	 * The factor of 4 minutes comes from the fact that the Earth rotates 1° every 4 minutes.
	 * @return minutes
	 */
	private double TC(double lat, double lon, Calendar time) {
		return EARTH_ROTATION_ONE_DEGREE * (lon - LSTM(time)) + equationOfTime(lat, lon, time);
	}

	private static double B(Calendar time) {
		int d = time.get(Calendar.DAY_OF_YEAR); // d is the number of days since the start of the year
		return 360d / 365d * (d - 81d);
	}

	/**
	 * Local Standard Time Meridian (LSTM)
	 *
	 * The Local Standard Time Meridian (LSTM) is a reference meridian used for a particular time zone
	 * and is similar to the Prime Meridian, which is used for Greenwich Mean Time.
	 * @return degrees
	 */
	private static double LSTM(Calendar time) {
		return EARTH_ROTATION_PER_HOUR * diffFromGMT(time);
	}

	/**
	 * ΔTGMT is the difference of the Local Time (LT) from Greenwich Mean Time (GMT) in hours.
	 * @return hours
	 */
	private static double diffFromGMT(Calendar time) {
		return time.getTimeZone().getOffset(time.getTimeInMillis()) / 1000d / 60d / 60d;
	}

	public static void main(String[] args) {
		Sun sun = new PhotovoltaicSun();
		Calendar now = Calendar.getInstance();
		for (int d = 1; d <= 365; ++d) {
			now.set(Calendar.DAY_OF_YEAR, d);
			System.out.printf("%1$tF\t%2$d\t%3$.3f\n", now, d, sun.equationOfTime(0, 0, now));
		}
	}
}
