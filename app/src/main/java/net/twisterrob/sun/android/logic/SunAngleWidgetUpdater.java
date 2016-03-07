package net.twisterrob.sun.android.logic;

import java.text.*;
import java.util.*;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.*;
import android.text.SpannableString;
import android.text.style.*;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import static android.support.v4.content.ContextCompat.*;
import static android.text.Spanned.*;

import net.twisterrob.sun.algo.*;
import net.twisterrob.sun.algo.SunSearchResults.*;
import net.twisterrob.sun.android.*;
import net.twisterrob.sun.android.ui.*;
import net.twisterrob.sun.model.*;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.*;

public class SunAngleWidgetUpdater {
	private static final DecimalFormat fraction = initFractionFormat();
	private static final DateFormat time2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
	private static final DateFormat time3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	private static final LightStateMap<Integer> STATE_LABELs = new StateNameIDs();
	private static final LightStateMap<Integer> BGs = new BackgroundIDs();
	private static final LightStateMap<Integer> ANGLE_COLORs = new AngleColorIDs();
	private static final LightStateMap<Integer> STATE_COLORs = new StateColorIDs();
	private static final LightStateMap<Integer> UPDATE_COLORs = new UpdateColorIDs();
	private static final SunCalculator CALC = new SunCalculator(new PhotovoltaicSun());
	private static final Map<ThresholdRelation, Integer> RELATIONS = new EnumMap<>(ThresholdRelation.class);

	static {
		RELATIONS.put(ThresholdRelation.ABOVE, R.string.threshold_above);
		RELATIONS.put(ThresholdRelation.BELOW, R.string.threshold_below);
	}

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

	public void clearLocation(LocationListener fallback) {
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		lm.removeUpdates(fallback);
	}

