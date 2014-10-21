package net.twisterrob.android.content.res;

import android.content.res.Resources;

public class StringArray extends ObjectArray<String> {
	public StringArray(Resources res, int arrayResourceID) {
		super(res.getStringArray(arrayResourceID));
	}
}

