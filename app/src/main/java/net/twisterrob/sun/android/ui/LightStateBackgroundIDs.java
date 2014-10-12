package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.model.LightStateMap;

import static net.twisterrob.sun.android.R.drawable.*;
import static net.twisterrob.sun.model.LightState.*;

public class LightStateBackgroundIDs extends LightStateMap<Integer> {
	public LightStateBackgroundIDs() {
		put(DAY, day);
		put(NIGHT, night);

		put(CIVIL_TWILIGHT, twilight_civil);
		put(CIVIL_THRESHOLD, twilight_civil);

		put(NAUTICAL_TWILIGHT, twilight_nautical);
		put(NAUTICAL_THRESHOLD, twilight_nautical);

		put(ASTRONOMICAL_TWILIGHT, twilight_astronomical);
		put(ASTRONOMICAL_THRESHOLD, twilight_astronomical);

		putMorning(HORIZON_TRANSITION, sunrise);
		putEvening(HORIZON_TRANSITION, sunset);
	}
}
