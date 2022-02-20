package net.twisterrob.sun.android

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.widget.Toast
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

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		super.onUpdate(context, appWidgetManager, appWidgetIds)
		val async = goAsync()
		locations.get(5000L) { location ->
			try {
				updateAll(location, *appWidgetIds)
			} catch (ex: Exception) {
				Log.e(TAG, "${this}.updateAll", ex)
				Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show()
			} finally {
				async.finish()
			}
		}
	}

	private fun updateAll(location: Location?, vararg appWidgetIds: Int) {
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
	}
}