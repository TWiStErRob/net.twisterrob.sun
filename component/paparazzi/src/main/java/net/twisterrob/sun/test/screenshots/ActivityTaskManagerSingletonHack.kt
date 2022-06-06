package net.twisterrob.sun.test.screenshots

import android.annotation.SuppressLint
import android.app.ActivityTaskManager
import android.app.IActivityTaskManager
import android.util.Singleton
import org.junit.rules.ExternalResource
import org.mockito.Mockito
import java.lang.reflect.Field

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 *
 * Note: this does not happen if the activity theme is already set before calling onCreate.
 * ```
 * java.lang.NullPointerException
 *     at android.app.Activity.setTaskDescription(Activity.java:6742)
 *     at android.app.Activity.onApplyThemeResource(Activity.java:5010)
 *     at android.view.ContextThemeWrapper.initializeTheme(ContextThemeWrapper.java:216)
 *     at android.view.ContextThemeWrapper.getTheme(ContextThemeWrapper.java:175)
 *     at android.content.Context.obtainStyledAttributes(Context.java:738)
 *     at android.view.Window.getWindowStyle(Window.java:703)
 *     at com.android.internal.policy.PhoneWindow.generateLayout(PhoneWindow.java:2339)
 *     at com.android.internal.policy.PhoneWindow.installDecor(PhoneWindow.java:2694)
 *     at com.android.internal.policy.PhoneWindow.setContentView(PhoneWindow.java:428)
 *     at android.app.Activity.setContentView(Activity.java:3326)
 * ```
 */
class ActivityTaskManagerSingletonHack : ExternalResource() {

	private var backup: Any? = null

	@Throws(IllegalAccessException::class)
	override fun before() {
		backup = STATIC[IActivityTaskManagerSingleton]
		STATIC[IActivityTaskManagerSingleton] = object : Singleton<IActivityTaskManager>() {
			override fun create(): IActivityTaskManager =
				Mockito.mock(IActivityTaskManager::class.java)
		}
	}

	override fun after() {
		try {
			STATIC[IActivityTaskManagerSingleton] = backup
			backup = null
		} catch (ex: IllegalAccessException) {
			@Suppress("NullableToStringCall") // Exactly what I want.
			throw IllegalStateException("Cannot restore original state: ${backup}", ex)
		}
	}

	companion object {

		@SuppressLint("DiscouragedPrivateApi")
		private val IActivityTaskManagerSingleton: Field = run {
			try {
				ActivityTaskManager::class.java
					.getDeclaredField("IActivityTaskManagerSingleton")
					.apply { isAccessible = true }
					.apply { clearFinal() }
			} catch (ex: NoSuchFieldException) {
				throw IllegalStateException(ex)
			} catch (ex: IllegalAccessException) {
				throw IllegalStateException(ex)
			}
		}
	}
}
