package net.twisterrob.sun.android;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import static net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation;

public class SunAngleWidgetPreferences {
	public static final String PREF_NAME = "SunAngleWidget";
	/** String: {@link ThresholdRelation#name() ThresholdRelation constant name},
	 * default: {@link ThresholdRelation#ABOVE ABOVE} */
	public static final String PREF_THRESHOLD_RELATION = "relation";
	public static final ThresholdRelation DEFAULT_THRESHOLD_RELATION = ThresholdRelation.ABOVE;
	/** double: angle in degrees,
	 * default: {@value #DEFAULT_THRESHOLD_ANGLE} */
	public static final String PREF_THRESHOLD_ANGLE = "threshold";
	public static final float DEFAULT_THRESHOLD_ANGLE = 0;
	/** double: angle in degrees,
	 * default: {@value #DEFAULT_MOCK_ANGLE} */
	public static final String PREF_MOCK_ANGLE = "mockAngle";
	public static final float DEFAULT_MOCK_ANGLE = Float.NaN;
	/** long: {@link System#currentTimeMillis()},
	 * default: {@value #DEFAULT_MOCK_ANGLE} */
	public static final String PREF_MOCK_TIME = "mockTime";
	public static final long DEFAULT_MOCK_TIME = 520597560L * 1000;
	/** boolean: true=show,
	 * default: {@value #DEFAULT_SHOW_UPDATE_TIME} */
	public static final String PREF_SHOW_UPDATE_TIME = "showLastUpdateTime";
	public static final boolean DEFAULT_SHOW_UPDATE_TIME = false;
	/** boolean: true=show,
	 * default: {@value #DEFAULT_SHOW_PART_OF_DAY} */
	public static final String PREF_SHOW_PART_OF_DAY = "showPartOfDay";
	public static final boolean DEFAULT_SHOW_PART_OF_DAY = true;

	public static @NonNull SharedPreferences getPreferences(@NonNull Context context, int appWidgetId) {
		return context.getApplicationContext()
		              .getSharedPreferences(PREF_NAME + "-" + appWidgetId, Context.MODE_PRIVATE);
	}
}
