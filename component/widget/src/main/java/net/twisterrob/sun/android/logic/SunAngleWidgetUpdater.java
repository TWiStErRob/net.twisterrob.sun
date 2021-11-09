package net.twisterrob.sun.android.logic;

import java.util.Calendar;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.location.LocationListenerCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.util.Consumer;

import net.twisterrob.sun.algo.SunCalculator;
import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.SunAngleWidgetProvider;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.*;

public class SunAngleWidgetUpdater {

	private static final SunAngleWidgetView VIEW = new SunAngleWidgetView(new TimeProvider());
	private static final SunCalculator CALC = new SunCalculator(new PhotovoltaicSun());

	public SunAngleWidgetUpdater() {
		// no context (yet)
	}

	public SunAngleWidgetUpdater(Context context) {
		setContext(context);
	}

	private Context context;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@SuppressLint("MissingPermission") // targetSdkVersion is <23
	public void clearLocation(LocationListenerCompat fallback) {
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		LocationManagerCompat.removeUpdates(lm, fallback);
	}

	@SuppressLint("MissingPermission") // targetSdkVersion is <23
	// TODO https://developer.android.com/training/location/retrieve-current.html#GetLocation
	public Location getLocation(final @NonNull LocationListenerCompat fallback) {
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		if (location == null) {
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			String provider = lm.getBestProvider(criteria, true);
			if (provider != null) {
				location = lm.getLastKnownLocation(provider);
				if (location == null) {
					if (Log.isLoggable("Sun", Log.VERBOSE)) {
						Log.v("Sun", "No location, request update on " + provider + " for " + fallback);
					}
					LocationManagerCompat.getCurrentLocation(
							lm, provider, null, Executors.newSingleThreadExecutor(),
							new Consumer<Location>() {
								@Override public void accept(Location location) {
									fallback.onLocationChanged(location);
								}
							}
					);
				}
			} else {
				if (Log.isLoggable("Sun", Log.VERBOSE)) {
					Log.v("Sun", "No provider enabled wait for update");
				}
				LocationManagerCompat.getCurrentLocation(
						lm, LocationManager.PASSIVE_PROVIDER, null, Executors.newSingleThreadExecutor(),
						new Consumer<Location>() {
							@Override public void accept(Location location) {
								fallback.onLocationChanged(location);
							}
						}
				);
			}
		}
		return location;
	}

	public static void forceUpdateAll(Context context) {
		forceUpdate(context, SunAngleWidgetProvider.getAppWidgetIds(context));
	}

	public static void forceUpdate(Context context, int... appWidgetIds) {
		Intent intent = createUpdateIntent(context, appWidgetIds);
		context.sendBroadcast(intent);
	}

	public boolean update(int appWidgetId, @NonNull LocationListenerCompat fallback) {
		Location location = getLocation(fallback);
		if (Log.isLoggable("Sun", Log.VERBOSE)) {
			Log.v("Sun", "update(" + appWidgetId + "," + location + ")");
		}

		SunSearchResults result = null;
		SharedPreferences prefs = SunAngleWidgetProvider.getPreferences(context, appWidgetId);
		if (location != null) {
			SunSearchParams params = new SunSearchParams();
			params.latitude = location.getLatitude();
			params.longitude = location.getLongitude();
			params.thresholdAngle = prefs.getFloat(PREF_THRESHOLD_ANGLE, DEFAULT_THRESHOLD_ANGLE);
			String thresholdRelation = prefs.getString(PREF_THRESHOLD_RELATION, DEFAULT_THRESHOLD_RELATION.name());
			params.thresholdRelation = ThresholdRelation.valueOf(thresholdRelation);
			params.time = Calendar.getInstance();
			if (prefs.contains(PREF_MOCK_TIME)) {
				params.time.setTimeInMillis(prefs.getLong(PREF_MOCK_TIME, DEFAULT_MOCK_TIME));
			}
			result = CALC.find(params);
			if (prefs.contains(PREF_MOCK_ANGLE)) {
				result.current.angle = prefs.getFloat(PREF_MOCK_ANGLE, DEFAULT_MOCK_ANGLE);
			}
		}
		RemoteViews views = VIEW.createUpdateViews(context, appWidgetId, result, prefs);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return result != null;
	}

	protected static Intent createUpdateIntent(Context context, int... appWidgetIds) {
		Intent intent = new Intent(context, SunAngleWidgetProvider.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		return intent;
	}
}
