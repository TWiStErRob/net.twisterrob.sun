package net.twisterrob.sun.algo;

import java.util.Calendar;

import net.twisterrob.sun.Sun;
import net.twisterrob.sun.algo.SunSearchResults.*;

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
		result.horizon =
				find(p.latitude, p.longitude, startOfDay(p.time), endOfDay(p.time), ThresholdRelation.ABOVE, 0);

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
	public static Calendar startOfDay(Calendar time) {
		Calendar result = (Calendar)time.clone();
		result.set(Calendar.HOUR_OF_DAY, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.SECOND, 0);
		return result;
	}
	public static Calendar endOfDay(Calendar time) {
		Calendar result = startOfDay(time);
		result.add(Calendar.DATE, 1);
		return result;
	}

	private Range find(double lat, double lon, Calendar start, Calendar end, ThresholdRelation relation,
			double threshold) {
		Range result = new Range();
		final int every = 1;
		final Calendar running = (Calendar)start.clone();
		while (start.compareTo(running) <= 0 && running.compareTo(end) <= 0) {
			double angle = sun.altitudeAngle(lat, lon, running);
			if (result.start == null && angle >= threshold) {
				result.start = (Calendar)running.clone();
			}
			if (result.start != null && result.end == null && angle <= threshold) {
				result.end = (Calendar)running.clone();
			}
			running.add(Calendar.MINUTE, every);
		}
		if (relation == ThresholdRelation.BELOW) {
			Calendar temp = result.end;
			result.end = result.start;
			result.start = temp;
			result.end.add(Calendar.DATE, 1);
		}
		return result;
	}
}
