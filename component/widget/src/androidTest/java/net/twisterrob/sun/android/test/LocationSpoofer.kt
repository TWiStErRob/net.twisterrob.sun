package net.twisterrob.sun.android.test

import android.app.AppOpsManager
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Binder
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.content.getSystemService
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.util.concurrent.TimeoutException

class LocationSpoofer(
	private val context: Context
) {

	private val lm = context.getSystemService<LocationManager>()!!
	private lateinit var testProvider: String

	fun initialize() {
		ensureLocationSetUp()
		disableBuiltInProviders()
	}

	fun cleanup() {
		removeTestProvider()
		restoreBuiltinProviders()
	}

	fun setProvider(
		testProvider: String,
		testProviderProps: ProviderProperties = ProviderProperties.Builder().build()
	) {
		check(!this::testProvider.isInitialized) {
			"This class is only designed for one test provider."
		}
		this.testProvider = testProvider

		assertFalse(lm.hasProvider(testProvider))

		lm.addTestProvider(testProvider, testProviderProps)
		assertTrue(lm.hasProvider(testProvider))

		lm.setTestProviderEnabled(testProvider, true)
		assertTrue(lm.isProviderEnabled(testProvider))
	}

	private fun removeTestProvider() {
		if (::testProvider.isInitialized) {
			lm.removeTestProvider(testProvider)
		}
	}

	fun setLocation(block: Location.() -> Unit): Location {
		val testLocation = Location(testProvider).apply {
			time = System.currentTimeMillis()
			elapsedRealtimeNanos = SystemClock.elapsedRealtime()
			accuracy = 0.0f
		}
		testLocation.apply(block)
		lm.setTestProviderLocation(testProvider, testLocation)
		return testLocation
	}

	private fun ensureLocationSetUp() {
		val ops = context.getSystemService<AppOpsManager>()!!
		fun AppOpsManager.checkMock(): Int =
			checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, context.packageName)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && AppOpsManager.MODE_ALLOWED != ops.checkMock()) {
			Log.d("LocationSpoofer", "Setting up ${context.packageName} to be able to do MOCK_LOCATION operations.")
			InstrumentationRegistry.getInstrumentation().uiAutomation
				.executeShellCommand("appops set ${context.packageName} android:mock_location allow")
			Log.d("LocationSpoofer", "Waiting a second to let the system wake up.")
			busyWait(5000, 100) { ops.checkMock() == AppOpsManager.MODE_ALLOWED }
			Log.d("LocationSpoofer", "Should have MOCK_LOCATION now.")
			assertEquals(AppOpsManager.MODE_ALLOWED, ops.checkMock())
		}
		assertTrue(lm.isLocationEnabled)
	}

	private fun disableBuiltInProviders() {
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
	}

	private fun restoreBuiltinProviders() {
		lm.allProviders.forEach { provider ->
			lm.removeTestProvider(provider)
		}
	}

	private fun diagnostics() {
		val providers = lm.allProviders.joinToString(separator = "\n") {
			"$it(${lm.isProviderEnabled(it)}):${lm.getProviderProperties(it).toString()}"
		}
		val criteria = Criteria().apply {
			accuracy = Criteria.ACCURACY_COARSE
			powerRequirement = Criteria.POWER_LOW
		}
		val bestProvider = lm.getBestProvider(criteria, true) ?: LocationManager.PASSIVE_PROVIDER
		val last = lm.getLastKnownLocation(bestProvider)
		val lastPassive = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
		println(bestProvider + "->" + last.toString() + "\npassive->" + lastPassive + "\n" + providers)
	}
}

private fun AppOpsManager.checkOp(op: String, packageName: String): /*@AppOpsManager.Mode*/ Int =
	if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
		unsafeCheckOpNoThrow(op, Binder.getCallingUid(), packageName)
	} else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
		@Suppress("DEPRECATION")
		checkOpNoThrow(op, Binder.getCallingUid(), packageName)
	} else {
		error("checkOpNoThrow doesn't exist before KitKat")
	}

private fun busyWait(timeout: Long, poll: Long, condition: () -> Boolean) {
	check(poll >= 0) { "Polling time must be positive." }
	check(timeout >= 0) { "Timeout must be positive." }
	var time = timeout
	while (true) {
		if (condition()) break
		time -= poll
		if (time < 0) TimeoutException("Busy wait on $condition timed out after $timeout")
		Thread.sleep(poll)
	}
}
