package net.twisterrob.android.app;

import android.annotation.SuppressLint;
import android.appwidget.*;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import static android.appwidget.AppWidgetManager.*;

public abstract class WidgetConfigurationActivity extends ActionBarActivity {
	private SharedPreferences prefs;
	private int appWidgetId;
	private Intent result;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
		Log.d("WidgetConfiguration", "Editing widget: " + appWidgetId);
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

	@SuppressLint("CommitPrefEdits")
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
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
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
