package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.android.R;
import net.twisterrob.sun.model.LightStateMap;

import static net.twisterrob.sun.model.LightState.*;

public class LightStateBackgroundIDs extends LightStateMap<Integer> {
	public LightStateBackgroundIDs() {
		put(DAY, R.drawable.day);
		put(NIGHT, R.drawable.night);

		put(CIVIL_TWILIGHT, R.drawable.twilight_civil);
		put(CIVIL_THRESHOLD, R.drawable.twilight_civil);

		put(NAUTICAL_TWILIGHT, R.drawable.twilight_nautical);
		put(NAUTICAL_THRESHOLD, R.drawable.twilight_nautical);

		put(ASTRONOMICAL_TWILIGHT, R.drawable.twilight_astronomical);
		put(ASTRONOMICAL_THRESHOLD, R.drawable.twilight_astronomical);

		putMorning(HORIZON_TRANSITION, R.drawable.sunrise);
		putEvening(HORIZON_TRANSITION, R.drawable.sunset);
	}
}
