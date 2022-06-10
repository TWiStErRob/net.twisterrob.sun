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
import androidx.annotation.VisibleForTesting
import androidx.core.content.getSystemService
import androidx.test.platform.app.InstrumentationRegistry
import org.jetbrains.annotations.TestOnly
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import java.util.concurrent.TimeoutException

class LocationSpoofer(
	private val context: Context
) {

	private val locationManager = context.getSystemService<LocationManager>()!!

	@Suppress("LateinitUsage")
	// It expresses the logic well, it'll be set up later in the lifecycle.
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

		assertFalse(locationManager.hasProvider(testProvider))

		locationManager.addTestProvider(testProvider, testProviderProps)
		assertTrue(locationManager.hasProvider(testProvider))

		locationManager.setTestProviderEnabled(testProvider, true)
		assertTrue(locationManager.isProviderEnabled(testProvider))
	}

	private fun removeTestProvider() {
		if (::testProvider.isInitialized) {
			locationManager.removeTestProvider(testProvider)
		}
	}

	fun setLocation(block: Location.() -> Unit): Location {
		val testLocation = Location(testProvider).apply {
			time = System.currentTimeMillis()
			elapsedRealtimeNanos = SystemClock.elapsedRealtime()
			accuracy = 0.0f
		}
		testLocation.apply(block)
		locationManager.setTestProviderLocation(testProvider, testLocation)
		return testLocation
	}

	private fun ensureLocationSetUp() {
		val ops = context.getSystemService<AppOpsManager>()!!
		fun AppOpsManager.checkMock(): Int =
			checkOp(AppOpsManager.OPSTR_MOCK_LOCATION, context.packageName)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && AppOpsManager.MODE_ALLOWED != ops.checkMock()) {
			Log.d(TAG, "Setting up ${context.packageName} to be able to do MOCK_LOCATION operations.")
			InstrumentationRegistry.getInstrumentation().uiAutomation
				.executeShellCommand("appops set ${context.packageName} android:mock_location allow")
			Log.d(TAG, "Waiting a second to let the system wake up.")
			busyWait(5000, 100) { ops.checkMock() == AppOpsManager.MODE_ALLOWED }
			Log.d(TAG, "Should have MOCK_LOCATION now.")
			assertEquals(AppOpsManager.MODE_ALLOWED, ops.checkMock())
		}
		assertTrue(locationManager.isLocationEnabled)
	}

	private fun disableBuiltInProviders() {
		locationManager.allProviders.forEach { provider ->
			/**
			 * ```
			 * Cannot mock the passive provider
			 *   at android.location.ILocationManager$Stub$Proxy.addTestProvider(ILocationManager.java:2652)
			 *   at android.location.LocationManager.addTestProvider(LocationManager.java:2032)
			 *   at android.location.LocationManager.addTestProvider(LocationManager.java:2007)
			 *   ... 30 trimmed
			 * Caused by: android.os.RemoteException: Remote stack trace:
			 *   at com.android.server.location.provider.PassiveLocationProviderManager.setMockProvider(PassiveLocationProviderManager.java:49)
			 *   at com.android.server.location.LocationManagerService.addTestProvider(LocationManagerService.java:1314)
			 *   at android.location.ILocationManager$Stub.onTransact(ILocationManager.java:1215)
			 * ```
			 */
			if (provider != LocationManager.PASSIVE_PROVIDER) {
				locationManager.addTestProvider(provider, locationManager.getProviderProperties(provider)!!)
				locationManager.setTestProviderEnabled(provider, false)
			}
		}
	}

	private fun restoreBuiltinProviders() {
		locationManager.allProviders.forEach { provider ->
			locationManager.removeTestProvider(provider)
		}
	}

	/**
	 * Debug-only code, call to see what providers would return.
	 */
	@Suppress("unused")
	@VisibleForTesting
	@TestOnly
	fun diagnostics() {
		val providers = locationManager.allProviders.joinToString(separator = "\n") {
			"$it(${locationManager.isProviderEnabled(it)}):${locationManager.getProviderProperties(it).toString()}"
		}
		val criteria = Criteria().apply {
			accuracy = Criteria.ACCURACY_COARSE
			powerRequirement = Criteria.POWER_LOW
		}
		val bestProvider = locationManager.getBestProvider(criteria, true) ?: LocationManager.PASSIVE_PROVIDER
		val last = locationManager.getLastKnownLocation(bestProvider)
		val lastPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
		@Suppress("ForbiddenMethodCall", "NullableToStringCall")
		println("${bestProvider}->${last}\npassive->${lastPassive}\n${providers}")
	}

	companion object {

		private const val TAG: String = "LocationSpoofer"
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
