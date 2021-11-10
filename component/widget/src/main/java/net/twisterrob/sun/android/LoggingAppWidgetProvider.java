package net.twisterrob.sun.android;

import java.util.Arrays;
import java.util.Locale;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

public class LoggingAppWidgetProvider extends AppWidgetProvider {
	private final String tag;
	protected LoggingAppWidgetProvider() {
		tag = getClass().getSimpleName();
	}
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onDeleted(" + Arrays.toString(appWidgetIds) + ")");
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onEnabled");
		}
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onDisabled");
		}
		super.onDisabled(context);
	}

	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			intent.getStringExtra(null); // force unparcel
			Log.v(tag, this + ".onReceive(" + intent + " (" + intent.getExtras() + "))");
		}
		super.onReceive(context, intent); // delegate to other on*()
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onUpdate(" + Arrays.toString(appWidgetIds) + ")");
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			newOptions.get(null); // force unparcel
			Log.v(tag, this + ".onAppWidgetOptionsChanged(" + appWidgetId + ", " + newOptions.toString() + ")");
		}
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	@Override
	public String toString() {
		return String.format(Locale.ROOT, "%08x", this.hashCode());
	}
}
