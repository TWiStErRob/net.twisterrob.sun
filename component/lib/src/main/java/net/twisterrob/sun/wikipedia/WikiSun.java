package net.twisterrob.sun.wikipedia;

import java.util.Calendar;

import static java.lang.Math.*;

import net.twisterrob.sun.*;

/**
 * http://en.wikipedia.org/wiki/Solar_elevation_angle#Solar_elevation_angle
 */
public class WikiSun implements Sun {
	private final SeasonFormula formula;

	public WikiSun() {
		this(new WikiFormula());
	}

	public WikiSun(SeasonFormula formula) {
		this.formula = formula;
	}

	@Override
	public double altitudeAngle(double lat, double lon, Calendar time) {
		double phi = toRadians(lat);
		double delta = formula.declination(time);
		double h = 0;
		return asin(cos(h) * cos(delta) * cos(phi) + sin(delta) * sin(phi));
	}

	@Override
	public double azimuthAngle(double lat, double lon, Calendar time) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public double clockTime(double lat, double lon, Calendar time) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public double solarTime(double lat, double lon, Calendar time) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public double hourAngle(double lat, double lon, Calendar time) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public double declination(double lat, double lon, Calendar time) {
		return formula.declination(time);
	}

	@Override
	public double equationOfTime(double lat, double lon, Calendar time) {
		throw new UnsupportedOperationException("Not implemented.");
	}
}
