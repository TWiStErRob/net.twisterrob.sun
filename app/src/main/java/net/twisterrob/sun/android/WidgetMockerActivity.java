package net.twisterrob.sun.android;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import static android.view.inputmethod.EditorInfo.*;

import net.twisterrob.android.content.pref.WidgetPreferences;

import static net.twisterrob.sun.android.SunAngleWidgetProvider.*;
public class WidgetMockerActivity extends ListActivity {
	private static final String TAG = "asdf";

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
		final EditText numberEditor = new EditText(this);
		numberEditor.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL | TYPE_NUMBER_FLAG_SIGNED);
		numberEditor.setText(String.valueOf(getCurrentAngle(appWidgetId)));

		new AlertDialog.Builder(this)
				.setTitle("Enter an angle")
				.setView(numberEditor)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						float result = Float.parseFloat(numberEditor.getText().toString());
						setCurrentAngle(appWidgetId, result);
						((BaseAdapter)getListAdapter()).notifyDataSetChanged();
					}
				})
				.create()
				.show();
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Prefill");
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				prefill();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void prefill() {
		float[] presets = {90, -90, 0, -3, -9, -15, -6, -12, -18};
		for (int i = 0; i < getListAdapter().getCount() && i < presets.length; ++i) {
			int appWidgetId = (Integer)getListAdapter().getItem(i);
			setCurrentAngle(appWidgetId, presets[i]);
		}
		((BaseAdapter)getListAdapter()).notifyDataSetChanged();
	}

	private float getCurrentAngle(int appWidgetID) {
		return new WidgetPreferences(this, PREF_NAME, appWidgetID)
				.getFloat(PREF_MOCK_ANGLE, Float.NaN);
	}

	private void setCurrentAngle(int appWidgetId, float result) {
		new WidgetPreferences(this, PREF_NAME, appWidgetId)
				.edit()
				.putFloat(PREF_MOCK_ANGLE, result)
				.apply();
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
			TextView text1 = (TextView)convertView.findViewById(android.R.id.text1);
			int appWidgetID = getItem(position);
			text1.setText(String.valueOf(appWidgetID));
			TextView text2 = (TextView)convertView.findViewById(android.R.id.text2);
			text2.setText(String.valueOf(getCurrentAngle(appWidgetID)));
			return convertView;
		}
	}
}
