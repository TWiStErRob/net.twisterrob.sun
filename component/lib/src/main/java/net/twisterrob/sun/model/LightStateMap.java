package net.twisterrob.sun.model;

import java.util.*;

import androidx.annotation.NonNull;

public class LightStateMap<T> {
	private final Map<LightState, T> MORNING_STATE_IDs = new EnumMap<>(LightState.class);
	private final Map<LightState, T> EVENING_STATE_IDs = new EnumMap<>(LightState.class);

	public void put(LightState state, T value) {
		putMorning(state, value);
		putEvening(state, value);
	}

	public void putMorning(LightState state, T value) {
		MORNING_STATE_IDs.put(state, value);
	}

	public void putEvening(LightState state, T value) {
		EVENING_STATE_IDs.put(state, value);
	}

	public @NonNull T get(@NonNull LightState state, @NonNull Calendar time) {
		if (time.get(Calendar.AM_PM) == Calendar.AM) {
			return MORNING_STATE_IDs.get(state);
		} else {
			return EVENING_STATE_IDs.get(state);
		}
	}
}
