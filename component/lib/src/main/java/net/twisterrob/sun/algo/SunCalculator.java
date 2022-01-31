package net.twisterrob.sun.algo;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.sun.Sun;
import net.twisterrob.sun.algo.SunSearchResults.*;

public class SunCalculator {
	private final @NonNull Sun sun;

	public SunCalculator(@NonNull Sun sun) {
		this.sun = sun;
	}

	public @NonNull SunSearchResults find(@NonNull SunSearchParams p) {
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

	private void updateMinMax(@NonNull Moment min, @NonNull Moment max, double lat, double lon, @NonNull Calendar start, @NonNull Calendar stop) {
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
	public static @NonNull Calendar startOfDay(@NonNull Calendar time) {
		Calendar result = (Calendar)time.clone();
		result.set(Calendar.HOUR_OF_DAY, 0);
		result.set(Calendar.MINUTE, 0);
		result.set(Calendar.SECOND, 0);
		return result;
	}
	public static @NonNull Calendar endOfDay(@NonNull Calendar time) {
		Calendar result = startOfDay(time);
		result.add(Calendar.DATE, 1);
		return result;
	}

	private @NonNull Range find(
			double lat,
			double lon,
			@NonNull Calendar start,
			@NonNull Calendar end,
			@Nullable ThresholdRelation relation,
			double threshold
	) {
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
		if (result.start == null || result.end == null) {
			// If one end of the result is missing, clear both to prevent weird displays.
			result.start = null;
			result.end = null;
		} else if (relation == ThresholdRelation.BELOW) {
			Calendar temp = result.end;
			result.end = result.start;
			result.start = temp;
			result.end.add(Calendar.DATE, 1);
		} // TODO else is wrong, null and ABOVE, default elsewhere.
		return result;
	}
}
