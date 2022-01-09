package net.twisterrob.android.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import androidx.annotation.NonNull;

public class WidgetHelpers {

	public static @NonNull Intent createUpdateIntent(@NonNull ComponentName target, @NonNull int... appWidgetIds) {
		Intent intent = new Intent();
		intent.setComponent(target);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		return intent;
	}
}
