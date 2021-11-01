package net.twisterrob.android.app;

import android.app.Activity;
import android.appwidget.*;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.Build.*;
import android.os.Bundle;
import android.util.Log;

import static android.appwidget.AppWidgetManager.*;

public abstract class WidgetConfigurationActivity extends Activity {
	private SharedPreferences prefs;
	private int appWidgetId;
	private Intent result;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
		if (Log.isLoggable("WidgetConfiguration", Log.DEBUG)) {
			Log.d("WidgetConfiguration", "Editing widget: " + appWidgetId);
		}
		result = new Intent();
		result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, result);

		if (appWidgetId == INVALID_APPWIDGET_ID) {
			Log.w("WidgetConfiguration", "Invalid widget ID, closing");
			finish();
			// should return here, but it depends on the child class from here
		}

		prefs = onPreferencesOpen(appWidgetId);
	}

	@Override protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (savedInstanceState == null) {
			onPreferencesLoad(prefs);
		}
	}

	@Override public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
			// open and close the options menu to call onCreateOptionsMenu
			openOptionsMenu();
			closeOptionsMenu();
		}
	}

	protected void finishCommit() {
		SharedPreferences.Editor edit = prefs.edit();
		onPreferencesSave(edit);
		edit.apply(); // make sure preferences are saved before updating the widget
		setResult(RESULT_OK, result);
		finish();
		forceUpdate();
	}

	private void forceUpdate() {
		AppWidgetProviderInfo info =
				AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetInfo(appWidgetId);

		Intent intent = new Intent();
		intent.setComponent(info.provider);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetId});
		sendBroadcast(intent);
	}

	protected abstract SharedPreferences onPreferencesOpen(int appWidgetId);

	protected abstract void onPreferencesLoad(SharedPreferences prefs);

	protected abstract void onPreferencesSave(Editor prefs);

	protected final SharedPreferences getWidgetPreferences() {
		return prefs;
	}

	protected final int getAppWidgetId() {
		return appWidgetId;
	}
}
