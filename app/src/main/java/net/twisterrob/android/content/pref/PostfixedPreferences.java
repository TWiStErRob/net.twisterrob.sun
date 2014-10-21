package net.twisterrob.android.content.pref;

import android.content.Context;

public class PostfixedPreferences extends FixedPreferences {
	private final String postFix;

	public PostfixedPreferences(Context context, String name, String postFix) {
		super(context, name);
		this.postFix = postFix;
	}

	@Override protected String composeKey(String key) {
		return key + postFix;
	}

	@Override protected String decomposeKey(String key) {
		return key.substring(0, key.length() - postFix.length());
	}

	@Override protected boolean isComposed(String key) {
		return key.endsWith(postFix);
	}
}
