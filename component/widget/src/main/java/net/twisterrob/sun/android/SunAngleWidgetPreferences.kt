package net.twisterrob.sun.android

import android.content.Context
import android.content.SharedPreferences
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation

object SunAngleWidgetPreferences {

	private const val PREF_NAME = "SunAngleWidget"

	/**
	 * User defined state of the relation toggle.
	 * Configurable on the UI and affects widget calculations and UI.
	 *
	 * *type*: [String]
	 * *description*: [ThresholdRelation.name],
	 * *default*: [ThresholdRelation.ABOVE]
	 */
	const val PREF_THRESHOLD_RELATION: String = "relation"

	@JvmField
	val DEFAULT_THRESHOLD_RELATION: ThresholdRelation = ThresholdRelation.ABOVE

	/**
	 * User defined state of the angle.
	 * Configurable on the UI and affects widget calculations and UI.
	 *
	 * *type*: [Double]
	 * *description*: angle in degrees,
	 * *default*: [DEFAULT_THRESHOLD_ANGLE]
	 */
	const val PREF_THRESHOLD_ANGLE: String = "threshold"
	const val DEFAULT_THRESHOLD_ANGLE: Float = 0f

	/**
	 * Developer selection of current angle for debugging with stubbed data.
	 * Configurable on the UI and affects widget calculations and UI.
	 *
	 * *type*: [Double]
	 * *description*: angle in degrees
	 * *default*: [DEFAULT_MOCK_ANGLE]
	 */
	const val PREF_MOCK_ANGLE: String = "mockAngle"
	const val DEFAULT_MOCK_ANGLE: Float = Float.NaN

	/**
	 * Developer selection of current time for debugging with stubbed data.
	 * Configurable on the UI and affects widget calculations and UI.
	 *
	 * *type*: [Long]
	 * *description*: [System.currentTimeMillis]
	 * *default*: [DEFAULT_MOCK_TIME]
	 */
	const val PREF_MOCK_TIME: String = "mockTime"

	/**
	 * Default hard-coded current time for debugging for consistent behavior.
	 * Value is Tue Jul 01 1986 10:26:00 GMT+0000.
	 */
	const val DEFAULT_MOCK_TIME: Long = 520_597_560_000L

	/**
	 * User selection of whether to show the last updated time or not.
	 * Configurable on the UI and affects widget UI.
	 *
	 * *type*: [Boolean]
	 * *description*: true=show
	 * *default*: [DEFAULT_SHOW_UPDATE_TIME]
	 */
	const val PREF_SHOW_UPDATE_TIME: String = "showLastUpdateTime"
	@Suppress("BooleanPropertyNaming") // TODEL https://github.com/detekt/detekt/issues/4920
	const val DEFAULT_SHOW_UPDATE_TIME: Boolean = false

	/**
	 * User selection of whether to show the part of day or not.
	 * Configurable on the UI and affects widget UI.
	 *
	 * *type*: [Boolean]
	 * *description*: true=show
	 * *default*: [DEFAULT_SHOW_PART_OF_DAY]
	 */
	const val PREF_SHOW_PART_OF_DAY: String = "showPartOfDay"
	@Suppress("BooleanPropertyNaming") // TODEL https://github.com/detekt/detekt/issues/4920
	const val DEFAULT_SHOW_PART_OF_DAY: Boolean = true

	@JvmStatic
	fun getPreferences(context: Context, appWidgetId: Int): SharedPreferences =
		context
			.applicationContext
			.getSharedPreferences("${PREF_NAME}-${appWidgetId}", Context.MODE_PRIVATE)
}
