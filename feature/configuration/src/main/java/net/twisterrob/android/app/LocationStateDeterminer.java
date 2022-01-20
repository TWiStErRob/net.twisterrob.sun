package net.twisterrob.android.app;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;
import pub.devrel.easypermissions.EasyPermissions;

public class LocationStateDeterminer {

	private final @NonNull Activity context;

	public LocationStateDeterminer(@NonNull Activity context) {
		this.context = context;
	}

	public @NonNull LocationState determine() {
		if (isLocationEnabled()) {
			if (hasCoarse()) {
				if (hasFine()) {
					if (hasBackground()) {
						return LocationState.ALL_GRANTED;
					} else if (shouldBackgroundRationale()) {
						return LocationState.BACKGROUND_DENIED;
					} else if (false) {
						// It is possible this was really denied,
						// so permission=false && shouldShowRationale==false, and requesting even fails,
						// but it is impossible to detect this case with Android SDK APIs.
						// https://stackoverflow.com/a/63487691/253468
						return LocationState.BACKGROUND_DENIED;
					}
					return LocationState.BACKGROUND_DENIED;
				} else if (shouldFineRationale()) {
					return LocationState.FINE_DENIED;
				}
				return LocationState.FINE_DENIED;
			} else if (shouldCoarseRationale()) {
				return LocationState.COARSE_DENIED;
			}
			return LocationState.COARSE_DENIED;
		} else {
			return LocationState.LOCATION_DISABLED;
		}
	}

	@VisibleForTesting boolean isLocationEnabled() {
		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		return LocationManagerCompat.isLocationEnabled(locationManager);
	}

	@VisibleForTesting boolean hasCoarse() {
		return EasyPermissions.hasPermissions(context, permission.ACCESS_COARSE_LOCATION);
	}

	@VisibleForTesting boolean shouldCoarseRationale() {
		return ActivityCompat.shouldShowRequestPermissionRationale(context, permission.ACCESS_COARSE_LOCATION);
	}

	@VisibleForTesting boolean hasFine() {
		return EasyPermissions.hasPermissions(context, permission.ACCESS_FINE_LOCATION);
	}

	@VisibleForTesting boolean shouldFineRationale() {
		return ActivityCompat.shouldShowRequestPermissionRationale(context, permission.ACCESS_FINE_LOCATION);
	}

	@VisibleForTesting boolean hasBackground() {
		if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
			return EasyPermissions.hasPermissions(context, permission.ACCESS_BACKGROUND_LOCATION);
		} else {
			return true;
		}
	}

	@VisibleForTesting boolean shouldBackgroundRationale() {
		if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
			return ActivityCompat.shouldShowRequestPermissionRationale(context, permission.ACCESS_BACKGROUND_LOCATION);
		} else {
			return false;
		}
	}

	/**
	 * Implementation of <a href="https://developer.android.com/training/location/permissions">Location Permissions</a>.
	 */
	public static @NonNull String[] calculateForegroundPermissionsToRequest() {
		String[] locationPermissions;
		if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
			// Complex world. Background location need to be requested separately from FINE and COARSE.
			// > On Android 11 (API level 30) and higher, however, the system dialog doesn't include the "Allow all the time" option.
			// > Instead, users must enable background location on a settings page.
			// > If you try to request only ACCESS_FINE_LOCATION, the system ignores the request on some releases of Android 12.
			// > If your app targets Android 12 or higher, the system logs the following error message in Logcat:
			// > ACCESS_FINE_LOCATION must be requested with ACCESS_COARSE_LOCATION.
			locationPermissions = new String[] {
					permission.ACCESS_FINE_LOCATION,
					permission.ACCESS_COARSE_LOCATION
			};
		} else if (Build.VERSION_CODES.Q <= /*==*/ Build.VERSION.SDK_INT) {
			// Sad world, background location need to be requested too in order to update a widget.
			// > The system permissions dialog includes an option named "Allow all the time".
			locationPermissions = new String[] {
					permission.ACCESS_FINE_LOCATION,
					permission.ACCESS_COARSE_LOCATION,
					permission.ACCESS_BACKGROUND_LOCATION
			};
		} else /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)*/ {
			// Weird world, only FINE and COARSE to worry about, but to get FINE, one must include COARSE.
			// Android allows foreground and background for these.
			locationPermissions = new String[] {
					permission.ACCESS_FINE_LOCATION,
					permission.ACCESS_COARSE_LOCATION
			};
		}
		return locationPermissions;
	}

	public static @NonNull String[] calculateBackgroundPermissionsToRequest() {
		String[] locationPermissions;
		if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
			locationPermissions = new String[] {
					permission.ACCESS_BACKGROUND_LOCATION
			};
		} else /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)*/ {
			locationPermissions = new String[] {
					// none
			};
		}
		return locationPermissions;
	}
}
