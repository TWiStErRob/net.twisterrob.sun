package net.twisterrob.sun.android;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.*;
import android.app.Activity;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.os.Build.*;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class WidgetPreviewActivity extends Activity {
	private AppWidgetManager manager;
	private AppWidgetHost host;
	private ViewGroup layout;
	private int appWidgetId;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);

		layout = (ViewGroup)findViewById(R.id.widget);
		((View)layout.getParent()).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				ViewGroup hostView = (ViewGroup)layout.getChildAt(0);
				View widgetView = hostView.getChildAt(0);
				screenshot(widgetView);
			}
		});
		addWidgetPadding(layout);

		manager = AppWidgetManager.getInstance(getApplicationContext());
		host = new AppWidgetHost(getApplicationContext(), 0);
		host.deleteHost(); // clean up leftovers, if any
		appWidgetId = host.allocateAppWidgetId();
		bindWidget(appWidgetId, new ComponentName(getApplicationContext(), SunAngleWidgetProvider.class));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	private void bindWidget(int appWidgetId, ComponentName provider) {
		if (!manager.bindAppWidgetIdIfAllowed(appWidgetId, provider)) {
			Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider);
			startActivityForResult(intent, RESULT_FIRST_USER);
		}
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_FIRST_USER:
				int appWidgetId =
						data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
				if (resultCode == RESULT_OK) {
					resetView(appWidgetId);
				} else {
					host.deleteAppWidgetId(appWidgetId);
				}
				return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void resetView(int appWidgetId) {
		AppWidgetProviderInfo info = manager.getAppWidgetInfo(appWidgetId);
		AppWidgetHostView hostView = host.createView(getApplicationContext(), appWidgetId, info);
		layout.removeAllViews();
		layout.addView(hostView);
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	private void addWidgetPadding(ViewGroup layout) {
		LayoutParams layoutParams = layout.getLayoutParams();
		if (VERSION_CODES.JELLY_BEAN <= VERSION.SDK_INT) {
			Rect padding =
					AppWidgetHostView.getDefaultPaddingForWidget(getApplicationContext(), getComponentName(), null);
			layoutParams.width += padding.left + padding.right;
			layoutParams.height += padding.top + padding.bottom;
		}
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
		resetView(appWidgetId); // need to recreate the view because stop clears them
	}

	@Override protected void onStop() {
		host.stopListening();
		super.onStop();
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		host.deleteHost();
	}
}
