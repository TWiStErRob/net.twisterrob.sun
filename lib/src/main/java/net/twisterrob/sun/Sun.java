package net.twisterrob.sun;

import java.util.Calendar;

public interface Sun {
	double altitudeAngle(double lat, double lon, Calendar time);
	double azimuthAngle(double lat, double lon, Calendar time);
	double clockTime(double lat, double lon, Calendar time);
	double solarTime(double lat, double lon, Calendar time);
	double hourAngle(double lat, double lon, Calendar time);
	double declination(double lat, double lon, Calendar time);
	double equationOfTime(double lat, double lon, Calendar time);
}
