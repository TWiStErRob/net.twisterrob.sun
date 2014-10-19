package net.twisterrob.sun.android;

import java.util.Calendar;

import android.content.*;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import static android.appwidget.AppWidgetManager.*;

import net.twisterrob.android.app.WidgetConfigurationActivity;
import net.twisterrob.android.content.res.ResourceArray;
import net.twisterrob.sun.algo.*;
import net.twisterrob.sun.algo.SunSearchResults.*;
import net.twisterrob.sun.android.view.SunThresholdDrawable;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

public class SunAngleWidgetConfiguration extends WidgetConfigurationActivity {
	private static final int MAXIMUM_COLOR = Color.argb(0xAA, 0xFF, 0x44, 0x22);
	private static final int MINIMUM_COLOR = Color.argb(0xAA, 0x00, 0x88, 0xFF);
	private CompoundButton relation;
	private TextView threshold;
	private SeekBar angle;
	private Spinner preset;
	private SunThresholdDrawable sun;
	private SunSearchResults lastResults;
	private ResourceArray mapping = new ResourceArray(ResourceArray.Type.Int, R.array.angle_preset_values);

	@Override protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			if (!getIntent().hasExtra(EXTRA_APPWIDGET_ID)) {
				getIntent().putExtra(EXTRA_APPWIDGET_ID, 183);
			}
		}
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_config);
		threshold = (TextView)findViewById(R.id.threshold);
		mapping.initialize(getResources());

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
				if (position != mapping.last()) {
					int presetAngleValue = mapping.getValue(position);
					Log.d("Config", "Preset for pos: " + position + " = " + presetAngleValue);
					angle.setProgress(toProgress(presetAngleValue));
				}
			}

			@Override public void onNothingSelected(AdapterView<?> list) {
				// ignore
			}
		});
	}

	@Override protected void onStart() {
		updateLocation();
		super.onStart();
	}
	@Override protected void onPreferencesLoad(SharedPreferences prefs) {
		String relVal = prefs.getString(SunAngleWidgetProvider.PREF_THRESHOLD_RELATION, ThresholdRelation.ABOVE.name());
		float angleVal = prefs.getFloat(SunAngleWidgetProvider.PREF_THRESHOLD_ANGLE, 0);
		Log.d("Config", "Existing values: " + relVal + " " + angleVal);
		relation.setChecked(toChecked(ThresholdRelation.valueOf(relVal)));
		angle.setProgress(toProgress(angleVal));
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
		threshold.setText(getString(R.string.message_selected_angle, getRelString(rel), angle));
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
	private void updateLocation() {
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		String provider = lm.getBestProvider(new Criteria(), true);
		Location location = lm.getLastKnownLocation(provider);
		if (location != null) {
			update(location);
		} else {
			update(null);
			lm.requestSingleUpdate(provider, new LocationListener() {
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
			}, null);
		}
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
		edit.putString(SunAngleWidgetProvider.PREF_THRESHOLD_RELATION, getCurrentRelation().name());
		edit.putFloat(SunAngleWidgetProvider.PREF_THRESHOLD_ANGLE, getCurrentThresholdAngle());
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
		int temp = Math.round(angle);
		int position = mapping.getPosition(temp);
		if (position == -1) {
			position = mapping.last();
		}
		preset.setSelection(position);
	}
}