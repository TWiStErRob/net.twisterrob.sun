package net.twisterrob.sun.android;

import java.util.*;

import android.annotation.SuppressLint;
import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.content.DialogInterface.*;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TimePicker.OnTimeChangedListener;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.*;

@SuppressLint("NewApi")
public class WidgetMockerActivity extends ListActivity {
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override protected void onResume() {
		super.onResume();
		setListAdapter(new WidgetAdapter(getApplicationContext()));
	}

	@Override protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int appWidgetId = (Integer)l.getAdapter().getItem(position);
		edit(appWidgetId);
	}

	private void edit(final int appWidgetId) {
		final float initialAngle = getMockedAngle(appWidgetId);
		final long initialTime = getMockedTime(appWidgetId);
		final Calendar time = Calendar.getInstance();
		time.setTimeInMillis(initialTime);

		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		final CheckBox angleSign = new CheckBox(this);
		angleSign.setText("Below horizon");
		angleSign.setChecked(initialAngle < 0);

		NumberPicker angleEditor = new NumberPicker(this);
		angleEditor.setMinValue(0);
		angleEditor.setMaxValue(99);
		angleEditor.setValue(Math.abs((int)initialAngle));
		angleEditor.setOnValueChangedListener(new OnValueChangeListener() {
			@Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				setMockedAngle(appWidgetId, newVal * (angleSign.isChecked()? -1 : +1));
			}
		});

		DatePicker dateEditor = new DatePicker(this);
		dateEditor.init(time.get(Calendar.YEAR), time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {
					@Override public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						time.set(Calendar.YEAR, year);
						time.set(Calendar.MONTH, monthOfYear);
						time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						setMockedTime(appWidgetId, time.getTimeInMillis());
					}
				}
		);
		dateEditor.setSpinnersShown(true);
		dateEditor.setCalendarViewShown(false);

		TimePicker timeEditor = new TimePicker(this);
		timeEditor.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
		timeEditor.setCurrentMinute(time.get(Calendar.MINUTE));
		timeEditor.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				time.set(Calendar.HOUR_OF_DAY, hourOfDay);
				time.set(Calendar.MINUTE, minute);
				setMockedTime(appWidgetId, time.getTimeInMillis());
			}
		});

		layout.addView(angleSign, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addView(angleEditor, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addView(timeEditor, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addView(dateEditor, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		ScrollView scroll = new ScrollView(this);
		scroll.addView(layout);

		new AlertDialog.Builder(this)
				.setTitle("Edit mocks")
				.setView(scroll)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// listeners already updated the preferences
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						setMockedAngle(appWidgetId, initialAngle);
						setMockedTime(appWidgetId, initialTime);
					}
				})
				.setNeutralButton("Reset", new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						clearMockedAngle(appWidgetId);
						clearMockedTime(appWidgetId);
					}
				})
				.setOnDismissListener(new OnDismissListener() {
					@Override public void onDismiss(DialogInterface dialog) {
						((BaseAdapter)getListAdapter()).notifyDataSetChanged();
					}
				})
				.create()
				.show();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Pre-fill");
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				preFill();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void preFill() {
		float[] presets = {90, -90, 0, -3, -9, -15, -6, -12, -18, 180};
		for (int i = 0; i < getListAdapter().getCount() && i < presets.length; ++i) {
			int appWidgetId = (Integer)getListAdapter().getItem(i);
			setMockedAngle(appWidgetId, presets[i]);
			clearMockedTime(appWidgetId);
		}
		((BaseAdapter)getListAdapter()).notifyDataSetChanged();
	}

	private float getMockedAngle(int appWidgetID) {
		return SunAngleWidgetProvider.getPreferences(this, appWidgetID).getFloat(PREF_MOCK_ANGLE, DEFAULT_MOCK_ANGLE);
	}

	private void setMockedAngle(int appWidgetId, float angle) {
		SunAngleWidgetProvider.getPreferences(this, appWidgetId).edit().putFloat(PREF_MOCK_ANGLE, angle).apply();
	}

	private void clearMockedAngle(int appWidgetId) {
		SunAngleWidgetProvider.getPreferences(this, appWidgetId).edit().remove(PREF_MOCK_ANGLE).apply();
	}

	private long getMockedTime(int appWidgetID) {
		return SunAngleWidgetProvider.getPreferences(this, appWidgetID).getLong(PREF_MOCK_TIME, DEFAULT_MOCK_TIME);
	}

	private void setMockedTime(int appWidgetId, long timeInMillis) {
		SunAngleWidgetProvider.getPreferences(this, appWidgetId).edit().putLong(PREF_MOCK_TIME, timeInMillis).apply();
	}

	private void clearMockedTime(int appWidgetId) {
		SunAngleWidgetProvider.getPreferences(this, appWidgetId).edit().remove(PREF_MOCK_TIME).apply();
	}

	private class WidgetAdapter extends BaseAdapter {
		private final int[] appWidgetIds;

		public WidgetAdapter(Context context) {
			appWidgetIds = AppWidgetManager.getInstance(context)
			                               .getAppWidgetIds(new ComponentName(context, SunAngleWidgetProvider.class));
		}

		@Override public int getCount() {
			return appWidgetIds.length;
		}
		@Override public Integer getItem(int position) {
			return appWidgetIds[position];
		}
		@Override public long getItemId(int position) {
			return position;
		}
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
			}
			int appWidgetID = getItem(position);

			TextView text1 = (TextView)convertView.findViewById(android.R.id.text1);
			text1.setText(String.valueOf(appWidgetID));

			TextView text2 = (TextView)convertView.findViewById(android.R.id.text2);
			text2.setText(String.format(Locale.ROOT, "%1$.0f @ %2$tF %<tT",
					getMockedAngle(appWidgetID), new Date(getMockedTime(appWidgetID))));
			return convertView;
		}
	}
}
