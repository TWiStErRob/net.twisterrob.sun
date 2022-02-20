package net.twisterrob.sun.android.test

import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.core.content.getSystemService
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.Thread.sleep

class LocationRetrieverTest {

	@Test
	fun test() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getInstrumentation().uiAutomation
				.executeShellCommand("appops set ${context.packageName} android:mock_location allow")
			sleep(1000)
		}
		val lm = context.getSystemService<LocationManager>()!!
		assertTrue(lm.isLocationEnabled)

		lm.allProviders.forEach { provider ->
			/**
			 *  Cannot mock the passive provider
			 *    at android.location.ILocationManager$Stub$Proxy.addTestProvider(ILocationManager.java:2652)
			 *    at android.location.LocationManager.addTestProvider(LocationManager.java:2032)
			 *    at android.location.LocationManager.addTestProvider(LocationManager.java:2007)
			 *    ... 30 trimmed
			 *  Caused by: android.os.RemoteException: Remote stack trace:
			 *    at com.android.server.location.provider.PassiveLocationProviderManager.setMockProvider(PassiveLocationProviderManager.java:49)
			 *    at com.android.server.location.LocationManagerService.addTestProvider(LocationManagerService.java:1314)
			 *    at android.location.ILocationManager$Stub.onTransact(ILocationManager.java:1215)
			 */
			if (provider != LocationManager.PASSIVE_PROVIDER) {
				lm.addTestProvider(provider, lm.getProviderProperties(provider)!!)
				lm.setTestProviderEnabled(provider, false)
			}
		}

		val testProviderProps = ProviderProperties.Builder()
			.setAccuracy(ProviderProperties.ACCURACY_COARSE)
			.setPowerUsage(ProviderProperties.POWER_USAGE_LOW)
			.build()

		lm.addTestProvider(TEST_PROVIDER, testProviderProps)
		assertTrue(lm.hasProvider(TEST_PROVIDER))

		lm.setTestProviderEnabled(TEST_PROVIDER, true)
		assertTrue(lm.isProviderEnabled(TEST_PROVIDER))

		val testLocation = Location(TEST_PROVIDER).apply {
			time = System.currentTimeMillis()
			elapsedRealtimeNanos = SystemClock.elapsedRealtime()
			accuracy = 0.0f
			latitude = 1.0
			longitude = 2.0
			altitude = 100.0
		}
		lm.setTestProviderLocation(TEST_PROVIDER, testLocation)

//		val intent = Intent("android.location.GPS_ENABLED_CHANGE")
//		intent.putExtra("enabled", true)
//		context.sendBroadcast(intent)
//		sleep(1000)

//		context.getSystemService<AppOpsManager>()!!.

//		val setting = Settings.Secure.getString (context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
//		val removed = setting.split(",").minus(LocationManager.GPS_PROVIDER).joinToString(separator = ",")
//		Settings.Secure.putString (context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED, removed)

//		DataOutputStream(Runtime.getRuntime().exec("su").outputStream).run {
//			arrayOf("cd /system/bin", "settings put secure location_providers_allowed +gps", "exit").forEach { cmd ->
//				writeBytes("$cmd\n")
//			}
//			flush()
//			close()
//		}
		val providers = lm.allProviders.joinToString(separator = "\n") {
			"$it(${lm.isProviderEnabled(it)}):${lm.getProviderProperties(it).toString()}"
		}
		val criteria = Criteria().apply {
			accuracy = Criteria.ACCURACY_COARSE
			powerRequirement = Criteria.POWER_LOW
		}
		val provider = lm.getBestProvider(criteria, true) ?: LocationManager.PASSIVE_PROVIDER
		val last = lm.getLastKnownLocation(provider)
		val lastPassive = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
		throw Error(provider + "->" + last.toString() + "\npassive->" + lastPassive + "\n" + providers)
	}

	// https://android.googlesource.com/platform/packages/apps/Settings/+/ics-plus-aosp/src/com/android/settings/widget/SettingsAppWidgetProvider.java#714
	fun Context.turnGPSOn() {
		val provider = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
		if (LocationManager.GPS_PROVIDER in provider) {
			val poke = Intent()
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider")
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
			poke.data = Uri.parse("custom:3") // SettingsAppWidgetProvider.BUTTON_GPS
			sendBroadcast(poke)
		}
	}

	companion object {

		private const val TEST_PROVIDER = "test"
	}
}
