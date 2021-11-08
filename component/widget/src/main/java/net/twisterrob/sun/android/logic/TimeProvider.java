package net.twisterrob.sun.android.logic;

import java.util.Calendar;

import androidx.annotation.NonNull;

public class TimeProvider {

	public @NonNull Calendar now() {
		return Calendar.getInstance();
	}
}
