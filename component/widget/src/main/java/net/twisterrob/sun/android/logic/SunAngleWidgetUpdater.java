package net.twisterrob.sun.android.logic;

import java.util.Calendar;

import javax.inject.Inject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.sun.algo.SunCalculator;
import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.SunSearchParams;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.SunAngleWidgetPreferences;

import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_MOCK_ANGLE;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_MOCK_TIME;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_THRESHOLD_ANGLE;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_THRESHOLD_RELATION;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_MOCK_ANGLE;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_MOCK_TIME;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_THRESHOLD_ANGLE;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_THRESHOLD_RELATION;

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

	public boolean update(int appWidgetId, @Nullable Location location) {
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
