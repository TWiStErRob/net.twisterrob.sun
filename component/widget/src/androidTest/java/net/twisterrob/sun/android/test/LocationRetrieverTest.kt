package net.twisterrob.sun.android.test

import android.location.Location
import android.location.provider.ProviderProperties
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import net.twisterrob.sun.android.LocationRetriever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Suppress("detekt.MagicNumber")
class LocationRetrieverTest {

	@get:Rule
	val locationRule = LocationSpooferRule()

	private lateinit var sut: LocationRetriever

	@Before
	fun setUp() {
		sut = LocationRetriever(ApplicationProvider.getApplicationContext()).apply {
			// Need to disable PROVIDER_PASSIVE, because it cannot be overridden as a testProvider.
			isPassiveProviderPreferred = false
		}

		locationRule.spoofer.setProvider(
			TEST_PROVIDER,
			ProviderProperties.Builder()
				.setAccuracy(ProviderProperties.ACCURACY_COARSE)
				.setPowerUsage(ProviderProperties.POWER_USAGE_LOW)
				.build()
		)
	}

	@Test(timeout = 10_000)
	fun testLastKnownLocationReturned() {
		val fakeLocation = locationRule.spoofer.setLocation {
			latitude = 11.1
			longitude = 22.2
		}
		var isActualLocationReceived = false
		var actualLocation: Location? = null
		InstrumentationRegistry.getInstrumentation().runOnMainSync {
			sut.get { location ->
				actualLocation = location
				isActualLocationReceived = true
			}
		}

		assertTrue(isActualLocationReceived)
		assertEquals(fakeLocation, actualLocation)
	}

	@Test(timeout = 10_000)
	fun testFallbackRecordsNewLocation() {
		var isActualLocationReceived = false
		var actualLocation: Location? = null
		val blocker = CountDownLatch(1)
		InstrumentationRegistry.getInstrumentation().runOnMainSync {
			sut.get { location ->
				actualLocation = location
				isActualLocationReceived = true
				blocker.countDown()
			}
		}
		val fakeLocation = locationRule.spoofer.setLocation {
			latitude = 11.1
			longitude = 22.2
		}
		blocker.await(100, TimeUnit.MILLISECONDS)

		assertTrue(isActualLocationReceived)
		assertEquals(fakeLocation, actualLocation)
	}

	@Test(timeout = 10_000)
	fun testFallbackSendsNoLocation() {
		var isActualLocationReceived = false
		var actualLocation: Location? = null
		val blocker = CountDownLatch(1)
		InstrumentationRegistry.getInstrumentation().runOnMainSync {
			sut.get(100) { location ->
				actualLocation = location
				isActualLocationReceived = true
				blocker.countDown()
			}
		}
		sleep(2 * 100L)

		assertTrue(isActualLocationReceived)
		assertEquals(null, actualLocation)
	}

	@Test(timeout = 10_000)
	fun testFallbackSendsNoLocationEvenIfUpdatedAfterTimeout() {
		var isActualLocationReceived = false
		var actualLocation: Location? = null
		val blocker = CountDownLatch(1)
		InstrumentationRegistry.getInstrumentation().runOnMainSync {
			sut.get(100) { location ->
				actualLocation = location
				isActualLocationReceived = true
				blocker.countDown()
			}
		}
		sleep(2 * 100L)
		locationRule.spoofer.setLocation {
			latitude = 11.1
			longitude = 22.2
		}
		sleep(100L)

		assertTrue(isActualLocationReceived)
		assertEquals(null, actualLocation)
	}

	companion object {

		private const val TEST_PROVIDER = "test"

		/**
		 * Initialize separately so that this is not part of each (or the first) individual test.
		 * ```
		 * --------- beginning of main
		 * V/UiAutomation: Init UiAutomation@c405a97[id=38, flags=0]
		 * --------- beginning of system
		 * D/BatterySaverPolicy: accessibility changed to true, updating policy.
		 * --------- beginning of kernel
		 * I/binder  : send failed reply for transaction 1236722 to 5834:5834
		 * E/JavaBinder: !!! FAILED BINDER TRANSACTION !!!  (parcel size = 264)
		 * E/AccessibilityManager: Error while adding an accessibility interaction connection.
		 * E/AccessibilityManager: android.os.DeadObjectException: Transaction failed on small parcel; remote process probably died
		 * E/AccessibilityManager: 	at android.os.BinderProxy.transact(BinderProxy.java:571)
		 * E/AccessibilityManager: 	at android.view.accessibility.AccessibilityManager.addAccessibilityInteractionConnection(AccessibilityManager.java:1307)
		 * E/AccessibilityManager: 	at android.view.ViewRootImpl$AccessibilityInteractionConnectionManager.onAccessibilityStateChanged(ViewRootImpl.java:9953)
		 * E/AccessibilityManager: 	at android.view.accessibility.AccessibilityManager.lambda$notifyAccessibilityStateChanged$0(AccessibilityManager.java:1669)
		 * E/AccessibilityManager: 	at android.os.Handler.handleCallback(Handler.java:938)
		 * E/AccessibilityManager: 	at android.app.ActivityThread.main(ActivityThread.java:7839)
		 * ```
		 */
		@JvmStatic
		@BeforeClass
		fun initializeUiAutomation() {
			// Need to make an action that accesses mUiAutomationConnection.
			InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("echo hello")
		}
	}
}
