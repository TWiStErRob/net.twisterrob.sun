package net.twisterrob.android.sun.model;

import java.util.Calendar;

import net.twisterrob.android.sun.model.SunSearchResults.Moment;
import net.twisterrob.android.sun.model.SunSearchResults.Range;
import net.twisterrob.android.sun.model.SunSearchResults.SunSearchParams;
import net.twisterrob.android.sun.model.SunSearchResults.ThresholdRelation;

public class SunCalculator {
	private final Sun sun;

	public SunCalculator(Sun sun) {
		this.sun = sun;
	}

	public SunSearchResults find(SunSearchParams p) {
		p = p.clone();
		SunSearchResults result = new SunSearchResults(p);
		result.current = new Moment(p.time, sun.altitudeAngle(p.latitude, p.longitude, p.time));

		result.threshold = find(p.latitude, p.longitude, startOfDay(p.time), endOfDay(p.time), p.thresholdRelation,
				p.thresholdAngle);
		result.horizon = find(p.latitude, p.longitude, startOfDay(p.time), endOfDay(p.time), ThresholdRelation.ABOVE, 0);

		updateMinMax(result.minimum = new Moment(), result.maximum = new Moment(), //
				p.latitude, p.longitude, startOfDay(p.time), endOfDay(p.time));
		return result;
	}

	private void updateMinMax(Moment min, Moment max, double lat, double lon, Calendar start, Calendar stop) {
		final int every = 1;
		Calendar running = (Calendar)start.clone();

		min.angle = Double.MAX_VALUE;
		max.angle = Double.MIN_VALUE;
		while (running.before(stop)) {
			double angle = sun.altitudeAngle(lat, lon, running);
			if (angle < min.angle) {
				min.angle = angle;
				min.time = (Calendar)running.clone();
			}
			if (max.angle < angle) {
				max.angle = angle;
				max.time = (Calendar)running.clone();
			}
			running.add(Calendar.MINUTE, every);
		}
	}
	private static Calendar startOfDay(Calendar time) {
		Calendar result = (Calendar)time.clone();
		result.set(Calendar.HOUR_OF_DAY, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.SECOND, 0);
		return result;
	}
	private static Calendar endOfDay(Calendar time) {
		Calendar result = startOfDay(time);
		result.add(Calendar.DATE, 1);
		return result;
	}

	private Range find(double lat, double lon, Calendar start, Calendar stop, ThresholdRelation relation,
			double threshold) {
		final int every = 1;
		Range result = new Range();
		if (relation != null) {
			Calendar running = (Calendar)start.clone();
			while (running.before(stop)) {
				double angle = sun.altitudeAngle(lat, lon, running);
				switch (relation) {
					case ABOVE:
						if (result.start == null && angle >= threshold) {
							result.start = (Calendar)running.clone();
						}
						if (result.start != null && result.end == null && angle <= threshold) {
							result.end = (Calendar)running.clone();
						}
						break;
					case BELOW:
						if (result.start == null && angle <= threshold) {
							result.start = (Calendar)running.clone();
						}
						if (result.start != null && result.end == null && angle >= threshold) {
							result.end = (Calendar)running.clone();
						}
						break;
					default:
						throw new UnsupportedOperationException("Enum value: " + relation);
				}
				running.add(Calendar.MINUTE, every);
			}
		}
		return result;
	}
}
