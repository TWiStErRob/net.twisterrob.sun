package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.model.LightStateMap;

import static net.twisterrob.sun.model.LightState.*;

public class AngleColorIDs extends LightStateMap<Integer> {
	public AngleColorIDs() {
		put(DAY, android.R.color.white);
		put(NIGHT, android.R.color.white);

		put(CIVIL_TWILIGHT, android.R.color.white);
		put(CIVIL_THRESHOLD, android.R.color.white);

		put(NAUTICAL_TWILIGHT, android.R.color.white);
		put(NAUTICAL_THRESHOLD, android.R.color.white);

		put(ASTRONOMICAL_TWILIGHT, android.R.color.white);
		put(ASTRONOMICAL_THRESHOLD, android.R.color.white);

		put(HORIZON_TRANSITION, android.R.color.white);

		put(INVALID, android.R.color.white);
	}
}
