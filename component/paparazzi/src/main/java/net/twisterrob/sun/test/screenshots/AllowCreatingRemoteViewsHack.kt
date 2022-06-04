package net.twisterrob.sun.test.screenshots

import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.Application
import android.content.Context
import org.junit.rules.ExternalResource
import org.mockito.Answers.CALLS_REAL_METHODS
import org.mockito.Mockito
import java.lang.reflect.Field

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 *
 * ```
 * java.lang.IllegalStateException: Cannot create remote views out of an aplication.
 *     at android.widget.RemoteViews.getApplicationInfo(RemoteViews.java:3757)
 *     at android.widget.RemoteViews.<init>(RemoteViews.java:2212)
 *     at net.twisterrob.sun.android.logic.SunAngleWidgetUpdater.createUpdateViews(SunAngleWidgetUpdater.java:149)
 * ```
 */
class AllowCreatingRemoteViewsHack(
	private val contextProvider: () -> Context
) : ExternalResource() {

	private var backup: Any? = null

	@Throws(IllegalAccessException::class)
	override fun before() {
		backup = sCurrentActivityThread.get(null)
		// Default visible constructor, convoluted way to create instance of it.
		val thread = Mockito.mock(
			ActivityThread::class.java, Mockito.withSettings()
				.defaultAnswer(CALLS_REAL_METHODS)
				.useConstructor()
		)
		mInitialApplication.set(thread, object : Application() {
			init {
				attachBaseContext(contextProvider())
			}
		})
		sCurrentActivityThread.set(null, thread)
	}

	override fun after() {
		try {
			sCurrentActivityThread.set(null, backup)
			backup = null
		} catch (e: IllegalAccessException) {
			throw IllegalStateException("Cannot restore original state: $backup", e)
		}
	}

	companion object {

		@SuppressLint("DiscouragedPrivateApi")
		private val sCurrentActivityThread: Field = run {
			try {
				ActivityThread::class.java
					.getDeclaredField("sCurrentActivityThread")
					.apply { isAccessible = true }
			} catch (e: NoSuchFieldException) {
				throw IllegalStateException(e)
			}
		}

		@SuppressLint("DiscouragedPrivateApi")
		private val mInitialApplication: Field = run {
			try {
				ActivityThread::class.java
					.getDeclaredField("mInitialApplication")
					.apply { isAccessible = true }
			} catch (e: NoSuchFieldException) {
				throw IllegalStateException(e)
			}
		}
	}
}
