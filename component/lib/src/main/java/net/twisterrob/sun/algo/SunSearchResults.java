package net.twisterrob.sun.algo;

import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SunSearchResults {
	public @NonNull SunSearchParams params;

	public @NonNull Moment current;
	public @NonNull Moment minimum;
	public @NonNull Moment maximum;

	public @NonNull Range threshold;
	public @NonNull Range horizon;

	public SunSearchResults(
			@NonNull SunSearchParams params,
			@NonNull Moment current,
			@NonNull Moment minimum,
			@NonNull Moment maximum,
			@NonNull Range threshold,
			@NonNull Range horizon
	) {
		this.params = params;
		this.current = current;
		this.minimum = minimum;
		this.maximum = maximum;
		this.threshold = threshold;
		this.horizon = horizon;
	}
	public SunSearchResults(@NonNull SunSearchParams params) {
		this.params = params;
	}

	@Override
	public @NonNull String toString() {
		return getTime(current.time) + " at " + ((int)(current.angle * 1000) / 1000d)
				+ "°" //
				+ ", " + params.thresholdRelation + " " + params.thresholdAngle + " between "
				+ getTime(threshold.start) + " and " + getTime(threshold.end) + ".";
	}

	private static String getTime(Calendar time) {
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		return getTimePart(hour) + ":" + getTimePart(minute);
	}

	private static String getTimePart(int part) {
		return part > 9? String.valueOf(part) : "0" + part;
	}

	public static SunSearchResults unknown() {
		Calendar now = Calendar.getInstance();
		SunSearchParams params = new SunSearchParams(Double.NaN, Double.NaN, now);
		SunSearchResults results = new SunSearchResults(params);
		Calendar startOfDay = SunCalculator.startOfDay(now);
		Calendar endOfDay = SunCalculator.endOfDay(now);
		Range wholeDay = new Range(startOfDay, endOfDay);
		results.threshold = wholeDay;
		results.horizon = wholeDay;
		results.minimum = new Moment(startOfDay, -90);
		results.maximum = new Moment(endOfDay, +90);
		results.current = new Moment(now, 0);
		return results;
	}

	public static class SunSearchParams implements Cloneable {
		public @NonNull Calendar time;
		public double latitude;
		public double longitude;
		public double thresholdAngle;
		public @Nullable ThresholdRelation thresholdRelation;

		public SunSearchParams() {
		}
		public SunSearchParams(
				double latitude,
				double longitude,
				@NonNull Calendar time
		) {
			this(latitude, longitude, time, null, 0);
		}
		public SunSearchParams(
				double latitude,
				double longitude,
				@NonNull Calendar time,
				@Nullable ThresholdRelation relation,
				double threshold
		) {
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
		public boolean hasLocation() {
			return !Double.isNaN(latitude) && !Double.isNaN(longitude);
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
