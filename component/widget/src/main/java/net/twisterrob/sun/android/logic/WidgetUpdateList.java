package net.twisterrob.sun.android.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.location.LocationListenerCompat;

public class WidgetUpdateList {

	private final @NonNull Set<Integer> toBeUpdated = new HashSet<>();

	public synchronized void remove(@NonNull int... ids) {
		for (int id : ids) {
			toBeUpdated.remove(id);
		}
	}

	public synchronized void add(@NonNull int... ids) {
		for (int id : ids) {
			toBeUpdated.add(id);
		}
	}

	public synchronized void catchup(@NonNull SunAngleWidgetUpdater updater, @NonNull LocationListenerCompat fallback) {
		for (Iterator<Integer> current = toBeUpdated.iterator(); current.hasNext(); ) {
			Integer appWidgetId = current.next();
			if (updater.update(appWidgetId, fallback)) {
				current.remove();
			}
		}
	}
}
