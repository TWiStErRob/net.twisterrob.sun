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
						return LocationState.BACKGROUND_GRANTED;
					} else if (shouldBackgroundRationale()) {
						return LocationState.BACKGROUND_DENIED;
					}
					return LocationState.FINE_GRANTED;
				} else if (shouldFineRationale()) {
					return LocationState.FINE_DENIED;
				}
				return LocationState.COARSE_GRANTED;
			} else if (shouldCoarseRationale()) {
				return LocationState.COARSE_DENIED;
			}
			return LocationState.LOCATION_ENABLED;
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
}
