package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.model.LightStateMap;

import static net.twisterrob.sun.model.LightState.*;

public class UpdateColorIDs extends LightStateMap<Integer> {
	public UpdateColorIDs() {
		put(DAY, android.R.color.darker_gray);
		put(NIGHT, android.R.color.darker_gray);

		put(CIVIL_TWILIGHT, android.R.color.darker_gray);
		put(CIVIL_THRESHOLD, android.R.color.darker_gray);

		put(NAUTICAL_TWILIGHT, android.R.color.darker_gray);
		put(NAUTICAL_THRESHOLD, android.R.color.darker_gray);

		put(ASTRONOMICAL_TWILIGHT, android.R.color.darker_gray);
		put(ASTRONOMICAL_THRESHOLD, android.R.color.darker_gray);

		put(HORIZON_TRANSITION, android.R.color.darker_gray);

		put(INVALID, android.R.color.darker_gray);
	}
}
