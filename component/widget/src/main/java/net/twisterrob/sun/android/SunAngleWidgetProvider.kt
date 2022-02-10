package net.twisterrob.sun.android;

import java.util.Locale;

import javax.inject.Inject;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.location.LocationListenerCompat;

import net.twisterrob.sun.android.logic.SunAngleWidgetUpdater;
import net.twisterrob.sun.android.logic.WidgetUpdateList;

public class SunAngleWidgetProvider extends LoggingAppWidgetProvider {

	private static final String TAG = "Sun";

	@Inject WidgetComponent component = null;
	@Inject SunAngleWidgetUpdater updater = null;

	@Override
	public void onReceive(@NonNull Context context, @NonNull Intent intent) {
		if (component == null) {
			DaggerWidgetComponent.factory().create(context).inject(this);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onDeleted(@NonNull Context context, @NonNull int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			SunAngleWidgetPreferences.getPreferences(context, appWidgetId).edit().clear().apply();
		}
	}

	@Override
	public void onUpdate(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateAll(context, appWidgetIds);
	}

	private void updateAll(final @NonNull Context context, final @NonNull int... appWidgetIds) {
		try {
			WidgetUpdateList TODOs = new WidgetUpdateList();
			TODOs.add(appWidgetIds);
			TODOs.catchup(updater, new LocationListenerCompat() {
				@Override
				public void onLocationChanged(@Nullable Location location) {
					if (Log.isLoggable(TAG, Log.VERBOSE)) {
						Log.v(TAG, SunAngleWidgetProvider.this + ".onLocationChanged(" + location + ")");
					}
					updater.clearLocation(this);
					updateAll(context, appWidgetIds);
				}
			});
		} catch (Exception ex) {
			Log.e(TAG, this + ".updateAll", ex);
			Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public @NonNull String toString() {
		return String.format(Locale.ROOT, "%08x", this.hashCode());
	}
}
