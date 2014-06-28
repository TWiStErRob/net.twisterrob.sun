package net.twisterrob.android.sun;

import java.text.*;
import java.util.*;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.location.*;
import android.util.Log;
import android.widget.RemoteViews;

import net.twisterrob.android.sun.SunCalculator.SunAngle;

public class SunAngleWidgetUpdater {
	private static final DecimalFormat fraction = initFractionFormat();
	private static final DateFormat time2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
	private static final DateFormat time3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	private static final LightStateMap<Integer> STATES = new LightStateNameIDs();
	private final SunCalculator calc = new SunCalculator(new SunX());

	private Context context;

	public void setContext(Context context) {
		this.context = context;
	}

	private Location getLocation(LocationListener fallback) {
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		String provider = lm.getBestProvider(new Criteria(), true);
		Location location = lm.getLastKnownLocation(provider);
		if (location == null) {
			Log.v("Sun", "update deferred for " + provider);
			lm.requestSingleUpdate(provider, fallback, null);
		}
		return location;
	}

	public boolean update(int appWidgetId, LocationListener fallback) {
		Location location = getLocation(fallback);
		Log.v("Sun", "update(" + appWidgetId + "," + location + ")");
		SunAngle angle = null;
		if (location != null) {
			angle = calc.find(50, location.getLatitude(), location.getLongitude(), Calendar.getInstance());
		}
		return updateViews(appWidgetId, angle);
	}

	private boolean updateViews(int appWidgetId, SunAngle angle) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sun_angle_widget);
		if (angle != null) {
			views.setTextViewText(R.id.state, context.getText(STATES.get(angle.sunState, angle.lastUpdate)));
			views.setTextViewText(R.id.angle, (angle.current < 0? "-" : "") + Math.abs((int)angle.current) + "°");
			views.setTextViewText(R.id.angleFraction, fraction.format(angle.current));
			views.setTextViewText(R.id.timeUpdated, time3.format(angle.lastUpdate.getTime()));
			views.setTextViewText(R.id.threshold, ((int)angle.angleThreshold) + "°");
			views.setTextViewText(R.id.timeThresholdFrom, time2.format(angle.start.getTime()));
			views.setTextViewText(R.id.timeThresholdTo, time2.format(angle.end.getTime()));
		} else {
			views.setTextViewText(R.id.state, context.getText(R.string.call_to_action_refresh));
			views.setTextViewText(R.id.angle, context.getText(R.string.angle_unkown));
			views.setTextViewText(R.id.angleFraction, "");
			views.setTextViewText(R.id.timeUpdated, time3.format(Calendar.getInstance().getTime()));
			views.setTextViewText(R.id.threshold, context.getText(R.string.angle_unkown));
			views.setTextViewText(R.id.timeThresholdFrom, context.getText(R.string.time_2_unknown));
			views.setTextViewText(R.id.timeThresholdTo, context.getText(R.string.time_2_unknown));
		}
		views.setOnClickPendingIntent(R.id.layoutRoot, createClickIntent(appWidgetId));
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return angle != null;
	}

	protected PendingIntent createClickIntent(int appWidgetId) {
		Intent intent = new Intent(context, SunAngleWidgetProvider.class);
		intent.setAction(SunAngleWidgetProvider.WIDGET_CLICKED);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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