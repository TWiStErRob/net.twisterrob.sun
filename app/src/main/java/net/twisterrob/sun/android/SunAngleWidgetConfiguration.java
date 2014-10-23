package net.twisterrob.sun.android;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import static android.appwidget.AppWidgetManager.*;

import net.twisterrob.android.app.WidgetConfigurationActivity;
import net.twisterrob.sun.algo.*;
import net.twisterrob.sun.algo.SunSearchResults.*;
import net.twisterrob.sun.android.view.SunThresholdDrawable;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.*;

public class SunAngleWidgetConfiguration extends WidgetConfigurationActivity {
	private static final int MAXIMUM_COLOR = Color.argb(0xAA, 0xFF, 0x44, 0x22);
	private static final int MINIMUM_COLOR = Color.argb(0xAA, 0x00, 0x88, 0xFF);
	private CompoundButton relation;
	private TextView threshold;
	private SeekBar angle;
	private Spinner preset;
	private SunThresholdDrawable sun;
	private SunSearchResults lastResults;
	private int[] mapping;
	private MenuItem menuShowPartOfDay;
	private MenuItem menuShowLastUpdateTime;
	private LocationUpdater locationUpdater;

	@Override protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			if (!getIntent().hasExtra(EXTRA_APPWIDGET_ID)) {
				getIntent().putExtra(EXTRA_APPWIDGET_ID, 183);
			}
		}
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_config);
		threshold = (TextView)findViewById(R.id.threshold);
		mapping = getResources().getIntArray(R.array.angle_preset_values);

		sun = createSun();
		((ImageView)findViewById(R.id.visualization)).setImageDrawable(sun);

		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finishCommit();
			}
		});

		angle = (SeekBar)findViewById(R.id.angle);
		angle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setPresetByAngle(toThreshold(progress));
				updateImage(lastResults);
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				// ignore
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
				// ignore
			}
		});

		relation = (CompoundButton)findViewById(R.id.thresholdRelation);
		relation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateImage(lastResults);
			}
		});

		preset = ((Spinner)findViewById(R.id.preset));
		preset.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> list, View view, int position, long id) {
				if (position != mapping.length - 1) {
					int presetAngleValue = mapping[position];
					Log.d("Config", "Preset for pos: " + position + " = " + presetAngleValue);
					angle.setProgress(toProgress(presetAngleValue));
				}
			}

			@Override public void onNothingSelected(AdapterView<?> list) {
				// ignore
			}
		});

		locationUpdater = new LocationUpdater();
	}

	@Override protected void onStart() {
		super.onStart();
		locationUpdater.single();
	}

	@Override protected void onStop() {
		locationUpdater.cancel();
		super.onStop();
	}
	@Override protected SharedPreferences onPreferencesOpen(int appWidgetId) {
		return SunAngleWidgetProvider.getPreferences(this, appWidgetId);
	}

	@Override protected void onPreferencesLoad(SharedPreferences prefs) {
		String rel = prefs.getString(PREF_THRESHOLD_RELATION, DEFAULT_THRESHOLD_RELATION.name());
		relation.setChecked(toChecked(ThresholdRelation.valueOf(rel)));
		angle.setProgress(toProgress(prefs.getFloat(PREF_THRESHOLD_ANGLE, DEFAULT_THRESHOLD_ANGLE)));
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.config, menu);
		SharedPreferences prefs = getWidgetPreferences();

		menuShowPartOfDay = menu.findItem(R.id.action_show_partOfDay);
		updateCheckableOption(menuShowPartOfDay,
				prefs.getBoolean(PREF_SHOW_PART_OF_DAY, DEFAULT_SHOW_PART_OF_DAY));

		menuShowLastUpdateTime = menu.findItem(R.id.action_show_lastUpdateTime);
		updateCheckableOption(menuShowLastUpdateTime,
				prefs.getBoolean(PREF_SHOW_UPDATE_TIME, DEFAULT_SHOW_UPDATE_TIME));

		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_show_lastUpdateTime:
			case R.id.action_show_partOfDay:
				updateCheckableOption(item, !item.isChecked());
				return true;
			case R.id.action_help:
				new AlertDialog.Builder(this)
						.setTitle(getTitle())
						.setMessage(getText(R.string.config_help))
						.setPositiveButton(android.R.string.ok, null)
						.create()
						.show()
				;
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateCheckableOption(MenuItem item, boolean checkedState) {
		item.setChecked(checkedState);
		item.setIcon(checkedState
						? android.R.drawable.checkbox_on_background
						: android.R.drawable.checkbox_off_background
		);
	}

	private void updateImage(SunSearchResults results) {
		ThresholdRelation rel = getCurrentRelation();
		float angle = getCurrentThresholdAngle();
		sun.setSelected(rel, angle);
		boolean belowMin = angle <= results.minimum.angle;
		boolean aboveMax = angle >= results.maximum.angle;
		sun.setMinimumEdge(rel == ThresholdRelation.ABOVE && !belowMin);
		sun.setMaximumEdge(rel == ThresholdRelation.BELOW && !aboveMax);
		sun.setMinMax((float)results.minimum.angle, (float)results.maximum.angle);
		threshold.setTextColor(Color.BLACK);
		threshold.setText(getString(R.string.message_selected_angle, getRelString(rel), angle, results.current.angle));
		if (belowMin) {
			threshold.setTextColor(MINIMUM_COLOR);
			threshold.setText(getString(R.string.warning_minimum, results.minimum.angle, angle));
		}
		if (aboveMax) {
			threshold.setTextColor(MAXIMUM_COLOR);
			threshold.setText(getString(R.string.warning_maximum, results.maximum.angle, angle));
		}
	}

	private CharSequence getRelString(ThresholdRelation rel) {
		int id = rel == ThresholdRelation.ABOVE? R.string.threshold_relation_above : R.string.threshold_relation_below;
		return getString(id);
	}

	protected SunThresholdDrawable createSun() {
		sun = new SunThresholdDrawable();
		sun.setRadius(256);
		sun.setSelectedVisuals(16, 20, Color.argb(0x66, 0x00, 0xFF, 0x00));
		sun.setMinimumVisuals(6, 10, MINIMUM_COLOR);
		sun.setMaximumVisuals(6, 10, MAXIMUM_COLOR);
		sun.setSelectedEdge(true);
		return sun;
	}

	protected void update(Location loc) {
		SunSearchResults results = null;
		if (loc != null) {
			SunSearchParams params = new SunSearchParams(loc.getLatitude(), loc.getLongitude(), Calendar.getInstance());
			results = new SunCalculator(new PhotovoltaicSun()).find(params);
		}
		if (results == null) {
			results = SunSearchResults.unknown();
		}
		updateImage(results);
		this.lastResults = results;
	}

	@Override protected void onPreferencesSave(SharedPreferences.Editor edit) {
		edit.putString(PREF_THRESHOLD_RELATION, getCurrentRelation().name());
		edit.putFloat(PREF_THRESHOLD_ANGLE, getCurrentThresholdAngle());
		edit.putBoolean(PREF_SHOW_UPDATE_TIME, menuShowLastUpdateTime.isChecked());
		edit.putBoolean(PREF_SHOW_PART_OF_DAY, menuShowPartOfDay.isChecked());
	}

	private static ThresholdRelation toRelation(boolean checked) {
		return checked? ThresholdRelation.ABOVE : ThresholdRelation.BELOW;
	}
	private static boolean toChecked(ThresholdRelation rel) {
		return rel == ThresholdRelation.ABOVE;
	}

	private static float toThreshold(int progress) {
		return progress - 90;
	}
	private static int toProgress(float threshold) {
		return (int)(threshold + 90);
	}

	protected ThresholdRelation getCurrentRelation() {
		return toRelation(relation.isChecked());
	}

	protected float getCurrentThresholdAngle() {
		return toThreshold(angle.getProgress());
	}

	protected void setPresetByAngle(float angle) {
		Log.d("Config", "Syncing preset for angle: " + angle);
		int position = find(mapping, Math.round(angle));
		if (position == -1) {
			position = mapping.length - 1;
		}
		preset.setSelection(position);
	}

	private static int find(int[] array, int value) {
		for (int i = 0; i < array.length; ++i) {
			if (array[i] == value) {
				return i;
			}
		}
		return -1;
	}

	private final class LocationUpdater implements LocationListener {
		private final LocationManager lm;
		private String provider;

		public LocationUpdater() {
			lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			findBestProvider();
		}

		public void findBestProvider() {
			provider = lm.getBestProvider(new Criteria(), true);
		}

		public void single() {
			cancel();
			Location location = lm.getLastKnownLocation(provider);
			if (location != null) {
				update(location);
			} else {
				update(null);
				lm.requestSingleUpdate(provider, this, getMainLooper());
			}
		}

		public void cancel() {
			lm.removeUpdates(this);
		}

		public void update(Location location) {
			SunAngleWidgetConfiguration.this.update(location);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// ignore
		}

		public void onProviderEnabled(String provider) {
			// ignore
		}

		public void onProviderDisabled(String provider) {
			// ignore
		}

		public void onLocationChanged(Location location) {
			update(location);
		}
	}
}
