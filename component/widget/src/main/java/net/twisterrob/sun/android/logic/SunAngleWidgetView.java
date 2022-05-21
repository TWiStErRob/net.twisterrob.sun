package net.twisterrob.sun.android.logic;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

import javax.inject.Inject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.RemoteViews;

import static android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static androidx.core.content.ContextCompat.getColor;

import net.twisterrob.android.widget.WidgetHelpers;
import net.twisterrob.sun.algo.SunSearchResults;
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.android.SunAngleWidgetProvider;
import net.twisterrob.sun.android.logic.SunAngleFormatter.Result;
import net.twisterrob.sun.android.ui.UiStates;
import net.twisterrob.sun.android.widget.R;
import net.twisterrob.sun.model.LightState;

import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_SHOW_PART_OF_DAY;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_SHOW_UPDATE_TIME;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_SHOW_PART_OF_DAY;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_SHOW_UPDATE_TIME;

public class SunAngleWidgetView {

	private static final Map<ThresholdRelation, Integer> RELATIONS = new EnumMap<>(ThresholdRelation.class);

	static {
		RELATIONS.put(ThresholdRelation.ABOVE, R.string.threshold_above);
		RELATIONS.put(ThresholdRelation.BELOW, R.string.threshold_below);
	}

	private final @NonNull TimeProvider times;
	private final @NonNull UiStates states;
	private final @NonNull SunAngleFormatter formatter;

	@Inject
	public SunAngleWidgetView(
			@NonNull TimeProvider times,
			@NonNull UiStates states,
			@NonNull SunAngleFormatter formatter
	) {
		this.times = times;
		this.states = states;
		this.formatter = formatter;
	}

	@NonNull RemoteViews createUpdateViews(
			@NonNull Context context,
			int appWidgetId,
			@Nullable SunSearchResults results,
			@NonNull SharedPreferences prefs
	) {
		Resources res = context.getResources();
		RemoteViews views;
		if (results == null) {
			views = new RemoteViews(context.getPackageName(), R.layout.widget_1x1_invalid);
			views.setTextViewText(R.id.timeUpdated, formatter.formatTime3(times.now().getTime()));
			views.setTextViewText(R.id.state, res.getText(R.string.call_to_action_location));
			views.setTextViewText(R.id.threshold, res.getText(R.string.call_to_action_location_fix));
			views.setOnClickPendingIntent(R.id.state, createRefreshIntent(context, appWidgetId));
			views.setOnClickPendingIntent(R.id.threshold, createOpenIntent(context, appWidgetId));
		} else {
			views = new RemoteViews(context.getPackageName(), R.layout.widget_1x1);
			LightState state = LightState.from(results.current.angle);

			views.setImageViewResource(R.id.angle_background, states.getBackground(state, results.current.time));

			int angleColor = getColor(context, states.getAngleColor(state, results.current.time));
			views.setTextColor(R.id.angle, angleColor);
			views.setTextColor(R.id.angleFraction, angleColor);
			views.setTextColor(R.id.angleSign, angleColor);
			Result angle = formatter.formatFraction(results.current.angle);
			views.setTextViewText(R.id.angle, angle.angle);
			views.setTextViewText(R.id.angleFraction, angle.fraction);
			views.setOnClickPendingIntent(R.id.root, createRefreshIntent(context, appWidgetId));

			if (prefs.getBoolean(PREF_SHOW_PART_OF_DAY, DEFAULT_SHOW_PART_OF_DAY)) {
				views.setViewVisibility(R.id.state, View.VISIBLE);
				CharSequence stateText = res.getText(states.getStateLabel(state, results.current.time));
				views.setTextViewText(R.id.state, state == LightState.INVALID? bold(stateText) : stateText);
				views.setTextColor(R.id.state, getColor(context, states.getStateColor(state, results.current.time)));
			} else {
				views.setViewVisibility(R.id.state, View.GONE);
			}

			if (prefs.getBoolean(PREF_SHOW_UPDATE_TIME, DEFAULT_SHOW_UPDATE_TIME)) {
				views.setViewVisibility(R.id.timeUpdated, View.VISIBLE);
				views.setTextViewText(R.id.timeUpdated, formatter.formatTime3(results.current.time.getTime()));
				views.setTextColor(R.id.timeUpdated,
						getColor(context, states.getUpdateColor(state, results.current.time)));
			} else {
				views.setViewVisibility(R.id.timeUpdated, View.GONE);
			}

			views.setTextViewText(R.id.threshold, formatThreshold(results, context));
			views.setTextViewText(R.id.timeThresholdFrom,
					formatThresholdTime(context, results, results.threshold.start));
			views.setTextViewText(R.id.timeThresholdTo, formatThresholdTime(context, results, results.threshold.end));
			views.setOnClickPendingIntent(R.id.threshold_container, createOpenIntent(context, appWidgetId));
		}
		return views;
	}

	private @NonNull CharSequence formatThresholdTime(
			@NonNull Context context,
			@NonNull SunSearchResults results,
			@Nullable Calendar time
	) {
		CharSequence result;
		if (time == null) {
			result = context.getString(R.string.time_2_none);
		} else {
			result = formatter.formatTime2(time.getTime());
			Calendar justBefore = (Calendar)time.clone();
			// https://github.com/TWiStErRob/net.twisterrob.sun/issues/17
			justBefore.add(Calendar.MINUTE, -30); // TODO configure?
			if (results.current.time.after(justBefore) && results.current.time.before(time)) {
				result = bold(color(result, getColor(context, R.color.coming_soon)));
			}
		}
		return result;
	}

	private static @NonNull CharSequence formatThreshold(@NonNull SunSearchResults results, @NonNull Context context) {
		Calendar start = results.threshold.start;
		Calendar end = results.threshold.end;
		Calendar now = results.current.time;
		int thresholdResource = RELATIONS.get(results.params.thresholdRelation);
		CharSequence threshold = context.getString(thresholdResource, (int)results.params.thresholdAngle);
		if (now.after(start) && now.before(end)) {
			threshold = bold(threshold);
		}
		return threshold;
	}

	private static @NonNull CharSequence bold(@NonNull CharSequence string) {
		SpannableString boldString = new SpannableString(string);
		boldString.setSpan(new StyleSpan(Typeface.BOLD), 0, boldString.length(), SPAN_INCLUSIVE_EXCLUSIVE);
		return boldString;
	}

	private static @NonNull CharSequence color(@NonNull CharSequence string, int color) {
		Spannable colorString = new SpannableString(string);
		colorString.setSpan(new ForegroundColorSpan(color), 0, colorString.length(), SPAN_INCLUSIVE_EXCLUSIVE);
		return colorString;
	}

	private static @NonNull PendingIntent createOpenIntent(@NonNull Context context, int appWidgetId) {
		try {
			Class<?> SAWC = Class.forName("net.twisterrob.sun.android.SunAngleWidgetConfiguration");
			Intent configIntent = new Intent(context, SAWC);
			configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
			return PendingIntent.getActivity(context, appWidgetId, configIntent, flags);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Missing configuration activity.");
		}
	}

	private static @NonNull PendingIntent createRefreshIntent(@NonNull Context context, int appWidgetId) {
		Intent intent = WidgetHelpers.createUpdateIntent(context, SunAngleWidgetProvider.class, appWidgetId);
		int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
		return PendingIntent.getBroadcast(context, appWidgetId, intent, flags);
	}
}
