package net.twisterrob.sun.android;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.appwidget.*;
import android.content.*;
import android.location.*;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import net.twisterrob.android.content.pref.WidgetPreferences;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.logic.*;

public class SunAngleWidgetProvider extends AppWidgetProvider implements LocationListener {
	public static final String PREF_NAME = "SunAngleWidget";
	/** String: {@link ThresholdRelation#name() ThresholdRelation constant name} */
	public static final String PREF_THRESHOLD_RELATION = "relation";
	/** double: angle in degrees */
	public static final String PREF_THRESHOLD_ANGLE = "threshold";
	/** double: angle in degrees */
	public static final String PREF_MOCK_ANGLE = "mockAngle";

	/**
	 * Needs to be static because random instances are created for separate onReceive calls.
	 */
	private static final SunAngleWidgetUpdater UPDATER = new SunAngleWidgetUpdater();
	private static final WidgetUpdateList TODOs = new WidgetUpdateList();

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v("Sun", this + ".onDeleted(" + Arrays.toString(appWidgetIds) + ")");
		TODOs.remove(appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			new WidgetPreferences(context, PREF_NAME, appWidgetId).edit().clear().apply();
		}
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
	public void onReceive(@NonNull Context context, @NonNull Intent intent) {
		Log.v("Sun", this + ".onReceive(" + intent + " (" + intent.getExtras() + "))");
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.v("Sun", this + ".onUpdate(" + Arrays.toString(appWidgetIds) + ")");
		TODOs.add(appWidgetIds);
		UPDATER.setContext(context);
		updateAll();
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		Log.v("Sun", this + ".onAppWidgetOptionsChanged(" + appWidgetId + ", " + newOptions + ")");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v("Sun", this + ".onLocationChanged(" + location + " (" + location.getExtras() + "))");
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
