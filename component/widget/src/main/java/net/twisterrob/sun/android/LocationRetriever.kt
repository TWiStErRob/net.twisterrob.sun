package net.twisterrob.sun.android

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import javax.inject.Inject

class LocationRetriever @Inject constructor(
	private val context: Context
) {

	fun get(block: (Location?) -> Unit) {
		val fallback = object : LocationListenerCompat {
			override fun onLocationChanged(location: Location) {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "${this}.onLocationChanged(${location})")
				}
				clearLocation(this)
				block(location)
			}
		}
		val location = getLocation(fallback)
		// If this is null, then block might be called twice: once now, and once fallback completes (if at all).
		block(location)
	}

	@RequiresPermission(value = ACCESS_FINE_LOCATION, conditional = true /*guarded by hasLocationPermission()*/)
	private fun clearLocation(fallback: LocationListenerCompat) {
		if (hasLocationPermission()) {
			val lm = context.getSystemService<LocationManager>()!!
			LocationManagerCompat.removeUpdates(lm, fallback)
		}
	}

	// @Suppress("DEPRECATION") Cannot use LocationManagerCompat.getCurrentLocation yet:
	// tried, but it gets into infinite loop when there's no location and runs on a different thread.
	// TODO https://developer.android.com/training/location/retrieve-current.html#GetLocation
	@RequiresPermission(value = ACCESS_FINE_LOCATION, conditional = true /*guarded by hasLocationPermission()*/)
	private fun getLocation(fallback: LocationListenerCompat): Location? {
		if (!hasLocationPermission()) {
			Log.w(TAG, "No location permission granted, stopping ${this} for ${fallback}")
			return null
		}
		val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
		// The passive provider doesn't seem to work with coarse permission only.
		var location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
		if (location == null) {
			val criteria = Criteria().apply {
				accuracy = Criteria.ACCURACY_COARSE
				powerRequirement = Criteria.POWER_LOW
			}
			val provider = lm.getBestProvider(criteria, true)
			if (provider != null) {
				location = lm.getLastKnownLocation(provider)
				if (location == null) {
					if (Log.isLoggable(TAG, Log.VERBOSE)) {
						Log.v(TAG, "No location, request update on ${provider} for ${fallback}")
					}
					@Suppress("DEPRECATION")
					lm.requestSingleUpdate(provider, fallback, null)
				}
			} else {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "No provider enabled, wait for update.")
				}
				@Suppress("DEPRECATION")
				lm.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, fallback, null)
			}
		}
		return location
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
