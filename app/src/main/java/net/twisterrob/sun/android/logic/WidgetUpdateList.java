package net.twisterrob.sun.android.logic;

import java.util.*;

import android.location.LocationListener;

public class WidgetUpdateList {
	private final Set<Integer> toBeUpdated = new HashSet<>();

	public synchronized void remove(int... ids) {
		for (int id : ids) {
			toBeUpdated.remove(id);
		}
	}

	public synchronized void add(int... ids) {
		for (int id : ids) {
			toBeUpdated.add(id);
		}
	}

	public synchronized void catchup(SunAngleWidgetUpdater updater, LocationListener fallback) {
		for (Iterator<Integer> current = toBeUpdated.iterator(); current.hasNext(); ) {
			Integer appWidgetId = current.next();
			if (updater.update(appWidgetId, fallback)) {
				current.remove();
			}
		}
	}
}
