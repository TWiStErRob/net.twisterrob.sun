package net.twisterrob.sun.android

import android.content.Context
import android.content.SharedPreferences
import net.twisterrob.sun.algo.SunSearchResults.ThresholdRelation

object SunAngleWidgetPreferences {

	private const val PREF_NAME = "SunAngleWidget"

	/**
	 * *type*: [String]
	 * *description*: [ThresholdRelation.name],
	 * *default*: [ThresholdRelation.ABOVE]
	 */
	const val PREF_THRESHOLD_RELATION = "relation"

	@JvmField
	val DEFAULT_THRESHOLD_RELATION = ThresholdRelation.ABOVE

	/**
	 * *type*: [Double]
	 * *description*: angle in degrees,
	 * *default*: [DEFAULT_THRESHOLD_ANGLE]
	 */
	const val PREF_THRESHOLD_ANGLE = "threshold"
	const val DEFAULT_THRESHOLD_ANGLE = 0f

	/**
	 * *type*: [Double]
	 * *description*: angle in degrees
	 * *default*: [DEFAULT_MOCK_ANGLE]
	 */
	const val PREF_MOCK_ANGLE = "mockAngle"
	const val DEFAULT_MOCK_ANGLE = Float.NaN

	/**
	 * *type*: [Long]
	 * *description*: [System.currentTimeMillis]
	 * *default*: [DEFAULT_MOCK_ANGLE]
	 */
	const val PREF_MOCK_TIME = "mockTime"
	const val DEFAULT_MOCK_TIME = 520597560L * 1000

	/**
	 * *type*: [Boolean]
	 * *description*: true=show
	 * *default*: [DEFAULT_SHOW_UPDATE_TIME]
	 */
	const val PREF_SHOW_UPDATE_TIME = "showLastUpdateTime"
	const val DEFAULT_SHOW_UPDATE_TIME = false

	/**
	 * *type*: [Boolean]
	 * *description*: true=show
	 * *default*: [DEFAULT_SHOW_PART_OF_DAY]
	 */
	const val PREF_SHOW_PART_OF_DAY = "showPartOfDay"
	const val DEFAULT_SHOW_PART_OF_DAY = true

	@JvmStatic
	fun getPreferences(context: Context, appWidgetId: Int): SharedPreferences =
		context
			.applicationContext
			.getSharedPreferences("${PREF_NAME}-${appWidgetId}", Context.MODE_PRIVATE)
}
