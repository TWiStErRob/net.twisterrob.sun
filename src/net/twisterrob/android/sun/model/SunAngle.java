package net.twisterrob.android.sun.model;

import java.util.Calendar;

public class SunAngle {
	public Calendar lastUpdate;
	public double current;

	public double angleThreshold;
	public Calendar start;
	public Calendar end;
	public LightState sunState;
	public ThresholdRelation thresholdRelation;

	@Override
	public String toString() {
		return "Now at " + ((int)(current * 1000) / 1000d) + "°" //
				+ "\n" + thresholdRelation + " " + angleThreshold //
				+ " between\n" + getTime(start) + "\nand\n" + getTime(end) + "\n.";
	}

	private static String getTime(Calendar time) {
		int hour = time.get(Calendar.HOUR_OF_DAY);
		int minute = time.get(Calendar.MINUTE);
		return hour + ":" + minute;
	}
}