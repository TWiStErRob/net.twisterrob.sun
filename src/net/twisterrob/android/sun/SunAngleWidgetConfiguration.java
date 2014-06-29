package net.twisterrob.android.sun;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import static android.appwidget.AppWidgetManager.*;

import net.twisterrob.android.sun.content.WidgetPreferences;
import net.twisterrob.android.sun.model.ThresholdRelation;
import net.twisterrob.android.sun.ui.SunGradientShaderFactory;
import net.twisterrob.android.sun.views.RingSectionDrawable;

public class SunAngleWidgetConfiguration extends Activity {
	private int appWidgetId;
	private TextView angle;
	private CompoundButton relation;
	private SeekBar threshold;
	private ImageView visualization;

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

	private RingSectionDrawable x;
	private void updateImage() {
		if (x == null) {
			resetImage();
		}
		updateRing(x, toThreshold(threshold.getProgress()), toRelation(relation.isChecked()));
	}

	/**
	 * @param angle -90 .. 90
	 */
	protected void updateRing(RingSectionDrawable ring, float angle, ThresholdRelation relation) {
		float sweep = 2 * (90 - angle);
		if (relation == ThresholdRelation.BELOW) {
			angle = 180 - angle;
			sweep = 360 - sweep;
		}
		ring.setSection(angle, sweep);
	}

	protected void resetImage() {
		final int radius = 256;
		PaintDrawable sun = new PaintDrawable();
		sun.setShape(new OvalShape());
		sun.setShaderFactory(new SunGradientShaderFactory());
		sun.setIntrinsicWidth(radius * 2);
		sun.setIntrinsicHeight(radius * 2);

		x = new RingSectionDrawable(radius - 40, 20);
		x.setSize(radius * 2, radius * 2);
		x.setColor(Color.argb(0x66, 0x00, 0xFF, 0x00));
		LayerDrawable image = new LayerDrawable(new Drawable[]{sun, x});
		visualization.setImageDrawable(image);
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