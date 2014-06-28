package net.twisterrob.android.sun.model;
import java.util.Calendar;

public interface Sun {
	public double altitudeAngle(double lat, double lon, Calendar time);
	public double azimuthAngle(double lat, double lon, Calendar time);
	public double clockTime(double lat, double lon, Calendar time);
	public double solarTime(double lat, double lon, Calendar time);
	public double hourAngle(double lat, double lon, Calendar time);
	public double declination(double lat, double lon, Calendar time);
	public double equationOfTime(double lat, double lon, Calendar time);
}
