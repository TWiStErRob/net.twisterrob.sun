package net.twisterrob.sun.test.screenshots

import android.annotation.SuppressLint
import android.app.ActivityClient
import android.app.IActivityClientController
import android.util.Singleton
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

	private var backup: IActivityClientController? = null

	@Throws(IllegalAccessException::class)
	override fun before() {
		backup = INTERFACE_SINGLETON.mKnownInstance
		INTERFACE_SINGLETON.mKnownInstance = Mockito.mock(IActivityClientController::class.java)
	}

	override fun after() {
		INTERFACE_SINGLETON.mKnownInstance = backup
		backup = null
	}

	companion object {

		private val INTERFACE_SINGLETON_FIELD: Field =
			@SuppressLint("BlockedPrivateApi")
			ActivityClient::class.java
				.getDeclaredField("INTERFACE_SINGLETON")
				.apply { isAccessible = true }

		private val INTERFACE_SINGLETON: Singleton<IActivityClientController>
			@Suppress("UNCHECKED_CAST", "detekt.CastNullableToNonNullableType")
			get() = INTERFACE_SINGLETON_FIELD.get(null) as Singleton<IActivityClientController>

		@SuppressLint("PrivateApi", "BlockedPrivateApi")
		private val mKnownInstanceField: Field =
			Class.forName("android.app.ActivityClient\$ActivityClientControllerSingleton")
				.getDeclaredField("mKnownInstance")
				.apply { isAccessible = true }

		private var Singleton<IActivityClientController>.mKnownInstance: IActivityClientController?
			@Suppress("detekt.CastToNullableType")
			get() = mKnownInstanceField.get(this) as IActivityClientController?
			set(value) = mKnownInstanceField.set(this, value)
	}
}
