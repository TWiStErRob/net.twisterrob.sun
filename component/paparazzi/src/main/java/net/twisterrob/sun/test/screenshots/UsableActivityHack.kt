package net.twisterrob.sun.test.screenshots

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.SystemServiceRegistry
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.Display
import com.android.layoutlib.bridge.android.BridgePackageManager
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.lang.reflect.Field

fun Activity.start(baseContext: Context, intent: Intent? = null) {
	val application = object : Application() {
		init {
			attachBaseContext(
				@Suppress("UnnecessaryLet")
				// Nesting constructors doesn't scale, this chains well.
				baseContext
					.let(::SystemServiceContextWrapper)
					.let(::HackingContextWrapper)
			)
		}

		override fun getApplicationContext(): Context =
			this
	}
	this.attach(application, intent)
	@SuppressLint("NewApi")
	this.theme = application.theme
}

private class SystemServiceContextWrapper(
	baseContext: Context
) : ContextWrapper(baseContext) {

	val services: MutableMap<String, Any> = mutableMapOf()

	override fun getSystemService(name: String): Any =
		try {
			super.getSystemService(name)?.let { Mockito.spy(it) } ?: mockSystemService(name)
		} catch (ex: AssertionError) {
			if (ex.message == "Unsupported Service: ${name}") {
				mockSystemService(name)
			} else {
				throw ex
			}
		}

	private fun mockSystemService(name: String): Any {
		services[name]?.let { return it }
		val serviceClass = getSystemServiceClass(name)
		//println("Falling back to mock ${serviceClass} for system service ${name}.")
		return Mockito.mock(serviceClass).also { services[name] = it }
	}

	private fun getSystemServiceClass(name: String): Class<*> =
		// Note: need to use .toMap on a already-Map, because ArrayMap has problems with iteration on entries.
		SYSTEM_SERVICE_NAMES.toMap().entries.single { it.value == name }.key

	companion object {

		@SuppressLint("BlockedPrivateApi")
		private val SYSTEM_SERVICE_NAMES_FIELD: Field =
			SystemServiceRegistry::class.java
				.getDeclaredField("SYSTEM_SERVICE_NAMES")
				.apply { isAccessible = true }

		val SYSTEM_SERVICE_NAMES: Map<Class<*>, String>
			@Suppress("UNCHECKED_CAST", "detekt.CastNullableToNonNullableType")
			get() = SYSTEM_SERVICE_NAMES_FIELD.get(null) as Map<Class<*>, String>
	}
}

