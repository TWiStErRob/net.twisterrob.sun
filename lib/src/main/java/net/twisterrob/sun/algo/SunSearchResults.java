package net.twisterrob.sun.algo;

import java.util.Calendar;

public class SunSearchResults {
	public SunSearchParams params;

	public Moment current;
	public Moment minimum;
	public Moment maximum;

	public Range threshold;
	public Range horizon;

	public SunSearchResults(SunSearchParams params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return getTime(current.time) + " at " + ((int)(current.angle * 1000) / 1000d)
				+ "°" //
				+ "\n" + params.thresholdRelation + " " + params.thresholdAngle + " between\n"
				+ getTime(threshold.start) + "\nand\n" + getTime(threshold.end) + "\n.";
	}

	private static String getTime(Calendar time) {
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		return hour + ":" + minute;
	}

	public static class SunSearchParams implements Cloneable {
		public Calendar time;
		public double latitude;
		public double longitude;
		public double thresholdAngle;
		public ThresholdRelation thresholdRelation;

		public SunSearchParams() {
		}
		public SunSearchParams(double latitude, double longitude, Calendar time) {
			this(latitude, longitude, time, null, 0);
		}
		public SunSearchParams(double latitude, double longitude, Calendar time, ThresholdRelation relation,
				double threshold) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.time = time;
			this.thresholdRelation = relation;
			this.thresholdAngle = threshold;
		}

		@Override
		@SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
		public SunSearchParams clone() {
			try {
				return (SunSearchParams)super.clone();
			} catch (CloneNotSupportedException ex) {
				throw new InternalError();
			}
		}
	}

	public static class Moment {
		public Calendar time;
		public double angle;

		public Moment() {
		}
		public Moment(Calendar time, double angle) {
			this.time = time;
			this.angle = angle;
		}
	}

	public static class Range {
		public Calendar start;
		public Calendar end;

		public Range() {
		}
		public Range(Calendar start, Calendar end) {
			this.start = start;
			this.end = end;
		}
	}

	public enum ThresholdRelation {
		ABOVE,
		BELOW
	}
}