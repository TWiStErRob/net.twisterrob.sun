package net.twisterrob.sun.android.logic;

import java.util.Calendar;

import javax.inject.Inject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.RemoteViews;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.location.LocationListenerCompat;
import androidx.core.location.LocationManagerCompat;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import net.twisterrob.sun.algo.SunCalculator;
import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.SunAngleWidgetPreferences;

import static net.twisterrob.sun.android.SunAngleWidgetPreferences.*;

public class SunAngleWidgetUpdater {

	private final @NonNull Context context;
	private final @NonNull SunAngleWidgetView view;
	private final @NonNull SunCalculator calculator;

	@Inject
	public SunAngleWidgetUpdater(
			@NonNull Context context,
			@NonNull SunAngleWidgetView view,
			@NonNull SunCalculator calculator
	) {
		this.context = context;
		this.view = view;
		this.calculator = calculator;
	}

	@RequiresPermission(value = ACCESS_FINE_LOCATION, conditional = true /*guarded by hasLocationPermission()*/)
	public void clearLocation(@NonNull LocationListenerCompat fallback) {
		if (hasLocationPermission()) {
			LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			LocationManagerCompat.removeUpdates(lm, fallback);
		}
	}

	@SuppressWarnings("deprecation") // Cannot use LocationManagerCompat.getCurrentLocation yet:
	// tried, but it gets into infinite loop when there's no location and runs on a different thread.
	// TODO https://developer.android.com/training/location/retrieve-current.html#GetLocation
	@RequiresPermission(value = ACCESS_FINE_LOCATION, conditional = true /*guarded by hasLocationPermission()*/)
	public @Nullable Location getLocation(final @NonNull LocationListenerCompat fallback) {
		if (!hasLocationPermission()) {
			Log.w("Sun", "No location permission granted, stopping " + this + " for " + fallback);
			return null;
		}
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		// The passive provider doesn't seem to work with coarse permission only.
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
					lm.requestSingleUpdate(provider, fallback, null);
				}
			} else {
				if (Log.isLoggable("Sun", Log.VERBOSE)) {
					Log.v("Sun", "No provider enabled wait for update");
				}
				lm.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, fallback, null);
			}
		}
		return location;
	}

	private boolean hasLocationPermission() {
		boolean hasFinePermission = checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
		boolean hasCoarsePermission = checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;
		return hasFinePermission || hasCoarsePermission;
	}

	public boolean update(int appWidgetId, @NonNull LocationListenerCompat fallback) {
		Location location = getLocation(fallback);
		if (Log.isLoggable("Sun", Log.VERBOSE)) {
			Log.v("Sun", "update(" + appWidgetId + "," + location + ")");
		}

		SunSearchResults result = null;
		SharedPreferences prefs = SunAngleWidgetPreferences.getPreferences(context, appWidgetId);
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
			result = calculator.find(params);
			if (prefs.contains(PREF_MOCK_ANGLE)) {
				result.current.angle = prefs.getFloat(PREF_MOCK_ANGLE, DEFAULT_MOCK_ANGLE);
			}
		}
		RemoteViews views = view.createUpdateViews(context, appWidgetId, result, prefs);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return result != null;
	}
}
