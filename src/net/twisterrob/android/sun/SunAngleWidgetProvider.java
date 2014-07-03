package net.twisterrob.android.sun;
import java.util.Arrays;

import android.appwidget.*;
import android.content.*;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.twisterrob.android.sun.model.SunSearchResults.ThresholdRelation;

public class SunAngleWidgetProvider extends AppWidgetProvider implements LocationListener {
	public static final String PREF_NAME = SunAngleWidgetProvider.class.getName();
	/** String: {@link ThresholdRelation#name() ThresholdRelation constant name} */
	public static final String PREF_THRESHOLD_RELATION = "relation";
	/** double: angle in degrees */
	public static final String PREF_THRESHOLD_ANGLE = "threshold";

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
		Log.v("Sun", this + ".onEnabled");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		Log.v("Sun", this + ".onDisabled");
		super.onDisabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("Sun", this + ".onReceive(" + intent + ")");
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.v("Sun", this + ".onUpdate(" + Arrays.toString(appWidgetIds) + ")");
		TODOs.add(appWidgetIds);
		UPDATER.setContext(context);
		updateAll();
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
			Toast.makeText(UPDATER.getContext(), ex.toString(), Toast.LENGTH_LONG).show();
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
