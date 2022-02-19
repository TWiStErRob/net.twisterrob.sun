package net.twisterrob.sun.android;

import java.util.Locale;

import javax.inject.Inject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.location.LocationListenerCompat;

import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.logic.SunAngleWidgetUpdater;
import net.twisterrob.sun.android.logic.WidgetUpdateList;

public class SunAngleWidgetProvider extends LoggingAppWidgetProvider {

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

	@Inject WidgetComponent component = null;

	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent) {
		if (component == null) {
			DaggerWidgetComponent.factory().create(context).inject(this);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		TODOs.remove(appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			getPreferences(context, appWidgetId).edit().clear().apply();
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		TODOs.add(appWidgetIds);
		UPDATER.setContext(context);
		updateAll(context);
	}

	private void updateAll(final @NonNull Context context) {
		try {
			TODOs.catchup(UPDATER, new LocationListenerCompat() {
				@Override
				public void onLocationChanged(@Nullable Location location) {
					if (Log.isLoggable(TAG, Log.VERBOSE)) {
						Log.v(TAG, SunAngleWidgetProvider.this + ".onLocationChanged(" + location + ")");
					}
					UPDATER.clearLocation(this);
					updateAll(context);
				}
			});
		} catch (Exception ex) {
			Log.e(TAG, this + ".updateAll", ex);
			Toast.makeText(UPDATER.getContext(), ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public @NonNull String toString() {
		return String.format(Locale.ROOT, "%08x", this.hashCode());
	}

	public static @NonNull SharedPreferences getPreferences(@NonNull Context context, int appWidgetId) {
		return context.getApplicationContext()
		              .getSharedPreferences(PREF_NAME + "-" + appWidgetId, Context.MODE_PRIVATE);
	}
}
