package net.twisterrob.sun.android;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.*;
import android.app.Activity;
import android.appwidget.*;
import android.content.Intent;
import android.graphics.*;
import android.os.Build.VERSION_CODES;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;

import static android.appwidget.AppWidgetHostView.*;
import static android.appwidget.AppWidgetManager.*;

@TargetApi(VERSION_CODES.JELLY_BEAN)
public class WidgetPreviewActivity extends Activity {
	private AppWidgetManager manager;
	private AppWidgetHost host;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_screenshot);

		manager = getInstance(getApplicationContext());
		host = new AppWidgetHost(this, 0);
		host.deleteHost();
	}

	private void newWidget(AppWidgetProviderInfo info) {
		int appWidgetId = host.allocateAppWidgetId();
		if (manager.bindAppWidgetIdIfAllowed(appWidgetId, info.provider)) {
			Intent intent = new Intent(ACTION_APPWIDGET_BIND);
			intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
			onActivityResult(RESULT_FIRST_USER, RESULT_OK, intent);
		} else {
			Intent intent = new Intent(ACTION_APPWIDGET_BIND);
			intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
			intent.putExtra(EXTRA_APPWIDGET_PROVIDER, info.provider);
			startActivityForResult(intent, RESULT_FIRST_USER);
		}
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_FIRST_USER:
				int appWidgetId = data.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
				if (resultCode == RESULT_OK) {
					createView(appWidgetId);
				} else {
					host.deleteAppWidgetId(appWidgetId);
				}
				return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void createView(int appWidgetId) {
		AppWidgetProviderInfo info = manager.getAppWidgetInfo(appWidgetId);
		final AppWidgetHostView hostView = host.createView(this, appWidgetId, info);
		ViewGroup layout = (ViewGroup)findViewById(R.id.widget);
		((View)layout.getParent()).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				screenshot(hostView.getChildAt(0));
			}
		});
		LayoutParams layoutParams = layout.getLayoutParams();
		Rect padding = getDefaultPaddingForWidget(getApplicationContext(), info.provider, null);
		layoutParams.width += padding.left + padding.right;
		layoutParams.height += padding.top + padding.bottom;
		layout.addView(hostView);
		layout.setLayoutParams(layoutParams);
	}

	@SuppressLint("SdCardPath")
	public static void screenshot(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
		try {
			File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			storageDir.mkdirs();
			File file = File.createTempFile(timeStamp, ".png", storageDir);
			@SuppressWarnings("resource")
			OutputStream stream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
			stream.close();
			String sdCardRoot = Environment.getExternalStorageDirectory().toString();
			Log.i("SCREENSHOT", "adb pull " + file.toString().replace(sdCardRoot, "/sdcard"));
		} catch (IOException e) {
			Log.e("SCREENSHOT", "Cannot save screenshot of " + view, e);
		}
	}

	@Override protected void onStart() {
		super.onStart();
		host.startListening();
	}

	@Override protected void onResume() {
		super.onResume();
		newWidget(findInfo());
	}

	@Override protected void onPause() {
		host.deleteHost();
		super.onPause();
	}
	@Override protected void onStop() {
		super.onStop();
		host.stopListening();
	}

	private AppWidgetProviderInfo findInfo() {
		AppWidgetManager manager = getInstance(this);
		for (AppWidgetProviderInfo info : manager.getInstalledProviders()) {
			if (info.provider.getClassName().equals(SunAngleWidgetProvider.class.getName())) {
				return info;
			}
		}
		return null;
	}
}
