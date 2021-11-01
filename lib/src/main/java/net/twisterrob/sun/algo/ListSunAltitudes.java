package net.twisterrob.sun.algo;

import java.util.*;

import net.twisterrob.sun.Sun;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

/** Compare to http://aa.usno.navy.mil/data/docs/AltAz.php */
public class ListSunAltitudes {
	public static void main(String[] args) {
		double lat = 51.519586;
		double lon = -0.068586;
		Calendar time = Calendar.getInstance();
		time.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		time.set(2014, Calendar.JUNE, 21, 0, 0, 0);

		Sun sun = new PhotovoltaicSun();
		int every = 1;
		for (int minute = 0; minute < 24 * 60 / every; ++minute) {
			double alt = sun.altitudeAngle(lat, lon, time);
			double azi = sun.azimuthAngle(lat, lon, time);
			System.out.printf("% 5d\t%tT\t%7.3f\t%7.3f\n", minute, time, alt, azi);

			time.add(Calendar.MINUTE, every);
		}
	}
}
