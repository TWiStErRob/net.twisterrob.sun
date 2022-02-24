package net.twisterrob.sun.android.ui;

import net.twisterrob.sun.android.states.R;
import net.twisterrob.sun.model.LightStateMap;

import static net.twisterrob.sun.model.LightState.*;

class StateNameIDs extends LightStateMap<Integer> {
	public StateNameIDs() {
		put(DAY, R.string.light_state_day);
		put(NIGHT, R.string.light_state_night);
		put(CIVIL_TWILIGHT, R.string.light_state_twilight_civil);
		put(NAUTICAL_TWILIGHT, R.string.light_state_twilight_nautical);
		put(ASTRONOMICAL_TWILIGHT, R.string.light_state_twilight_astronomical);

		putMorning(HORIZON_TRANSITION, R.string.light_state_sunrise);
		putEvening(HORIZON_TRANSITION, R.string.light_state_sunset);

		putMorning(CIVIL_THRESHOLD, R.string.light_state_dawn_civil);
		putMorning(NAUTICAL_THRESHOLD, R.string.light_state_dawn_nautical);
		putMorning(ASTRONOMICAL_THRESHOLD, R.string.light_state_dawn_astronomical);

		putEvening(CIVIL_THRESHOLD, R.string.light_state_dusk_civil);
		putEvening(NAUTICAL_THRESHOLD, R.string.light_state_dusk_nautical);
		putEvening(ASTRONOMICAL_THRESHOLD, R.string.light_state_dusk_astronomical);

		put(INVALID, R.string.light_state_invalid);
	}
}
