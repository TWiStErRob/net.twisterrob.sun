package net.twisterrob.android.sun.ui;
import net.twisterrob.android.sun.model.LightStateMap;

import static net.twisterrob.android.sun.model.LightState.*;

public class LightStateColorIDs extends LightStateMap<Integer> {
	public LightStateColorIDs() {
		put(DAY, android.R.color.black);
		put(NIGHT, android.R.color.darker_gray);
		put(HORIZON_TRANSITION, android.R.color.black);

		put(CIVIL_TWILIGHT, android.R.color.white);
		put(CIVIL_THRESHOLD, android.R.color.white);

		put(NAUTICAL_TWILIGHT, android.R.color.white);
		put(NAUTICAL_THRESHOLD, android.R.color.white);

		put(ASTRONOMICAL_TWILIGHT, android.R.color.white);
		put(ASTRONOMICAL_THRESHOLD, android.R.color.white);

	}
}
