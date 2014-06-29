package net.twisterrob.android.sun.content;

import java.util.Set;

import android.annotation.TargetApi;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.Build;

public class PostfixedPreferencesEditor implements Editor {
	private final SharedPreferences.Editor edit;
	private final String postFix;

	public PostfixedPreferencesEditor(SharedPreferences.Editor edit, String postFix) {
		this.edit = edit;
		this.postFix = postFix;
	}

	public Editor putString(String key, String value) {
		return edit.putString(key + postFix, value);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Editor putStringSet(String key, Set<String> values) {
		return edit.putStringSet(key + postFix, values);
	}

	public Editor putInt(String key, int value) {
		return edit.putInt(key + postFix, value);
	}

	public Editor putLong(String key, long value) {
		return edit.putLong(key + postFix, value);
	}

	public Editor putFloat(String key, float value) {
		return edit.putFloat(key + postFix, value);
	}

	public Editor putBoolean(String key, boolean value) {
		return edit.putBoolean(key + postFix, value);
	}

	public Editor remove(String key) {
		return edit.remove(key + postFix);
	}

	public Editor clear() {
		return edit.clear();
	}

	public boolean commit() {
		return edit.commit();
	}

	public void apply() {
		edit.apply();
	}
}
