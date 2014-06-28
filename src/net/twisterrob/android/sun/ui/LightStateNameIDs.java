package net.twisterrob.android.sun.ui;
import net.twisterrob.android.sun.model.LightStateMap;

import static net.twisterrob.android.sun.R.string.*;
import static net.twisterrob.android.sun.model.LightState.*;

public class LightStateNameIDs extends LightStateMap<Integer> {
	public LightStateNameIDs() {
		put(DAY, light_state_day);
		put(NIGHT, light_state_night);
		put(CIVIL_TWILIGHT, light_state_twilight_civil);
		put(NAUTICAL_TWILIGHT, light_state_twilight_nautical);
		put(ASTRONOMICAL_TWILIGHT, light_state_twilight_astronomical);

		putMorning(HORIZON_TRANSITION, light_state_sunrise);
		putEvening(HORIZON_TRANSITION, light_state_sunset);

		putMorning(CIVIL_THRESHOLD, light_state_dawn_civil);
		putMorning(NAUTICAL_THRESHOLD, light_state_dawn_nautical);
		putMorning(ASTRONOMICAL_THRESHOLD, light_state_dawn_astronomical);

		putEvening(CIVIL_THRESHOLD, light_state_dusk_civil);
		putEvening(NAUTICAL_THRESHOLD, light_state_dusk_nautical);
		putEvening(ASTRONOMICAL_THRESHOLD, light_state_dusk_astronomical);
	}
}
