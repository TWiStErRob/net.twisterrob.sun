package net.twisterrob.sun.android;

import java.util.*;

import android.annotation.TargetApi;
import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.*;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.*;
import android.text.style.*;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

import static android.appwidget.AppWidgetManager.*;
import static android.view.ViewGroup.LayoutParams.*;

import net.twisterrob.android.app.WidgetConfigurationActivity;
import net.twisterrob.sun.algo.*;
import net.twisterrob.sun.algo.SunSearchResults.*;
import net.twisterrob.sun.android.logic.SunAngleWidgetUpdater;
import net.twisterrob.sun.android.view.SunThresholdDrawable;
import net.twisterrob.sun.configuration.R;
import net.twisterrob.sun.configuration.BuildConfig;
import net.twisterrob.sun.pveducation.PhotovoltaicSun;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.*;

public class SunAngleWidgetConfiguration extends WidgetConfigurationActivity {
	private static final int MAXIMUM_COLOR = Color.argb(0xAA, 0xFF, 0x44, 0x22);
	private static final int MINIMUM_COLOR = Color.argb(0xAA, 0x00, 0x88, 0xFF);
	private CompoundButton relation;
	private TextView message;
	private SeekBar angle;
	private Spinner preset;
	private SunThresholdDrawable sun;
	private SunSearchResults lastResults = SunSearchResults.unknown();
	private int[] mapping;
	private Menu menu;
	private LocationUpdater locationUpdater;

