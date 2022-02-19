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
import androidx.annotation.Nullable;

public class LoggingAppWidgetProvider extends AppWidgetProvider {

	private final @NonNull String tag;

	protected LoggingAppWidgetProvider() {
		tag = getClass().getSimpleName();
	}

	@Override
	public void onDeleted(@NonNull Context context, @NonNull int[] appWidgetIds) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onDeleted(" + Arrays.toString(appWidgetIds) + ")");
		}
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(@NonNull Context context) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onEnabled");
		}
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(@NonNull Context context) {
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
	public void onUpdate(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onUpdate(" + Arrays.toString(appWidgetIds) + ")");
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	@Override
	public void onAppWidgetOptionsChanged(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, int appWidgetId,
			@Nullable Bundle newOptions) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			if (newOptions != null) newOptions.get(null); // force unparcel
			Log.v(tag, this + ".onAppWidgetOptionsChanged(" + appWidgetId + ", " + newOptions + ")");
		}
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
	}

	@TargetApi(VERSION_CODES.LOLLIPOP)
	@Override
	public void onRestored(@NonNull Context context, @NonNull int[] oldWidgetIds, @NonNull int[] newWidgetIds) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, this + ".onRestored(" + Arrays.toString(oldWidgetIds) + "," + Arrays.toString(newWidgetIds) + ")");
		}
		super.onRestored(context, oldWidgetIds, newWidgetIds);
	}

	@Override
	public @NonNull String toString() {
		return String.format(Locale.ROOT, "%08x", this.hashCode());
	}
}
