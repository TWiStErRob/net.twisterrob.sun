package net.twisterrob.sun.android;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.appwidget.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import static android.appwidget.AppWidgetManager.*;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;
import net.twisterrob.sun.preview.R;

import static net.twisterrob.sun.android.SunAngleWidgetPreferences.DEFAULT_MOCK_TIME;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_MOCK_TIME;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_SHOW_PART_OF_DAY;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_SHOW_UPDATE_TIME;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_THRESHOLD_ANGLE;
import static net.twisterrob.sun.android.SunAngleWidgetPreferences.PREF_THRESHOLD_RELATION;

public class WidgetScreenshotActivity extends Activity {
	private static final String TAG = "WidgetScreenshot";

	private AppWidgetManager manager;
	private AppWidgetHost host;
	private ViewGroup layout;
	private SeekBar widthBar;
	private TextView widthDisplay;
	private SeekBar heightBar;
	private TextView heightDisplay;
	private int appWidgetId;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);

		widthDisplay = findViewById(R.id.widthDisplay);
		widthBar = findViewById(R.id.width);
		widthBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateSizeDisplay(widthDisplay, progress);
				updateSize();
			}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		heightDisplay = findViewById(R.id.heightDisplay);
		heightBar = findViewById(R.id.height);
		heightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateSizeDisplay(heightDisplay, progress);
				updateSize();
			}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {

			}
			@Override public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		Spinner presets = findViewById(R.id.preset);
		presets.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String[] presets = getResources().getStringArray(R.array.widget_size_preset_values);
				String[] preset = presets[position].split("x");
				float width = Float.parseFloat(preset[0]);
				float height = Float.parseFloat(preset[1]);
				widthBar.setProgress(unMapProgress((int)width));
				heightBar.setProgress(unMapProgress((int)height));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		presets.setSelection(0);

		layout = findViewById(R.id.widget);
		layout.setOnClickListener(_ -> {
			ViewGroup hostView = (ViewGroup)layout.getChildAt(0);
			View widgetView = hostView.getChildAt(0);
			screenshot(widgetView);
		});

		manager = AppWidgetManager.getInstance(getApplicationContext());
		host = new AppWidgetHost(getApplicationContext(), 0);
		host.deleteHost(); // clean up leftovers, if any
		appWidgetId = host.allocateAppWidgetId();
		SunAngleWidgetPreferences
				.getPreferences(this, appWidgetId)
				.edit()
				.putLong(PREF_MOCK_TIME, DEFAULT_MOCK_TIME)
				.putString(PREF_THRESHOLD_RELATION, ThresholdRelation.ABOVE.name())
				.putFloat(PREF_THRESHOLD_ANGLE, 0)
				.putBoolean(PREF_SHOW_UPDATE_TIME, true)
				.putBoolean(PREF_SHOW_PART_OF_DAY, true)
				.apply();
		bindWidget(appWidgetId, new ComponentName(getApplicationContext(), SunAngleWidgetProvider.class));
	}

	private void updateSizeDisplay(TextView sizeDisplay, int progress) {
		progress = mapProgress(progress);
		sizeDisplay.setText(String.format(Locale.ROOT, "%ddp / %.0fpx", progress, dipToPix(progress)));
	}

	private int mapProgress(int progress) {
		return progress + 62;
	}
	private int unMapProgress(int value) {
		return value - 62;
	}

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
				if (data != null) {
					int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
					if (resultCode == RESULT_OK) {
						resetView(appWidgetId);
					} else {
						host.deleteAppWidgetId(appWidgetId);
					}
				}
				return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void resetView(int appWidgetId) {
		AppWidgetProviderInfo info = manager.getAppWidgetInfo(appWidgetId);
		if (info == null) {
			Log.w(TAG, "Cannot get widget info for " + appWidgetId);
		}
		AppWidgetHostView hostView = host.createView(getApplicationContext(), appWidgetId, info);
		if (hostView == null) {
			Log.w(TAG, host + " failed to create widget view for " + appWidgetId);
		}
		layout.removeAllViews();
		layout.addView(hostView);
		updateSize();
	}

	private void updateSize() {
		if (layout == null || layout.getChildCount() != 1) {
			return;
		}
		View view = layout.getChildAt(0);
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
		layoutParams.width = (int)dipToPix(mapProgress(widthBar.getProgress()));
		layoutParams.height = (int)dipToPix(mapProgress(heightBar.getProgress()));
		layoutParams.gravity = Gravity.CENTER;
		// padding is automatically added by AppWidgetHostView, so let's increase the size with the padding
		Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(getApplicationContext(), getComponentName(), null);
		layoutParams.width += padding.left + padding.right;
		layoutParams.height += padding.top + padding.bottom;
		view.setLayoutParams(layoutParams);
	}

	private float dipToPix(int value) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
	}

	public static void screenshot(View view) {
		Context context = view.getContext();
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
		try {
			File storageDir = context.getCacheDir();
			if (!storageDir.mkdirs() && !storageDir.isDirectory()) {
				throw new IOException("Cannot create directory: " + storageDir
						+ " (exists: " + storageDir.exists() + ", dir: " + storageDir.isDirectory() + ")");
			}
			File file = File.createTempFile(timeStamp, ".png", storageDir);
			OutputStream stream = new FileOutputStream(file);
			//noinspection TryFinallyCanBeTryWithResources
			try {
				bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
			} finally {
				stream.close();
			}

			Uri contentUri = FileProvider.getUriForFile(context, context.getString(R.string.app_package), file);

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(contentUri, "image/png");
			intent.putExtra(Intent.EXTRA_STREAM, contentUri);
			context.startActivity(intent);
		} catch (IOException e) {
			Log.e("SCREENSHOT", "Cannot save screenshot of " + view, e);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				try {
					Intent intent = new Intent(ACTION_APPWIDGET_CONFIGURE);
					Class<?> SAWC = Class.forName("net.twisterrob.sun.android.SunAngleWidgetConfiguration");
					intent.setComponent(new ComponentName(getApplicationContext(), SAWC));
					intent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
					startActivity(intent);
					return true;
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Can't find configuration activity");
				}
		}
		return super.onKeyUp(keyCode, event);
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
