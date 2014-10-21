package net.twisterrob.android.content.res;

import android.content.res.Resources;

public class TextArray extends ObjectArray<CharSequence> {
	public TextArray(Resources res, int arrayResourceID) {
		super(res.getTextArray(arrayResourceID));
	}
}
