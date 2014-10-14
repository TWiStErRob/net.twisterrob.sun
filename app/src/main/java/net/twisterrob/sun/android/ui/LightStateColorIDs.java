package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.model.LightStateMap;

import static net.twisterrob.sun.model.LightState.*;

public class LightStateColorIDs extends LightStateMap<Integer> {
	public LightStateColorIDs() {
		put(DAY, android.R.color.white);
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
// These Sun color values are from Jeremy Birn's book "Digital Lighting & Rendering"
// sunrise sunset=182,126,91
// noon=192,191,173
// clouds, haze=189,190,192
// overcast=174,183,190