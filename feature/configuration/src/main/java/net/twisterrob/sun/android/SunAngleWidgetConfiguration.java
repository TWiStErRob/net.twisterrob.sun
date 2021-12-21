package net.twisterrob.sun.android;

import java.util.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.*;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.*;
import android.text.style.*;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.appwidget.AppWidgetManager.*;
import static android.view.ViewGroup.LayoutParams.*;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationListenerCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.util.Consumer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import net.twisterrob.android.app.WidgetConfigurationActivity;
import net.twisterrob.sun.algo.*;
import net.twisterrob.sun.algo.SunSearchResults.*;
import net.twisterrob.sun.android.logic.SunAngleWidgetUpdater;
import net.twisterrob.sun.android.view.SunThresholdDrawable;
import net.twisterrob.sun.configuration.BuildConfig;
import net.twisterrob.sun.configuration.R;
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
		message = findViewById(R.id.message);
		mapping = getResources().getIntArray(R.array.angle_preset_values);

		sun = createSun();
		((ImageView)findViewById(R.id.visualization)).setImageDrawable(sun);

		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finishCommit();
			}
		});

		angle = findViewById(R.id.angle);
		angle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setPresetByAngle(toThreshold(progress));
				updateUI(lastResults);
			}

			@Override public void onStartTrackingTouch(SeekBar seekBar) {
				// ignore
			}

			@Override public void onStopTrackingTouch(SeekBar seekBar) {
				// ignore
			}
		});

		relation = findViewById(R.id.thresholdRelation);
		relation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateUI(lastResults);
			}
		});

		preset = findViewById(R.id.preset);
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

		locationUpdater = new LocationUpdater(getApplicationContext(), getAppWidgetId(), new Consumer<Location>() {
			@Override public void accept(Location location) {
				update(location);
			}
		});
		updateOrRequestPermissions();
	}

	@Override protected void onResume() {
		super.onResume();
		locationUpdater.single();
	}

	@Override protected void onDestroy() {
		locationUpdater.cancel();
		super.onDestroy();
	}

	private static final int REQUEST_CODE_LOCATION = 12312;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
			updateOrRequestPermissions();
		}
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String[] permissions,
			@NonNull int[] grantResults
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	@AfterPermissionGranted(REQUEST_CODE_LOCATION)
	private void updateOrRequestPermissions() {
		if (EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)) {
			locationUpdater.single();
		} else {
			EasyPermissions.requestPermissions(
					new PermissionRequest
							.Builder(
									this,
									REQUEST_CODE_LOCATION,
									ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
							)
							.setRationale(R.string.warning_no_location_rationale)
							.setPositiveButtonText(android.R.string.ok)
							.setNegativeButtonText(android.R.string.cancel)
							.build()
			);
		}
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
						.setView(picker)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
	private @NonNull CharSequence getHelpText(@StringRes int annotatedTextID) {
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

	private @NonNull NumberPicker createAnglePicker(float value) {
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

	private @NonNull Calendar currentMockDateTime() {
		long initialTime = getWidgetPreferences().getLong(PREF_MOCK_TIME, DEFAULT_MOCK_TIME);
		final Calendar time = Calendar.getInstance();
		time.setTimeInMillis(initialTime);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		return time;
	}

	private static void updateCheckableOption(@NonNull MenuItem item, boolean checkedState) {
		item.setChecked(checkedState);
		item.setIcon(checkedState
				? android.R.drawable.checkbox_on_background
				: android.R.drawable.checkbox_off_background
		);
	}

	void updateUI(@NonNull SunSearchResults results) {
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
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if (!LocationManagerCompat.isLocationEnabled(locationManager) ) {
				message.setTextColor(ContextCompat.getColor(this, R.color.invalid));
				message.setText(R.string.warning_no_location);
				message.setOnClickListener(new OnClickListener() {
					@Override public void onClick(View v) {
						openLocationSettings();
					}
				});
			} else if (!EasyPermissions.hasPermissions(this, ACCESS_FINE_LOCATION)) {
				if (EasyPermissions.permissionPermanentlyDenied(this, ACCESS_FINE_LOCATION)) {
					message.setTextColor(ContextCompat.getColor(this, R.color.invalid));
					message.setText(R.string.warning_no_location_permission_settings);
					message.setOnClickListener(new OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override public void onClick(View v) {
							Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
									.setData(Uri.fromParts("package", getPackageName(), null))
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// deprecation:Need to clean up code before I can change to registerForActivityResult.
							startActivityForResult(intent, 0);
						}
					});
				} else {
					message.setTextColor(ContextCompat.getColor(this, R.color.invalid));
					message.setText(R.string.warning_no_location_permission);
					message.setOnClickListener(new OnClickListener() {
						@Override public void onClick(View v) {
							updateOrRequestPermissions();
						}
					});
				}
			} else {
				message.setTextColor(ContextCompat.getColor(this, R.color.invalid));
				message.setText(R.string.warning_no_location_clueless);
				message.setOnClickListener(null);
			}
		}
	}

	private static @ColorInt int foregroundColor(@NonNull Context context) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(android.R.attr.colorForeground, typedValue, true);
		return typedValue.data;
	}

	@SuppressLint("QueryPermissionsNeeded") // https://developer.android.com/training/package-visibility/automatic
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

	protected void update(@Nullable Location loc) {
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

	private static final class LocationUpdater implements LocationListenerCompat {

		private final @NonNull SunAngleWidgetUpdater updater;
		private final int appWidgetId;
		private final @NonNull Consumer<Location> update;

		LocationUpdater(@NonNull Context context, int appWidgetId, @NonNull Consumer<Location> update) {
			this.updater = new SunAngleWidgetUpdater(context);
			this.appWidgetId = appWidgetId;
			this.update = update;
		}

		public void single() {
			Location location = updater.getLocation(this);
			update.accept(location);
		}

		public void cancel() {
			updater.clearLocation(this);
		}

		public void onLocationChanged(@NonNull Location location) {
			if (Log.isLoggable("Sun", Log.VERBOSE)) {
				Log.v("Sun", this + ".onLocationChanged(" + location + ")");
			}
			cancel();
			update.accept(location);
		}

		@Override
		public @NonNull String toString() {
			return String.format(Locale.ROOT, "LocationUpdater(%08x)[%d]", this.hashCode(), appWidgetId);
		}
	}
}
