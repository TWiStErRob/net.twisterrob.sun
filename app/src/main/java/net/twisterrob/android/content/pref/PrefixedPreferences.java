package net.twisterrob.android.content.pref;

import android.content.Context;

public class PrefixedPreferences extends FixedPreferences {
	private final String preFix;

	public PrefixedPreferences(Context context, String name, String preFix) {
		super(context, name);
		this.preFix = preFix;
	}

	@Override protected String composeKey(String key) {
		return preFix + key;
	}

	@Override protected String decomposeKey(String key) {
		return key.substring(preFix.length(), key.length());
	}

	@Override protected boolean isComposed(String key) {
		return key.startsWith(preFix);
	}
}
