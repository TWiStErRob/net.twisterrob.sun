package net.twisterrob.sun.test.screenshots

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.IActivityManager
import android.util.Singleton
import org.junit.rules.ExternalResource
import java.lang.reflect.Field
import java.lang.reflect.Proxy

/**
 * Fixes the following exception during Paparazzi screenshot tests.
 *
 * Paparazzi 1.0.
 * ```
 * java.lang.NullPointerException
 *     at android.app.PendingIntent.getBroadcastAsUser(PendingIntent.java:649)
 *     at android.app.PendingIntent.getBroadcast(PendingIntent.java:632)
 *     at net.twisterrob.sun.android.logic.SunAngleWidgetView.createRefreshIntent(SunAngleWidgetView.java:187)
 *     at net.twisterrob.sun.android.logic.SunAngleWidgetView.createUpdateViews(SunAngleWidgetView.java:82)
 * ```
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
class ActivityManagerSingletonHack : ExternalResource() {

	private var backup: Any? = null

	@Throws(IllegalAccessException::class)
	override fun before() {
		backup = STATIC[IActivityManagerSingleton]
		STATIC[IActivityManagerSingleton] = object : Singleton<IActivityManager>() {
			override fun create(): IActivityManager =
				mockActivityManager()
		}
	}

	override fun after() {
		try {
			STATIC[IActivityManagerSingleton] = backup
			backup = null
		} catch (e: IllegalAccessException) {
			@Suppress("NullableToStringCall") // Exactly what I want.
			throw IllegalStateException("Cannot restore original state: ${backup}", e)
		}
	}

	companion object {

		@SuppressLint("PrivateApi")
		private val IActivityManagerSingleton: Field = run {
			try {
				ActivityManager::class.java
					.getDeclaredField("IActivityManagerSingleton")
					.apply { isAccessible = true }
					.apply { clearFinal() }
			} catch (e: NoSuchFieldException) {
				throw IllegalStateException(e)
			} catch (e: IllegalAccessException) {
				throw IllegalStateException(e)
			}
		}
	}
}

/**
 * Workaround because `IActivityManager manager = mock(IActivityManager.class)`
 * throws [IncompatibleClassChangeError]:
 * ```
 * org.mockito.exceptions.base.MockitoException:
 * Mockito cannot mock this class: interface android.app.IActivityManager.
 *
 * Underlying exception: java.lang.IllegalStateException:
 * Failed to invoke proxy for public abstract java.lang.reflect.AnnotatedElement[]
 * net.bytebuddy.description.type.TypeDescription$Generic$AnnotationReader$Delegator$ForLoadedExecutableParameterType$Dispatcher.getAnnotatedParameterTypes(java.lang.Object)
 *
 * Caused by: java.lang.IncompatibleClassChangeError:
 * android.app.ApplicationErrorReport and android.app.ApplicationErrorReport$ParcelableCrashInfo disagree on InnerClasses attribute
 * ```
 */
private fun mockActivityManager(): IActivityManager {
	val instance = Proxy.newProxyInstance(
		IActivityManager::class.java.classLoader,
		arrayOf<Class<*>>(IActivityManager::class.java)
	) { _, _, _ ->
		null
	}
	return (instance ?: error("Proxy created for IActivityManager is null")) as IActivityManager
}
