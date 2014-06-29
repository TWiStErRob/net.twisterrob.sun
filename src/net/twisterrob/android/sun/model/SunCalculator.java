package net.twisterrob.android.sun.model;

import java.util.Calendar;

public class SunCalculator {
	private final Sun sun;

	public SunCalculator(Sun sun) {
		this.sun = sun;
	}

	public SunAngle find(double degrees, ThresholdRelation relation, double lat, double lon, Calendar time) {
		SunAngle result = new SunAngle();
		result.angleThreshold = degrees;
		result.thresholdRelation = relation;
		result.current = sun.altitudeAngle(lat, lon, time);
		result.lastUpdate = (Calendar)time.clone();
		Calendar running = (Calendar)time.clone();
		running.set(Calendar.HOUR_OF_DAY, 0);
		running.set(Calendar.MINUTE, 0);
		running.set(Calendar.SECOND, 0);

		int every = 1;
		for (int minute = 0; minute < 24 * 60 / every; ++minute) {
			running.add(Calendar.MINUTE, every);
			double altitude = sun.altitudeAngle(lat, lon, running);
			if (result.start == null && altitude > result.angleThreshold) {
				result.start = (Calendar)running.clone();
			}
			if (result.start != null && result.end == null && altitude < result.angleThreshold) {
				result.end = (Calendar)running.clone();
			}
			if (time.get(Calendar.DATE) != running.get(Calendar.DATE)) {
				running.add(Calendar.DATE, -1);
				if (result.start == null) {
					result.start = (Calendar)running.clone();
				}
				if (result.end == null) {
					result.end = (Calendar)running.clone();
				}
			}
		}

		result.sunState = LightState.from(result.current);
		return result;
	}
}
