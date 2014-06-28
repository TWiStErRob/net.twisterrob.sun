package net.twisterrob.android.sun;

import java.util.*;

import android.location.LocationListener;

public class Todos {
	private final Set<Integer> TODO = new HashSet<Integer>();

	public synchronized void remove(int... ids) {
		for (int i = 0; i < ids.length; i++) {
			TODO.remove(ids[i]);
		}
	}

	public synchronized void add(int... ids) {
		for (int i = 0; i < ids.length; i++) {
			TODO.add(ids[i]);
		}
	}

	public synchronized void catchup(SunAngleWidgetUpdater updater, LocationListener fallback) {
		for (Iterator<Integer> current = TODO.iterator(); current.hasNext();) {
			Integer appWidgetId = current.next();
			if (updater.update(appWidgetId, fallback)) {
				current.remove();
			}
		}
	}
}
