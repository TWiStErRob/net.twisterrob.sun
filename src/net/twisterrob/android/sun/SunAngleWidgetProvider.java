package net.twisterrob.android.sun;
import java.util.Arrays;

import android.appwidget.*;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.util.Log;

public class SunAngleWidgetProvider extends AppWidgetProvider implements LocationListener {
	public static final String WIDGET_CLICKED = "net.twisterrob.sun.APPWIDGET_UPDATE";

	/**
	 * Needs to be static because random instances are created for separate onReceive calls.
	 */
	private static final SunAngleWidgetUpdater UPDATER = new SunAngleWidgetUpdater();
	private static final WidgetUpdateList TODOs = new WidgetUpdateList();

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v("Sun", this + ".onDeleted(" + Arrays.toString(appWidgetIds) + ")");
		TODOs.remove(appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.v("Sun", this + ".onEnabled");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.v("Sun", this + ".onDisabled");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Sun", this + ".onReceive(" + intent + ")");

		if (WIDGET_CLICKED.equals(intent.getAction())) {
			try {
				UPDATER.setContext(context);
				int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
				if (!UPDATER.update(widgetId, this)) {
					TODOs.add(widgetId);
				}
			} catch (Exception ex) {
				Log.e("Sun", this + ".onReceive", ex);
			}
		} else {
			super.onReceive(context, intent);
		}
	}

	@Override
	public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.v("Sun", this + ".onUpdate(" + Arrays.toString(appWidgetIds) + ")");
		try {
			TODOs.add(appWidgetIds);
			UPDATER.setContext(context);
			updateAll();
		} catch (Exception ex) {
			Log.e("Sun", this + ".onUpdate", ex);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v("Sun", this + ".onLocationChanged");
		updateAll();
	}

	private void updateAll() {
		try {
			TODOs.catchup(UPDATER, this);
		} catch (Exception ex) {
			Log.e("Sun", this + ".updateAll", ex);
		}
	}

	@Override
	public String toString() {
		return String.format("%08x", this.hashCode());
	}

	public void onStatusChanged(String provider, int status, Bundle extras) { /* NOP */}
	public void onProviderDisabled(String provider) { /* NOP */}
	public void onProviderEnabled(String provider) { /* NOP */}
}
