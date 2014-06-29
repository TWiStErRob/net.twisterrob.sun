package net.twisterrob.android.sun;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import static android.appwidget.AppWidgetManager.*;

import net.twisterrob.android.sun.content.WidgetPreferences;
import net.twisterrob.android.sun.model.ThresholdRelation;

public class SunAngleWidgetConfiguration extends Activity {
	private int appWidgetId;
	private TextView angle;
	private CompoundButton relation;
	private SeekBar threshold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appWidgetId = getIntent().getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
		setResult(RESULT_CANCELED, result());
		if (appWidgetId == INVALID_APPWIDGET_ID) {
			finish();
		}

		super.setContentView(R.layout.sun_angle_config);
		angle = (TextView)findViewById(R.id.angle);
		relation = (CompoundButton)findViewById(R.id.thresholdRelation);
		threshold = (SeekBar)findViewById(R.id.threshold);
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				confirm();
			}
		});

		threshold.setProgress(toProgress(0));
		threshold.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				angle.setText(toThreshold(progress) + "°");
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
				// ignore
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
				// ignore
			}
		});
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