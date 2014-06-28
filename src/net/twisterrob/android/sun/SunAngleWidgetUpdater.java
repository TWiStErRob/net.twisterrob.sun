package net.twisterrob.android.sun;

import java.text.*;
import java.util.*;

import static java.lang.String.*;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.location.*;
import android.util.Log;
import android.widget.RemoteViews;

import net.twisterrob.android.sun.SunCalculator.SunAngle;

public class SunAngleWidgetUpdater {
	private static final DecimalFormat fraction = initFractionFormat();
	private static final DateFormat time = new SimpleDateFormat("HH:mm", Locale.getDefault());

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
			SunCalculator calc = new SunCalculator(new SunX());
			angle = calc.find(50, location.getLatitude(), location.getLongitude(), Calendar.getInstance());
		}
		return updateViews(appWidgetId, angle);
	}

	private boolean updateViews(int appWidgetId, SunAngle angle) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sun_angle_widget);
		if (angle != null) {
			views.setTextViewText(R.id.angle, ((int)angle.current) + "°");
			views.setTextViewText(R.id.angleFraction, fraction.format(angle.current));
			views.setTextViewText(R.id.timeUpdated, format("%tT", angle.lastUpdate));
			views.setTextViewText(R.id.treshold, ((int)angle.angleThreshold) + "°");
			views.setTextViewText(R.id.timeTresholdFrom, time.format(angle.start.getTime()));
			views.setTextViewText(R.id.timeTresholdTo, time.format(angle.end.getTime()));
		} else {
			views.setTextViewText(R.id.angle, "??°");
			views.setTextViewText(R.id.angleFraction, "");
			views.setTextViewText(R.id.timeUpdated, format("%1$tH:%1$tm", Calendar.getInstance()));
			views.setTextViewText(R.id.treshold, "50°");
			views.setTextViewText(R.id.timeTresholdFrom, "??:??");
			views.setTextViewText(R.id.timeTresholdTo, "??:??");
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