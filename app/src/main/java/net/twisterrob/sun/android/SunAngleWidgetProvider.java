package net.twisterrob.sun.android;

import java.util.*;

import android.annotation.TargetApi;
import android.appwidget.*;
import android.content.*;
import android.location.*;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.logic.*;

public class SunAngleWidgetProvider extends AppWidgetProvider implements LocationListener {
	private static final String TAG = "Sun";

	public static final String PREF_NAME = "SunAngleWidget";
	/** String: {@link ThresholdRelation#name() ThresholdRelation constant name},
	 * default: {@link ThresholdRelation#ABOVE ABOVE} */
	public static final String PREF_THRESHOLD_RELATION = "relation";
	public static final ThresholdRelation DEFAULT_THRESHOLD_RELATION = ThresholdRelation.ABOVE;
	/** double: angle in degrees,
	 * default: {@value #DEFAULT_THRESHOLD_ANGLE} */
	public static final String PREF_THRESHOLD_ANGLE = "threshold";
	public static final float DEFAULT_THRESHOLD_ANGLE = 0;
	/** double: angle in degrees,
	 * default: {@value #DEFAULT_MOCK_ANGLE} */
	public static final String PREF_MOCK_ANGLE = "mockAngle";
	public static final float DEFAULT_MOCK_ANGLE = Float.NaN;
	/** long: {@link System#currentTimeMillis()},
	 * default: {@value #DEFAULT_MOCK_ANGLE} */
	public static final String PREF_MOCK_TIME = "mockTime";
	public static final long DEFAULT_MOCK_TIME = 520597560L * 1000;
	/** boolean: true=show,
	 * default: {@value #DEFAULT_SHOW_UPDATE_TIME} */
	public static final String PREF_SHOW_UPDATE_TIME = "showLastUpdateTime";
	public static final boolean DEFAULT_SHOW_UPDATE_TIME = false;
	/** boolean: true=show,
	 * default: {@value #DEFAULT_SHOW_PART_OF_DAY} */
	public static final String PREF_SHOW_PART_OF_DAY = "showPartOfDay";
	public static final boolean DEFAULT_SHOW_PART_OF_DAY = true;

	/**
	 * Needs to be static because random instances are created for separate onReceive calls.
	 */
	private static final SunAngleWidgetUpdater UPDATER = new SunAngleWidgetUpdater();
	private static final WidgetUpdateList TODOs = new WidgetUpdateList();

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.v(TAG, this + ".onDeleted(" + Arrays.toString(appWidgetIds) + ")");
		TODOs.remove(appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			getPreferences(context, appWidgetId).edit().clear().apply();
		}
	}

	@Override
	public void onEnabled(Context context) {
		Log.v(TAG, this + ".onEnabled");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		Log.v(TAG, this + ".onDisabled");
		super.onDisabled(context);
	}

	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent) {
		intent.getStringExtra(null); // force unparcel
		Log.v(TAG, this + ".onReceive(" + intent + " (" + intent.getExtras() + "))");
		super.onReceive(context, intent); // delegate to other on*()
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.v(TAG, this + ".onUpdate(" + Arrays.toString(appWidgetIds) + ")");
		TODOs.add(appWidgetIds);
		UPDATER.setContext(context);
		updateAll();
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		newOptions.get(null); // force unparcel
		Log.v(TAG, this + ".onAppWidgetOptionsChanged(" + appWidgetId + ", " + newOptions.toString() + ")");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.v(TAG, this + ".onLocationChanged(" + location + ")");
		UPDATER.clearLocation(this);
		updateAll();
	}

	private void updateAll() {
		try {
			TODOs.catchup(UPDATER, this);
		} catch (Exception ex) {
			Log.e(TAG, this + ".updateAll", ex);
			Toast.makeText(UPDATER.getContext(), ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public String toString() {
		return String.format(Locale.ROOT, "%08x", this.hashCode());
	}

	public void onStatusChanged(String provider, int status, Bundle extras) { /* NOP */}
	public void onProviderDisabled(String provider) { /* NOP */}
	public void onProviderEnabled(String provider) { /* NOP */}

	public static SharedPreferences getPreferences(Context context, int appWidgetId) {
		return context.getApplicationContext()
		              .getSharedPreferences(PREF_NAME + "-" + appWidgetId, Context.MODE_PRIVATE);
	}

	public static int[] getAppWidgetIds(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
		ComponentName component = new ComponentName(context.getApplicationContext(), SunAngleWidgetProvider.class);
		return appWidgetManager.getAppWidgetIds(component);
	}
}
