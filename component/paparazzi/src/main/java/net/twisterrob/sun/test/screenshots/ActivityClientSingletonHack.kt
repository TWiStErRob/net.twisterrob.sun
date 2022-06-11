package net.twisterrob.sun.test.screenshots

import android.annotation.SuppressLint
import android.app.ActivityClient
import android.app.IActivityClientController
import org.junit.rules.ExternalResource
import org.mockito.Mockito
import java.lang.reflect.Field

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 * Note: this does not happen if the activity theme is already set before calling onCreate.
 *
 * Paparazzi 1.0.
 * ```
 * java.lang.NullPointerException
 *     at android.app.ActivityClient$ActivityClientControllerSingleton.create(ActivityClient.java:528)
 *     at android.app.ActivityClient$ActivityClientControllerSingleton.create(ActivityClient.java:517)
 *     at android.util.Singleton.get(Singleton.java:43)
 *     at android.app.ActivityClient.getActivityClientController(ActivityClient.java:504)
 *     at android.app.ActivityClient.setTaskDescription(ActivityClient.java:346)
 *     at android.app.Activity.setTaskDescription(Activity.java:6971)
 *     at android.app.Activity.onApplyThemeResource(Activity.java:5220)
 *     at android.view.ContextThemeWrapper.initializeTheme(ContextThemeWrapper.java:216)
 *     at android.view.ContextThemeWrapper.getTheme(ContextThemeWrapper.java:175)
 *     at android.content.Context.obtainStyledAttributes(Context.java:830)
 *     at androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes(TintTypedArray.java:54)
 *     at androidx.appcompat.app.AppCompatDelegateImpl.attachToWindow(AppCompatDelegateImpl.java:801)
 *     at androidx.appcompat.app.AppCompatDelegateImpl.ensureWindow(AppCompatDelegateImpl.java:779)
 *     at androidx.appcompat.app.AppCompatDelegateImpl.onCreate(AppCompatDelegateImpl.java:506)
 *     at androidx.appcompat.app.AppCompatActivity$2.onContextAvailable(AppCompatActivity.java:131)
 *     at androidx.activity.contextaware.ContextAwareHelper.dispatchOnContextAvailable(ContextAwareHelper.java:99)
 *     at androidx.activity.ComponentActivity.onCreate(ComponentActivity.java:320)
 *     at androidx.fragment.app.FragmentActivity.onCreate(FragmentActivity.java:249)
 *     at net.twisterrob.android.app.WidgetConfigurationActivity.onCreate(WidgetConfigurationActivity.java:25)
 *     at net.twisterrob.sun.android.SunAngleWidgetConfiguration.onCreate(SunAngleWidgetConfiguration.java:144)
 *     at android.app.Activity.onCreate(Activity.java:1665)
 * ```
 */
class ActivityClientSingletonHack : ExternalResource() {

	private var backup: Any? = null

	@Throws(IllegalAccessException::class)
	override fun before() {
		backup = STATIC[INTERFACE_SINGLETON][mKnownInstance]
		STATIC[INTERFACE_SINGLETON][mKnownInstance] = Mockito.mock(IActivityClientController::class.java)
	}

	override fun after() {
		try {
			STATIC[INTERFACE_SINGLETON][mKnownInstance] = backup
			backup = null
		} catch (ex: IllegalAccessException) {
			@Suppress("NullableToStringCall") // Exactly what I want.
			throw IllegalStateException("Cannot restore original state: ${backup}", ex)
		}
	}

	companion object {

		@SuppressLint("BlockedPrivateApi")
		private val INTERFACE_SINGLETON: Field = run {
			try {
				ActivityClient::class.java
					.getDeclaredField("INTERFACE_SINGLETON")
					.apply { isAccessible = true }
			} catch (ex: NoSuchFieldException) {
				throw IllegalStateException(ex)
			} catch (ex: IllegalAccessException) {
				throw IllegalStateException(ex)
			}
		}

		@SuppressLint("PrivateApi", "BlockedPrivateApi")
		private val mKnownInstance: Field = run {
			try {
				Class.forName("android.app.ActivityClient\$ActivityClientControllerSingleton")
					.getDeclaredField("mKnownInstance")
					.apply { isAccessible = true }
			} catch (ex: NoSuchFieldException) {
				throw IllegalStateException(ex)
			} catch (ex: IllegalAccessException) {
				throw IllegalStateException(ex)
			}
		}
	}
}
