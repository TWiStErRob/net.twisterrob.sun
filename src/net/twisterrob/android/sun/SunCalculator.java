package net.twisterrob.android.sun;

import java.util.Calendar;

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
			if (result.start == null && altitude > result.angleThreshold) {
				result.start = (Calendar)running.clone();
			}
			if (result.start != null && result.end == null && altitude < result.angleThreshold) {
				result.end = (Calendar)running.clone();
			}
		}

		return result;
	}

	class SunAngle {
		Calendar lastUpdate;
		double current;

		double angleThreshold;
		Calendar start;
		Calendar end;

		@Override
		public String toString() {
			return "Now at " + ((int)(current * 1000) / 1000d) + "°" //
					+ "\n" + "above " + angleThreshold //
					+ " between\n" + getTime(start) + "\nand\n" + getTime(end) + "\n.";
		}

		private String getTime(Calendar time) {
			int hour = time.get(Calendar.HOUR_OF_DAY);
			int minute = time.get(Calendar.MINUTE);
			return hour + ":" + minute;
		}
	}
}