	@Override protected void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			if (!getIntent().hasExtra(EXTRA_APPWIDGET_ID)) {
				getIntent().putExtra(EXTRA_APPWIDGET_ID, 183);
			}
		}
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_config);
		message = (TextView)findViewById(R.id.message);
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
				updateUI(lastResults);
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
				updateUI(lastResults);
			}
		});

		preset = ((Spinner)findViewById(R.id.preset));
		preset.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> list, View view, int position, long id) {
				if (position != mapping.length - 1) {
					int presetAngleValue = mapping[position];
					if (Log.isLoggable("Config", Log.DEBUG)) {
						Log.d("Config", "Preset for pos: " + position + " = " + presetAngleValue);
					}
					angle.setProgress(toProgress(presetAngleValue));
				}
			}

			@Override public void onNothingSelected(AdapterView<?> list) {
				// ignore
			}
		});

		locationUpdater = new LocationUpdater();
	}

	@Override protected void onResume() {
		super.onResume();
		locationUpdater.single();
	}

	@Override protected void onDestroy() {
		locationUpdater.cancel();
		super.onDestroy();
	}
	@Override protected SharedPreferences onPreferencesOpen(int appWidgetId) {
		return SunAngleWidgetProvider.getPreferences(getApplicationContext(), appWidgetId);
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

		updateCheckableOption(menu.findItem(R.id.action_show_partOfDay),
				prefs.getBoolean(PREF_SHOW_PART_OF_DAY, DEFAULT_SHOW_PART_OF_DAY));
		updateCheckableOption(menu.findItem(R.id.action_show_lastUpdateTime),
				prefs.getBoolean(PREF_SHOW_UPDATE_TIME, DEFAULT_SHOW_UPDATE_TIME));

		this.menu = menu;
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_show_lastUpdateTime || id == R.id.action_show_partOfDay) {
				updateCheckableOption(item, !item.isChecked());
				return true;
		} else if (id == R.id.action_help) {
				new AlertDialog.Builder(this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle(getTitle())
						.setMessage(getHelpText(R.string.config_help))
						.setPositiveButton(android.R.string.ok, null)
						.create()
						.show()
				;
				return true;
		} else if (id == R.id.action_mock_date) {
				final Calendar time = currentMockDateTime();
				DatePickerDialog dialog = new DatePickerDialog(this, new OnDateSetListener() {
					@Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						time.set(Calendar.YEAR, year);
						time.set(Calendar.MONTH, monthOfYear);
						time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						getWidgetPreferences().edit().putLong(PREF_MOCK_TIME, time.getTimeInMillis()).apply();
					}
				}, time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH));
				// dialog.setOnCancelListener(): the callback is not called on S4 4.4.2
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(android.R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								getWidgetPreferences().edit().remove(PREF_MOCK_TIME).apply();
							}
						});
				dialog.show();
				return true;
		} else if (id == R.id.action_mock_time) {
				final Calendar time = currentMockDateTime();
				TimePickerDialog dialog = new TimePickerDialog(this, new OnTimeSetListener() {
					@Override public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						time.set(Calendar.HOUR_OF_DAY, hourOfDay);
						time.set(Calendar.MINUTE, minute);
						getWidgetPreferences().edit().putLong(PREF_MOCK_TIME, time.getTimeInMillis()).apply();
					}
				}, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(android.R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								getWidgetPreferences().edit().remove(PREF_MOCK_TIME).apply();
							}
						});
				dialog.show();
				return true;
		} else if (id == R.id.action_mock_angle) {
				final NumberPicker picker = createAnglePicker(getWidgetPreferences().getFloat(PREF_MOCK_ANGLE, 0));
				new AlertDialog.Builder(this)
						.setTitle("Edit Angle")
						//noinspection NewApi, this case won't work in <11, but it's debug only.
						.setView(picker)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@TargetApi(VERSION_CODES.HONEYCOMB)
							public void onClick(DialogInterface dialog, int id) {
								float value = Float.parseFloat(picker.getDisplayedValues()[picker.getValue()]);
								getWidgetPreferences().edit().putFloat(PREF_MOCK_ANGLE, value).apply();
							}
						})
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which) {
								getWidgetPreferences().edit().remove(PREF_MOCK_ANGLE).apply();
							}
						})
						.create()
						.show();
				return true;
		} else if (id == R.id.action_mock_fill) {
				int[] appWidgetIds = SunAngleWidgetProvider.getAppWidgetIds(this);
				float[] presets = {90, -90, 0, -3, -9, -15, -6, -12, -18, 180};
				for (int i = 0; i < appWidgetIds.length && i < presets.length; ++i) {
					int appWidgetId = appWidgetIds[i];
					onPreferencesOpen(appWidgetId)
							.edit()
							.putFloat(PREF_MOCK_ANGLE, presets[i])
							.remove(PREF_MOCK_TIME)
							.apply();
				}
				return true;
		} else if (id == R.id.action_mock_clearAll) {
				int[] appWidgetIds = SunAngleWidgetProvider.getAppWidgetIds(this);
				float[] presets = {90, -90, 0, -3, -9, -15, -6, -12, -18, 180};
				for (int i = 0; i < appWidgetIds.length && i < presets.length; ++i) {
					int appWidgetId = appWidgetIds[i];
					onPreferencesOpen(appWidgetId)
							.edit()
							.remove(PREF_MOCK_ANGLE)
							.remove(PREF_MOCK_TIME)
							.apply();
				}
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Get a string/text resource and process annotations.
	 *
	 * inspiration from android.text.Html#startImage and end of StringBlock#applyStyles
	 * possibilities are limitless, for example: getKey() is "string" and text.replace(...)
	 */
	private CharSequence getHelpText(@StringRes int annotatedTextID) {
		Resources res = getResources();

		SpannableStringBuilder text = new SpannableStringBuilder(res.getText(annotatedTextID));
		Annotation[] annotations = text.getSpans(0, text.length(), Annotation.class);
		for (Annotation annot : annotations) {
			final String key = annot.getKey();
			final String value = annot.getValue();
			Object span = null;
			if ("drawable".equals(key)) { // <annotation drawable="drawable_name">&#xFFFC;</annotation>
				int drawableID = res.getIdentifier(value, "drawable", getPackageName());
				span = new ImageSpan(this, drawableID);
			} else if ("color".equals(key)) {
				int colorID = res.getIdentifier(value, "color", getPackageName());
				span = new ForegroundColorSpan(ContextCompat.getColor(this, colorID));
			} else if ("bgcolor".equals(key)) {
				int colorID = res.getIdentifier(value, "color", getPackageName());
				span = new BackgroundColorSpan(ContextCompat.getColor(this, colorID));
			}
			if (span != null) {
				text.setSpan(span, text.getSpanStart(annot), text.getSpanEnd(annot), text.getSpanFlags(annot));
			}
		}
		return text;
	}

	@TargetApi(VERSION_CODES.HONEYCOMB)
	private NumberPicker createAnglePicker(float value) {
		final NumberPicker picker = new NumberPicker(this);
		picker.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
		float step = 5;
		int min = (int)(-90 - step);
		int max = (int)(90 + step);
		String[] values = new String[(int)((max - min) / step) + 1];
		for (int i = 0; i < values.length; i++) {
			values[i] = String.valueOf(i * step + min);
		}
		picker.setMinValue(0);
		picker.setMaxValue(values.length - 1);
		picker.setDisplayedValues(values);
		picker.setValue((int)((value - min) / step));
		return picker;
	}

	private Calendar currentMockDateTime() {
		long initialTime = getWidgetPreferences().getLong(PREF_MOCK_TIME, DEFAULT_MOCK_TIME);
		final Calendar time = Calendar.getInstance();
		time.setTimeInMillis(initialTime);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		return time;
	}

	private void updateCheckableOption(MenuItem item, boolean checkedState) {
		item.setChecked(checkedState);
		item.setIcon(checkedState
				? android.R.drawable.checkbox_on_background
				: android.R.drawable.checkbox_off_background
		);
	}

	void updateUI(SunSearchResults results) {
		ThresholdRelation rel = getCurrentRelation();
		float angle = getCurrentThresholdAngle();
		sun.setSelected(rel, angle);
		boolean belowMin = angle <= results.minimum.angle;
		boolean aboveMax = angle >= results.maximum.angle;
		sun.setMinimumEdge(rel == ThresholdRelation.ABOVE && !belowMin);
		sun.setMaximumEdge(rel == ThresholdRelation.BELOW && !aboveMax);
		sun.setMinMax((float)results.minimum.angle, (float)results.maximum.angle);
		message.setTextColor(foregroundColor(this));
		message.setOnClickListener(null);
		message.setText(getString(R.string.message_selected_angle, getRelString(rel), angle));
		if (belowMin) {
			message.setTextColor(MINIMUM_COLOR);
			message.setText(getString(R.string.warning_minimum, results.minimum.angle, angle));
		}
		if (aboveMax) {
			message.setTextColor(MAXIMUM_COLOR);
			message.setText(getString(R.string.warning_maximum, results.maximum.angle, angle));
		}
		if (!results.params.hasLocation()) {
			message.setTextColor(ContextCompat.getColor(this, R.color.invalid));
			message.setText(R.string.warning_no_location);
			message.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					openLocationSettings();
				}
			});
		}
	}

	private static @ColorInt int foregroundColor(@NonNull Context context) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(android.R.attr.colorForeground, typedValue, true);
		return typedValue.data;
	}

	private void openLocationSettings() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.warning_no_location_settings, Toast.LENGTH_LONG).show();
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
		updateUI(results);
		this.lastResults = results;
	}

	@Override protected void onPreferencesSave(SharedPreferences.Editor edit) {
		edit.putString(PREF_THRESHOLD_RELATION, getCurrentRelation().name());
		edit.putFloat(PREF_THRESHOLD_ANGLE, getCurrentThresholdAngle());
		edit.putBoolean(PREF_SHOW_UPDATE_TIME, menu.findItem(R.id.action_show_lastUpdateTime).isChecked());
		edit.putBoolean(PREF_SHOW_PART_OF_DAY, menu.findItem(R.id.action_show_partOfDay).isChecked());
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
		if (Log.isLoggable("Config", Log.DEBUG)) {
			Log.d("Config", "Syncing preset for angle: " + angle);
		}
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
		private final SunAngleWidgetUpdater updater = new SunAngleWidgetUpdater(SunAngleWidgetConfiguration.this);

		public void single() {
			Location location = updater.getLocation(this);
			update(location);
		}

		public void cancel() {
			updater.clearLocation(this);
		}

		@SuppressWarnings("deprecation")
		public void onStatusChanged(String provider, int status, Bundle extras) { /* NOP */}
		public void onProviderDisabled(String provider) { /* NOP */}
		public void onProviderEnabled(String provider) { /* NOP */}
		public void onLocationChanged(Location location) {
			if (Log.isLoggable("Sun", Log.VERBOSE)) {
				Log.v("Sun", this + ".onLocationChanged(" + location + ")");
			}
			cancel();
			update(location);
		}

		@Override
		public String toString() {
			return String.format(Locale.ROOT, "LocationUpdater(%08x)[%d]", this.hashCode(), getAppWidgetId());
		}
	}
}
