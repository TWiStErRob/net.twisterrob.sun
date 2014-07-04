package net.twisterrob.android.sun;

import java.util.Calendar;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.location.*;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import static android.appwidget.AppWidgetManager.*;

import net.twisterrob.android.sun.content.WidgetPreferences;
import net.twisterrob.android.sun.model.*;
import net.twisterrob.android.sun.model.SunSearchResults.SunSearchParams;
import net.twisterrob.android.sun.model.SunSearchResults.ThresholdRelation;
import net.twisterrob.android.sun.views.SunThresholdDrawable;

public class SunAngleWidgetConfiguration extends Activity {
	private static final int MAXIMUM_COLOR = Color.argb(0xAA, 0xFF, 0x44, 0x22);
	private static final int MINIMUM_COLOR = Color.argb(0xAA, 0x00, 0x88, 0xFF);
	private int appWidgetId;
	private CompoundButton relation;
	private TextView threshold;
	private SeekBar angle;
	private ImageView visualization;
	private Spinner preset;
	private SunThresholdDrawable newSun;
	private SunSearchResults lastResults;
	private ResourceArray mapping = new ResourceArray(ResourceArray.Type.Int, R.array.angle_preset_values);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appWidgetId = getIntent().getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
		setResult(RESULT_CANCELED, result());
		if (appWidgetId == INVALID_APPWIDGET_ID) {
			//finish();
		}

		super.setContentView(R.layout.sun_angle_config);
		relation = (CompoundButton)findViewById(R.id.thresholdRelation);
		threshold = (TextView)findViewById(R.id.threshold);
		visualization = (ImageView)findViewById(R.id.visualization);
		angle = (SeekBar)findViewById(R.id.angle);
		preset = ((Spinner)findViewById(R.id.preset));
		mapping.initialize(getResources());

		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				confirm();
			}
		});

		angle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setPresetByAngle(toThreshold(progress));
				updateImage();
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				// ignore
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
				// ignore
			}
		});

		relation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateImage();
			}
		});

		preset.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> list, View view, int position, long id) {
				int presetAngleValue = mapping.getValue(position);
				angle.setProgress(toProgress(presetAngleValue));
			}

			@Override
			public void onNothingSelected(AdapterView<?> list) {
				// NOP
			}
		});

		preset.setSelection(0);
	}

	private void updateImage() {
		if (newSun == null) {
			resetImage();
		}
		ThresholdRelation rel = getCurrentRelation();
		float angle = getCurrentThresholdAngle();
		newSun.setSelected(rel, angle);
		boolean belowMin = angle <= lastResults.minimum.angle;
		boolean aboveMax = angle >= lastResults.maximum.angle;
		newSun.setMinimumEdge(belowMin);
		newSun.setMaximumEdge(aboveMax);
		threshold.setTextColor(Color.BLACK);
		threshold.setText(getString(R.string.message_selected_angle, getRelString(rel), angle));
		if (belowMin) {
			threshold.setTextColor(MINIMUM_COLOR);
			threshold.setText(getString(R.string.warning_minimum, lastResults.minimum.angle, angle));
		}
		if (aboveMax) {
			threshold.setTextColor(MAXIMUM_COLOR);
			threshold.setText(getString(R.string.warning_maximum, lastResults.maximum.angle, angle));
		}
	}

	private CharSequence getRelString(ThresholdRelation rel) {
		int id = rel == ThresholdRelation.ABOVE? R.string.threshold_relation_above : R.string.threshold_relation_below;
		return getString(id);
	}

	protected void resetImage() {
		newSun = new SunThresholdDrawable();
		newSun.setRadius(256);
		newSun.setSelectedVisuals(16, 20, Color.argb(0x66, 0x00, 0xFF, 0x00));
		newSun.setMinimumVisuals(6, 10, MINIMUM_COLOR);
		newSun.setMaximumVisuals(6, 10, MAXIMUM_COLOR);
		newSun.setSelectedEdge(true);
		visualization.setImageDrawable(newSun);
		updateLocation();
	}
	private void updateLocation() {
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		String provider = lm.getBestProvider(new Criteria(), true);
		Location location = lm.getLastKnownLocation(provider);
		if (location != null) {
			update(location);
		} else {
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
		SunSearchParams params = new SunSearchParams(loc.getLatitude(), loc.getLongitude(), Calendar.getInstance());
		lastResults = new SunCalculator(new SunX()).find(params);
		newSun.setMinMax((float)lastResults.minimum.angle, (float)lastResults.maximum.angle);
		updateImage();
	}

	private void confirm() {
		SharedPreferences prefs = new WidgetPreferences(this, SunAngleWidgetProvider.PREF_NAME, appWidgetId);
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(SunAngleWidgetProvider.PREF_THRESHOLD_RELATION, getCurrentRelation().name());
		edit.putFloat(SunAngleWidgetProvider.PREF_THRESHOLD_ANGLE, getCurrentThresholdAngle());
		edit.commit();
		setResult(RESULT_OK, result());
		finish();
	}

	private Intent result() {
		Intent result = new Intent();
		result.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);
		return result;
	}

	private static ThresholdRelation toRelation(boolean checked) {
		return checked? ThresholdRelation.ABOVE : ThresholdRelation.BELOW;
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
		int temp = Math.round(angle);
		int position = mapping.getPosition(temp);
		if (position == -1) {
			position = mapping.last();
		}
		preset.setSelection(position);
	}
}