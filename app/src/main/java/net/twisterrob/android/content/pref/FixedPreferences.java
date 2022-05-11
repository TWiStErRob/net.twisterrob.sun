package net.twisterrob.android.content.pref;

import java.util.*;
import java.util.Map.Entry;

import android.annotation.*;
import android.content.*;
import android.os.Build;

public abstract class FixedPreferences implements SharedPreferences {
	private final SharedPreferences prefs;
	private final Map<OnSharedPreferenceChangeListener, OnSharedPreferenceChangeListener> listeners =
			new WeakHashMap<>();

	public FixedPreferences(Context context, String name) {
		this.prefs = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	protected abstract String composeKey(String key);
	protected abstract String decomposeKey(String key);
	protected abstract boolean isComposed(String key);

	public Editor edit() {
		return new FixedPreferencesEditor();
	}

	public Map<String, ?> getAll() {
		return prefs.getAll(); // TODO return subset
	}

	public String getString(String key, String defValue) {
		return prefs.getString(composeKey(key), defValue);
	}

	@SuppressLint("ObsoleteSdkInt") // Keep for history.
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getStringSet(String key, Set<String> defValues) {
		return prefs.getStringSet(composeKey(key), defValues);
	}

	public int getInt(String key, int defValue) {
		return prefs.getInt(composeKey(key), defValue);
	}

	public long getLong(String key, long defValue) {
		return prefs.getLong(composeKey(key), defValue);
	}

	public float getFloat(String key, float defValue) {
		return prefs.getFloat(composeKey(key), defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(composeKey(key), defValue);
	}

	public boolean contains(String key) {
		return prefs.contains(composeKey(key));
	}

	public void registerOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
		OnSharedPreferenceChangeListener wrapper = listeners.get(listener);
		if (wrapper == null) {
			wrapper = new OnSharedPreferenceChangeListener() {
				@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
					listener.onSharedPreferenceChanged(FixedPreferences.this, decomposeKey(key));
				}
			};
			listeners.put(listener, wrapper);
		}
		prefs.registerOnSharedPreferenceChangeListener(wrapper);
	}

	public void unregisterOnSharedPreferenceChangeListener(final OnSharedPreferenceChangeListener listener) {
		prefs.unregisterOnSharedPreferenceChangeListener(listeners.get(listener));
	}

	protected class FixedPreferencesEditor implements Editor {
		private final SharedPreferences.Editor edit;

		@SuppressLint("CommitPrefEdits")
		public FixedPreferencesEditor() {
			this.edit = prefs.edit();
		}

		public Editor putString(String key, String value) {
			edit.putString(compose(key), value);
			return this;
		}
		private String compose(String key) {
			return composeKey(key);
		}

		@SuppressLint("ObsoleteSdkInt") // Keep for history.
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public Editor putStringSet(String key, Set<String> values) {
			edit.putStringSet(compose(key), values);
			return this;
		}

		public Editor putInt(String key, int value) {
			edit.putInt(compose(key), value);
			return this;
		}

		public Editor putLong(String key, long value) {
			edit.putLong(compose(key), value);
			return this;
		}

		public Editor putFloat(String key, float value) {
			edit.putFloat(compose(key), value);
			return this;
		}

		public Editor putBoolean(String key, boolean value) {
			edit.putBoolean(compose(key), value);
			return this;
		}

		public Editor remove(String key) {
			edit.remove(compose(key));
			return this;
		}

		public Editor clear() {
			for (Entry<String, ?> entry : getAll().entrySet()) {
				if (isComposed(entry.getKey())) {
					edit.remove(entry.getKey());
				}
			}
			return this;
		}

		public boolean commit() {
			return edit.commit();
		}

		public void apply() {
			edit.apply();
		}
	}
}
