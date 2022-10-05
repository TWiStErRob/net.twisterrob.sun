package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.model.LightStateMap;
import net.twisterrob.sun.states.R;

import static net.twisterrob.sun.model.LightState.*;

class BackgroundIDs extends LightStateMap<Integer> {
	public BackgroundIDs() {
		put(DAY, R.drawable.bg_day);
		put(NIGHT, R.drawable.bg_night);

		put(CIVIL_TWILIGHT, R.drawable.bg_twilight_civil);
		put(CIVIL_THRESHOLD, R.drawable.bg_twilight_civil);

		put(NAUTICAL_TWILIGHT, R.drawable.bg_twilight_nautical);
		put(NAUTICAL_THRESHOLD, R.drawable.bg_twilight_nautical);

		put(ASTRONOMICAL_TWILIGHT, R.drawable.bg_twilight_astronomical);
		put(ASTRONOMICAL_THRESHOLD, R.drawable.bg_twilight_astronomical);

		putMorning(HORIZON_TRANSITION, R.drawable.bg_sunrise);
		putEvening(HORIZON_TRANSITION, R.drawable.bg_sunset);

		put(INVALID, R.drawable.bg_invalid);
	}
}
// These Sun color values are from Jeremy Birn's book "Digital Lighting & Rendering"
// sunrise sunset=182,126,91
// noon=192,191,173
// clouds, haze=189,190,192
// overcast=174,183,190
