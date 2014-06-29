package net.twisterrob.android.sun;

import java.text.*;
import java.util.*;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.res.Resources;
import android.location.*;
import android.util.Log;
import android.widget.RemoteViews;

import net.twisterrob.android.sun.model.*;
import net.twisterrob.android.sun.model.SunCalculator.SunAngle;
import net.twisterrob.android.sun.ui.*;

public class SunAngleWidgetUpdater {
	private static final DecimalFormat fraction = initFractionFormat();
	private static final DateFormat time2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
	private static final DateFormat time3 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	private static final LightStateMap<Integer> CAPTIONs = new LightStateNameIDs();
	private static final LightStateMap<Integer> BGs = new LightStateBackgroundIDs();
	private static final LightStateMap<Integer> COLORs = new LightStateColorIDs();
	private static final SunCalculator CALC = new SunCalculator(new SunX());

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

	public void forceUpdateAll() {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName component = new ComponentName(context, SunAngleWidgetProvider.class);
		Intent intent = createUpdateIntent(appWidgetManager.getAppWidgetIds(component));
		context.sendBroadcast(intent);
	}

	public boolean update(int appWidgetId, LocationListener fallback) {
		Location location = getLocation(fallback);
		Log.v("Sun", "update(" + appWidgetId + "," + location + ")");
		SunAngle angle = null;
		if (location != null) {
			angle = CALC.find(50, location.getLatitude(), location.getLongitude(), Calendar.getInstance());
		}
		return updateViews(appWidgetId, angle);
	}

	private boolean updateViews(int appWidgetId, SunAngle angle) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sun_angle_widget);
		Resources res = context.getResources();
		if (angle != null) {
			views.setImageViewResource(R.id.angle_background, BGs.get(angle.sunState, angle.lastUpdate));
			views.setTextViewText(R.id.state, res.getText(CAPTIONs.get(angle.sunState, angle.lastUpdate)));
			views.setTextColor(R.id.angle, res.getColor(COLORs.get(angle.sunState, angle.lastUpdate)));
			views.setTextColor(R.id.angleFraction, res.getColor(COLORs.get(angle.sunState, angle.lastUpdate)));
			views.setTextViewText(R.id.angle, (angle.current < 0? "-" : "") + Math.abs((int)angle.current) + "°");
			views.setTextViewText(R.id.angleFraction, fraction.format(angle.current));
			views.setTextViewText(R.id.timeUpdated, time3.format(angle.lastUpdate.getTime()));
			views.setTextViewText(R.id.threshold, ((int)angle.angleThreshold) + "°");
			views.setTextViewText(R.id.timeThresholdFrom, time2.format(angle.start.getTime()));
			views.setTextViewText(R.id.timeThresholdTo, time2.format(angle.end.getTime()));
		} else {
			views.setTextViewText(R.id.state, res.getText(R.string.call_to_action_refresh));
			views.setTextViewText(R.id.angle, res.getText(R.string.angle_unkown));
			views.setTextViewText(R.id.angleFraction, "");
			views.setTextViewText(R.id.timeUpdated, time3.format(Calendar.getInstance().getTime()));
			views.setTextViewText(R.id.threshold, res.getText(R.string.angle_unkown));
			views.setTextViewText(R.id.timeThresholdFrom, res.getText(R.string.time_2_unknown));
			views.setTextViewText(R.id.timeThresholdTo, res.getText(R.string.time_2_unknown));
		}
		views.setOnClickPendingIntent(R.id.layoutRoot, createClickIntent(appWidgetId));
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return angle != null;
	}

	protected PendingIntent createClickIntent(int appWidgetId) {
		Intent intent = createUpdateIntent(appWidgetId);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	protected Intent createUpdateIntent(int... appWidgetIds) {
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