package net.twisterrob.sun.android;

import java.util.*;

import android.location.LocationListener;

public class WidgetUpdateList {
	private final Set<Integer> toBeUpdated = new HashSet<>();

	public synchronized void remove(int... ids) {
		for (int i = 0; i < ids.length; i++) {
			toBeUpdated.remove(ids[i]);
		}
	}

	public synchronized void add(int... ids) {
		for (int i = 0; i < ids.length; i++) {
			toBeUpdated.add(ids[i]);
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
