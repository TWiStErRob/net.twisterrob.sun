package net.twisterrob.android.content.pref;

import android.content.Context;

public class WidgetPreferences extends PostfixedPreferences {
	public WidgetPreferences(Context context, String name, int appWidgetID) {
		super(context, name, "/" + appWidgetID);
	}
}
