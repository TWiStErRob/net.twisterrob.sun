package net.twisterrob.android.sun.model;

import java.util.Calendar;

import android.util.Log;

public class SunCalculator {
	private final Sun sun;

	public SunCalculator(Sun sun) {
		this.sun = sun;
	}

	public SunAngle find(int degrees, double lat, double lon, Calendar time) {
		SunAngle result = new SunAngle();
		result.angleThreshold = degrees;
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
			Log.v("Calc", running.getTime().toString() + " -> " + altitude);
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

	public static class SunAngle {
		public Calendar lastUpdate;
		public double current;

		public double angleThreshold;
		public Calendar start;
		public Calendar end;
		public LightState sunState;

		@Override
		public String toString() {
			return "Now at " + ((int)(current * 1000) / 1000d) + "°" //
					+ "\n" + "above " + angleThreshold //
					+ " between\n" + getTime(start) + "\nand\n" + getTime(end) + "\n.";
		}

		private static String getTime(Calendar time) {
			int hour = time.get(Calendar.HOUR_OF_DAY);
			int minute = time.get(Calendar.MINUTE);
			return hour + ":" + minute;
		}
	}
}