private class HackingContextWrapper(
	baseContext: Context
) : ContextWrapper(baseContext) {

	/**
	 * ```
	 * Caused by: java.lang.NullPointerException
	 *     at androidx.appcompat.app.AppCompatDelegateImpl.attachBaseContext2(AppCompatDelegateImpl.java:430)
	 *     at androidx.appcompat.app.AppCompatActivity.attachBaseContext(AppCompatActivity.java:139)
	 *     ... 53 more
	 * ```
	 */
	override fun createConfigurationContext(overrideConfiguration: Configuration): Context =
		this

	/**
	 * TODO figure out why after https://github.com/cashapp/paparazzi/issues/1861 is resolved.
	 * Need some Kotlin compiler magic because [Context.getDisplayNoVerify] is `@hide`.
	 */
	// Requires -Xdont-warn-on-error-suppression, ERROR_SUPPRESSION is just a marker here.
	@Suppress("NOTHING_TO_OVERRIDE", "ERROR_SUPPRESSION", "KDocUnresolvedReference")
	override fun getDisplayNoVerify(): Display? =
		Mockito.mock(Display::class.java)

	/**
	 * Make sure [PackageManager] is returning something as most methods have a non-null contract.
	 *
	 * Return something from [PackageManager.getActivityInfo] to make sure appcompat:1.4.2 works:
	 * ```
	 * java.lang.NullPointerException
	 *     at androidx.core.app.NavUtils.getParentActivityName(NavUtils.java:263)
	 *     at androidx.core.app.NavUtils.getParentActivityName(NavUtils.java:220)
	 *     at androidx.appcompat.app.AppCompatDelegateImpl.onCreate(AppCompatDelegateImpl.java:511)
	 *     at androidx.appcompat.app.AppCompatActivity$2.onContextAvailable(AppCompatActivity.java:131)
	 *     at androidx.activity.contextaware.ContextAwareHelper.dispatchOnContextAvailable(ContextAwareHelper.java:99)
	 *     at androidx.activity.ComponentActivity.onCreate(ComponentActivity.java:320)
	 *     at androidx.fragment.app.FragmentActivity.onCreate(FragmentActivity.java:249)
	 *     at net.twisterrob.android.app.WidgetConfigurationActivity.onCreate(WidgetConfigurationActivity.java:25)
	 *     at net.twisterrob.sun.android.SunAngleWidgetConfiguration.onCreate(SunAngleWidgetConfiguration.java:144)
	 *     at android.app.Activity.onCreate(Activity.java:1665)
	 * ```
	 *
	 * Return something from [PackageManager.getServiceInfo] to make sure appcompat:1.6.0 works:
	 * ```
	 * java.lang.NullPointerException
	 *     at androidx.appcompat.app.AppCompatDelegate.isAutoStorageOptedIn(AppCompatDelegate.java:872/882)
	 *     at androidx.appcompat.app.AppCompatDelegateImpl.attachBaseContext2(AppCompatDelegateImpl.java:403)
	 *     at androidx.appcompat.app.AppCompatActivity.attachBaseContext(AppCompatActivity.java:141)
	 *     at android.app.Activity.attach(Activity.java:7940)
	 *     at net.twisterrob.sun.test.screenshots.UsableActivityHackKt.attach(UsableActivityHack.kt:147)
	 *     at net.twisterrob.sun.test.screenshots.UsableActivityHackKt.start(UsableActivityHack.kt:34)
	 *     at net.twisterrob.sun.android.SunAngleWidgetConfigurationScreenshotTest.setUp(.java:46)
	 * ```
	 * [ServiceInfo.metaData] needs to be non-null to prevent NPE at 882.
	 */
	override fun getPackageManager(): PackageManager =
		@Suppress("UseIfInsteadOfWhen", "NestedScopeFunctions")
		when (val packageManager = super.getPackageManager()) {
			is BridgePackageManager ->
				Mockito.spy(packageManager).apply {
					Mockito.doReturn(ActivityInfo()).`when`(this)
						.getActivityInfo(ArgumentMatchers.any(), ArgumentMatchers.anyInt())
					Mockito.doReturn(ServiceInfo().apply { metaData = Bundle() }).`when`(this)
						.getServiceInfo(ArgumentMatchers.any(), ArgumentMatchers.anyInt())
				}
			else ->
				packageManager
		}
}

/**
 * Using [Activity.attach]:
 *  * because [Activity.attachBaseContext] doesn't do enough to make the [activity](this) usable.
 *  * reflectively, because it's package private.
 */
private fun Activity.attach(
	application: Application,
	intent: Intent?,
) {
	Activity::class.java
		// Cannot use getDeclaredMethod(name, ...) because some types are hidden.
		.declaredMethods
		// API 36 introduced a second attach with 1 more parameter.
		.filter { it.name == "attach" }
		.minBy { it.parameterTypes.size }
		.apply { isAccessible = true }
		.invoke(
			this@attach,
			application /*Context context*/,
			null /*ActivityThread aThread*/,
			null /*Instrumentation instr*/,
			null /*IBinder token*/,
			0 /*int ident*/,
			application /*Application application*/,
			intent /*Intent intent*/,
			/*
			 * .attach uses info directly at `if (info.softInputMode` and `if (info.uiOptions`.
			 */
			ActivityInfo() /*ActivityInfo info*/,
			null /*CharSequence title*/,
			null /*Activity parent*/,
			null /*String id*/,
			null /*Activity.NonConfigurationInstances lastNonConfigurationInstances*/,
			null /*Configuration config*/,
			null /*String referrer*/,
			null /*IVoiceInteractor voiceInteractor*/,
			null /*Window window*/,
			null /*ActivityConfigCallback activityConfigCallback*/,
			null /*IBinder assistToken*/,
			null /*IBinder shareableActivityToken*/
		)
}