	// TODO https://developer.android.com/training/location/retrieve-current.html#GetLocation
	public Location getLocation(LocationListener fallback) {
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

	public static void forceUpdateAll(Context context) {
		forceUpdate(context, SunAngleWidgetProvider.getAppWidgetIds(context));
	}

	public static void forceUpdate(Context context, int... appWidgetIds) {
		Intent intent = createUpdateIntent(context, appWidgetIds);
		context.sendBroadcast(intent);
	}

	public boolean update(int appWidgetId, LocationListener fallback) {
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
		RemoteViews views = createUpdateViews(appWidgetId, result, prefs);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return result != null;
	}

	private RemoteViews createUpdateViews(int appWidgetId, SunSearchResults results, SharedPreferences prefs) {
		Resources res = context.getResources();
		RemoteViews views;
		if (results == null) {
			views = new RemoteViews(context.getPackageName(), R.layout.widget_1x1_invalid);
			views.setTextViewText(R.id.timeUpdated, time3.format(Calendar.getInstance().getTime()));
			views.setTextViewText(R.id.state, res.getText(R.string.call_to_action_location));
			views.setOnClickPendingIntent(R.id.state, createRefreshIntent(appWidgetId));
			views.setOnClickPendingIntent(R.id.threshold, createOpenIntent(appWidgetId));
		} else {
			views = new RemoteViews(context.getPackageName(), R.layout.widget_1x1);
			LightState state = LightState.from(results.current.angle);

			views.setImageViewResource(R.id.angle_background, BGs.get(state, results.current.time));

			int angleColor = getColor(context, ANGLE_COLORs.get(state, results.current.time));
			views.setTextColor(R.id.angle, angleColor);
			views.setTextColor(R.id.angleFraction, angleColor);
			views.setTextColor(R.id.angleSign, angleColor);
			String sign = results.current.angle < 0? "-" : ""; // because I want to display ±0
			views.setTextViewText(R.id.angle, sign + Math.abs((int)results.current.angle));
			views.setTextViewText(R.id.angleFraction, fraction.format(results.current.angle));
			views.setOnClickPendingIntent(R.id.root, createRefreshIntent(appWidgetId));

			if (prefs.getBoolean(PREF_SHOW_PART_OF_DAY, DEFAULT_SHOW_PART_OF_DAY)) {
				views.setViewVisibility(R.id.state, View.VISIBLE);
				CharSequence stateText = res.getText(STATE_LABELs.get(state, results.current.time));
				views.setTextViewText(R.id.state, state == LightState.INVALID? bold(stateText) : stateText);
				views.setTextColor(R.id.state, getColor(context, STATE_COLORs.get(state, results.current.time)));
			} else {
				views.setViewVisibility(R.id.state, View.GONE);
			}

			if (prefs.getBoolean(PREF_SHOW_UPDATE_TIME, DEFAULT_SHOW_UPDATE_TIME)) {
				views.setViewVisibility(R.id.timeUpdated, View.VISIBLE);
				views.setTextViewText(R.id.timeUpdated, time3.format(results.current.time.getTime()));
				views.setTextColor(R.id.timeUpdated, getColor(context, UPDATE_COLORs.get(state, results.current.time)));
			} else {
				views.setViewVisibility(R.id.timeUpdated, View.GONE);
			}

			views.setTextViewText(R.id.threshold, formatThreshold(results));
			views.setTextViewText(R.id.timeThresholdFrom, formatThresholdTime(results, results.threshold.start));
			views.setTextViewText(R.id.timeThresholdTo, formatThresholdTime(results, results.threshold.end));
			views.setOnClickPendingIntent(R.id.threshold_container, createOpenIntent(appWidgetId));
		}
		return views;
	}

	private CharSequence formatThresholdTime(SunSearchResults results, Calendar time) {
		CharSequence result;
		if (time == null) {
			result = getContext().getString(R.string.time_2_none);
		} else {
			result = time2.format(time.getTime());
			Calendar justBefore = (Calendar)time.clone();
			justBefore.add(Calendar.MINUTE, -30); // TODO configure?
			if (results.current.time.after(justBefore) && results.current.time.before(time)) {
				result = bold(color(result, getColor(context, R.color.coming_soon)));
			}
		}
		return result;
	}

	private CharSequence formatThreshold(SunSearchResults results) {
		Calendar start = results.threshold.start;
		Calendar end = results.threshold.end;
		Calendar now = results.current.time;
		Integer thresholdResource = RELATIONS.get(results.params.thresholdRelation);
		CharSequence threshold = context.getString(thresholdResource, (int)results.params.thresholdAngle);
		if (now.after(start) && now.before(end)) {
			threshold = bold(threshold);
		}
		return threshold;
	}

	private CharSequence bold(CharSequence string) {
		SpannableString boldString = new SpannableString(string);
		boldString.setSpan(new StyleSpan(Typeface.BOLD), 0, boldString.length(), SPAN_INCLUSIVE_EXCLUSIVE);
		return boldString;
	}

	private CharSequence color(CharSequence string, int color) {
		SpannableString colorString = new SpannableString(string);
		colorString.setSpan(new ForegroundColorSpan(color), 0, colorString.length(), SPAN_INCLUSIVE_EXCLUSIVE);
		return colorString;
	}

	protected PendingIntent createOpenIntent(int appWidgetId) {
		Intent configIntent = new Intent(context, SunAngleWidgetConfiguration.class);
		configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		return PendingIntent.getActivity(context, appWidgetId, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	protected PendingIntent createRefreshIntent(int appWidgetId) {
		Intent intent = createUpdateIntent(context, appWidgetId);
		return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	protected static Intent createUpdateIntent(Context context, int... appWidgetIds) {
		Intent intent = new Intent(context, SunAngleWidgetProvider.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		return intent;
	}

	private static DecimalFormat initFractionFormat() {
		DecimalFormat nf = (DecimalFormat)NumberFormat.getInstance();
		nf.setNegativePrefix("");
		nf.setNegativeSuffix("");
		nf.setPositivePrefix("");
		nf.setPositiveSuffix("");
		nf.setMinimumIntegerDigits(0);
		nf.setMaximumIntegerDigits(0);
		nf.setMinimumFractionDigits(4);
		nf.setMaximumFractionDigits(4);
		return nf;
	}
}
