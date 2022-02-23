package net.twisterrob.sun.android

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import java.lang.Thread.sleep
import javax.inject.Inject
import kotlin.concurrent.thread

class LocationRetriever @Inject constructor(
	private val context: Context
) {

	@set:VisibleForTesting
	var checkPassive: Boolean = true

	private interface LocationUpdate {

		fun cachedLocation(location: Location)
		fun newLocation(location: Location)
		fun noLocation()
		fun noLocationButRequesting(fallback: LocationListenerCompat)
	}

	fun get(timeout: Long = Long.MAX_VALUE, block: (Location?) -> Unit) {
		val locationManager = context.getSystemService<LocationManager>()!!
		locationManager.getLocation(object : LocationUpdate {
			lateinit var timeoutThread: Thread

			override fun noLocation() {
				block(null)
			}

			override fun cachedLocation(location: Location) {
				block(location)
			}

			override fun noLocationButRequesting(fallback: LocationListenerCompat) {
				timeoutThread = thread(name = "LocationRetriever.timeout", isDaemon = true) {
					try {
						sleep(timeout)
						locationManager.clearUpdates(fallback)
						block(null)
					} catch (ex: InterruptedException) {
						// We got an actual update from fallback.
						locationManager.clearUpdates(fallback)
					}
				}
			}

			override fun newLocation(location: Location) {
				if (::timeoutThread.isInitialized) {
					timeoutThread.interrupt()
				}
				block(location)
			}
		})
	}

	@RequiresPermission(value = ACCESS_FINE_LOCATION, conditional = true /*guarded by hasLocationPermission()*/)
	private fun LocationManager.clearUpdates(fallback: LocationListenerCompat) {
		if (hasLocationPermission()) {
			LocationManagerCompat.removeUpdates(this, fallback)
		}
	}

	@RequiresPermission(value = ACCESS_FINE_LOCATION, conditional = true /*guarded by hasLocationPermission()*/)
	private fun LocationManager.getLocation(callback: LocationUpdate) {
		if (!hasLocationPermission()) {
			if (Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "No location permission granted, stopping ${this} for ${callback}")
			}
			callback.noLocation()
			return
		}
		if (checkPassive) {
			// The passive provider doesn't seem to work with coarse permission only.
			this.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)?.let { location ->
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "${this} found cached location in passive provider: $location")
				}
				callback.cachedLocation(location)
				return
			}
		}
		val criteria = Criteria().apply {
			accuracy = Criteria.ACCURACY_COARSE
			powerRequirement = Criteria.POWER_LOW
		}
		val provider = this.getBestProvider(criteria, true) ?: LocationManager.PASSIVE_PROVIDER
		this.getLastKnownLocation(provider)?.let { location ->
			if (Log.isLoggable(TAG, Log.VERBOSE)) {
				Log.v(TAG, "${this} found cached location in best (${criteria}) provider: ${provider}, $location")
			}
			callback.cachedLocation(location)
			return
		}
		val fallback = object : LocationListenerCompat {
			override fun onLocationChanged(location: Location) {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "${this}.onLocationChanged(${location})")
				}
				clearUpdates(this)
				callback.newLocation(location)
			}

			override fun onLocationChanged(locations: List<Location>) {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "${this}.onLocationChanged(${locations})")
				}
				clearUpdates(this)
				callback.newLocation(locations.last())
			}
		}
		if (Log.isLoggable(TAG, Log.VERBOSE)) {
			Log.v(TAG, "No location, request update on ${provider} for ${fallback}")
		}
		callback.noLocationButRequesting(fallback)
		@Suppress("DEPRECATION")
		// Cannot use LocationManagerCompat.getCurrentLocation yet:
		// tried, but it gets into infinite loop when there's no location and runs on a different thread.
		// TODO https://developer.android.com/training/location/retrieve-current.html#GetLocation
		this.requestSingleUpdate(provider, fallback, null)
	}

	private fun hasLocationPermission(): Boolean {
		val hasFinePermission = checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
		val hasCoarsePermission = checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
		return hasFinePermission || hasCoarsePermission
	}

	companion object {

		private const val TAG = "Sun"
	}
}
