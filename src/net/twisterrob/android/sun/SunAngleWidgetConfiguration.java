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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import static android.appwidget.AppWidgetManager.*;

import net.twisterrob.android.sun.content.WidgetPreferences;
import net.twisterrob.android.sun.model.*;
import net.twisterrob.android.sun.model.SunSearchResults.SunSearchParams;
import net.twisterrob.android.sun.model.SunSearchResults.ThresholdRelation;
import net.twisterrob.android.sun.views.SunThresholdDrawable;

public class SunAngleWidgetConfiguration extends Activity {
	private int appWidgetId;
	private TextView angle;
	private CompoundButton relation;
	private SeekBar threshold;
	private ImageView visualization;
	private SunThresholdDrawable newSun;
	private SunSearchResults lastResults;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appWidgetId = getIntent().getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
		setResult(RESULT_CANCELED, result());
		if (appWidgetId == INVALID_APPWIDGET_ID) {
			//finish();
		}

		super.setContentView(R.layout.sun_angle_config);
		angle = (TextView)findViewById(R.id.angle);
		relation = (CompoundButton)findViewById(R.id.thresholdRelation);
		visualization = (ImageView)findViewById(R.id.visualization);
		threshold = (SeekBar)findViewById(R.id.threshold);
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				confirm();
			}
		});

		threshold.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				angle.setText(toThreshold(progress) + "°");
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
		threshold.setProgress(toProgress(0));
	}

	private void updateImage() {
		if (newSun == null) {
			resetImage();
		}
		ThresholdRelation rel = toRelation(relation.isChecked());
		float angle = toThreshold(threshold.getProgress());
		newSun.setSelected(rel, angle);
		newSun.setMinimumEdge(angle <= lastResults.minimum.angle);
		newSun.setMaximumEdge(angle >= lastResults.maximum.angle);
	}
	protected void resetImage() {
		newSun = new SunThresholdDrawable();
		newSun.setRadius(256);
		newSun.setSelectedVisuals(16, 20, Color.argb(0x66, 0x00, 0xFF, 0x00));
		newSun.setMinimumVisuals(6, 10, Color.argb(0xAA, 0x00, 0x88, 0xFF));
		newSun.setMaximumVisuals(6, 10, Color.argb(0xAA, 0xFF, 0x44, 0x22));
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
		edit.putString(SunAngleWidgetProvider.PREF_THRESHOLD_RELATION, toRelation(relation.isChecked()).name());
		edit.putFloat(SunAngleWidgetProvider.PREF_THRESHOLD_ANGLE, toThreshold(threshold.getProgress()));
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
}