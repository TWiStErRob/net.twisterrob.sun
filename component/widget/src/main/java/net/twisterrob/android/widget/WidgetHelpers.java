package net.twisterrob.android.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class WidgetHelpers {

	public static @NonNull Intent createUpdateIntent(
			@NonNull Context context,
			@NonNull Class<? extends AppWidgetProvider> target,
			@NonNull int... appWidgetIds
	) {
		ComponentName provider = new ComponentName(context.getApplicationContext(), target);
		return createUpdateIntent(provider, appWidgetIds);
	}

	public static @NonNull Intent createUpdateIntent(
			@NonNull ComponentName target,
			@NonNull int... appWidgetIds
	) {
		Intent intent = new Intent();
		intent.setComponent(target);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		return intent;
	}

	public static @NonNull int[] getAppWidgetIds(
			@NonNull Context context,
			@NonNull Class<? extends AppWidgetProvider> target
	) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
		ComponentName component = new ComponentName(context.getApplicationContext(), target);
		return appWidgetManager.getAppWidgetIds(component);
	}
}
