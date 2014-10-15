package net.twisterrob.android.content.pref;

import java.util.Map.Entry;
import java.util.Set;

import android.annotation.*;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

public class PostfixedPreferencesEditor implements Editor {
	private final SharedPreferences prefs;
	private final SharedPreferences.Editor edit;
	private final String postFix;

	@SuppressLint("CommitPrefEdits")
	public PostfixedPreferencesEditor(SharedPreferences prefs, String postFix) {
		this.prefs = prefs;
		this.edit = prefs.edit();
		this.postFix = postFix;
	}

	public Editor putString(String key, String value) {
		edit.putString(key + postFix, value);
		return this;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Editor putStringSet(String key, Set<String> values) {
		edit.putStringSet(key + postFix, values);
		return this;
	}

	public Editor putInt(String key, int value) {
		edit.putInt(key + postFix, value);
		return this;
	}

	public Editor putLong(String key, long value) {
		edit.putLong(key + postFix, value);
		return this;
	}

	public Editor putFloat(String key, float value) {
		edit.putFloat(key + postFix, value);
		return this;
	}

	public Editor putBoolean(String key, boolean value) {
		edit.putBoolean(key + postFix, value);
		return this;
	}

	public Editor remove(String key) {
		edit.remove(key + postFix);
		return this;
	}

	public Editor clear() {
		for (Entry<String, ?> entry : prefs.getAll().entrySet()) {
			if (entry.getKey().endsWith(postFix)) {
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
