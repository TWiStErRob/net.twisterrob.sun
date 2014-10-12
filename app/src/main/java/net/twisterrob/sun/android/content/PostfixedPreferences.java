package net.twisterrob.sun.android.content;

import java.util.*;

import android.annotation.*;
import android.content.*;
import android.os.Build;

public class PostfixedPreferences implements SharedPreferences {
	private final SharedPreferences prefs;
	private final String postFix;

	public PostfixedPreferences(Context context, String name, String postFix) {
		this.prefs = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
		this.postFix = postFix;
	}

	@SuppressLint("CommitPrefEdits")
	public Editor edit() {
		return new PostfixedPreferencesEditor(prefs.edit(), postFix);
	}

	public Map<String, ?> getAll() {
		return prefs.getAll(); // TODO postfix
	}

	public String getString(String key, String defValue) {
		return prefs.getString(key + postFix, defValue);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getStringSet(String key, Set<String> defValues) {
		return prefs.getStringSet(key + postFix, defValues);
	}

	public int getInt(String key, int defValue) {
		return prefs.getInt(key + postFix, defValue);
	}

	public long getLong(String key, long defValue) {
		return prefs.getLong(key + postFix, defValue);
	}

	public float getFloat(String key, float defValue) {
		return prefs.getFloat(key + postFix, defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key + postFix, defValue);
	}

	public boolean contains(String key) {
		return prefs.contains(key + postFix);
	}

	public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		prefs.registerOnSharedPreferenceChangeListener(listener);  // TODO postfix
	}

	public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
		prefs.unregisterOnSharedPreferenceChangeListener(listener);  // TODO postfix
	}
}
