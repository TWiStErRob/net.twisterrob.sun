package net.twisterrob.sun.android

import android.annotation.TargetApi
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import java.util.Locale

open class LoggingAppWidgetProvider protected constructor() : AppWidgetProvider() {

	private val tag: String = this::class.java.simpleName

	override fun onDeleted(context: Context, appWidgetIds: IntArray) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, "$this.onDeleted(${appWidgetIds.contentToString()})")
		}
		super.onDeleted(context, appWidgetIds)
	}

	override fun onEnabled(context: Context) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, "$this.onEnabled")
		}
		super.onEnabled(context)
	}

	override fun onDisabled(context: Context) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, "$this.onDisabled")
		}
		super.onDisabled(context)
	}

	override fun onReceive(context: Context, intent: Intent) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			intent.getStringExtra(null) // force unparcel
			Log.v(tag, "$this.onReceive(${intent} (${intent.extras ?: "no extras"}))")
		}
		super.onReceive(context, intent) // delegate to other on*()
	}

	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, "$this.onUpdate(${appWidgetIds.contentToString()})")
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds)
	}

	override fun onAppWidgetOptionsChanged(
		context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?
	) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			newOptions?.keySet() // force unparcel
			Log.v(tag, "$this.onAppWidgetOptionsChanged(${appWidgetId}, ${newOptions ?: "no options"})")
		}
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
		if (Log.isLoggable(tag, Log.VERBOSE)) {
			Log.v(tag, "$this.onRestored(${oldWidgetIds.contentToString()}, ${newWidgetIds.contentToString()})")
		}
		super.onRestored(context, oldWidgetIds, newWidgetIds)
	}

	override fun toString(): String =
		String.format(Locale.ROOT, "%08x", this.hashCode())
}
