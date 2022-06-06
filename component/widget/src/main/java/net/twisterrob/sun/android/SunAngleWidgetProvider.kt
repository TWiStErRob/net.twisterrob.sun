package net.twisterrob.sun.android

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import net.twisterrob.sun.android.logic.SunAngleWidgetUpdater
import java.util.Locale
import javax.inject.Inject

class SunAngleWidgetProvider : LoggingAppWidgetProvider() {

	@Inject lateinit var updater: SunAngleWidgetUpdater
	@Inject lateinit var locations: LocationRetriever

	override fun onReceive(context: Context, intent: Intent) {
		DaggerWidgetComponent.factory().create(context).inject(this)
		super.onReceive(context, intent)
	}

	override fun onDeleted(context: Context, appWidgetIds: IntArray) {
		super.onDeleted(context, appWidgetIds)
		for (appWidgetId in appWidgetIds) {
			SunAngleWidgetPreferences.getPreferences(context, appWidgetId).edit().clear().apply()
		}
	}

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, vararg appWidgetIds: Int) {
		super.onUpdate(context, appWidgetManager, appWidgetIds)
		val async = goAsync()
		locations.get(timeout = MAX_ASYNC_LOCATION_TIME) { location ->
			try {
				updateAll(location, appWidgetIds)
			} finally {
				async.finish()
			}
		}
	}

	private fun updateAll(location: Location?, appWidgetIds: IntArray) {
		@Suppress("LoopToCallChain")
		// Keeping it as for loop as the alternative would hide the side-effect and mislead.
		// Suggestions welcome!
		for (appWidgetId in appWidgetIds) {
			if (!updater.update(appWidgetId, location)) {
				Log.w(TAG, "${this}.update(${appWidgetId}) failed.")
			}
		}
	}

	override fun toString(): String =
		"%08x".format(Locale.ROOT, this.hashCode())

	companion object {

		private const val TAG = "Sun"

		/**
		 * Be well withing the limits of [android.content.BroadcastReceiver]s.
		 * https://developer.android.com/guide/topics/appwidgets/advanced#broadcastreceiver-duration
		 */
		private const val MAX_ASYNC_LOCATION_TIME: Long = 5000
	}
}
